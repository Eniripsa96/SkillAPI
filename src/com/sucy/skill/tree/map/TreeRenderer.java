package com.sucy.skill.tree.map;

import com.rit.sucy.config.Config;
import com.rit.sucy.gui.MapFont;
import com.rit.sucy.gui.MapImage;
import com.rit.sucy.reflect.Reflection;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.manager.ResourceManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

/**
 * Renders a skill tree to a map canvas
 */
public class TreeRenderer extends MapRenderer
{
    // The singleton instance of the renderer
    public static final TreeRenderer RENDERER = new TreeRenderer();

    // Config values
    private static final String FONT_1  = "list-font-family";
    private static final String SIZE_1  = "list-font-size";
    private static final String STYLE_1 = "list-font-style";
    private static final String SPACE_1 = "list-font-spacing";
    private static final String FONT_2  = "detail-font-family";
    private static final String SIZE_2  = "detail-font-size";
    private static final String STYLE_2 = "detail-font-style";
    private static final String SPACE_2 = "detail-font-spacing";
    private static final String COLOR   = "font-color";

    // Color code conversion
    private static final HashMap<Character, Byte> colors = new HashMap<Character, Byte>()
    {{
            put('0', MapImage.matchColor(new Color(0, 0, 0)));
            put('1', MapImage.matchColor(new Color(0, 0, 190)));
            put('2', MapImage.matchColor(new Color(0, 190, 0)));
            put('3', MapImage.matchColor(new Color(0, 190, 190)));
            put('4', MapImage.matchColor(new Color(190, 0, 0)));
            put('5', MapImage.matchColor(new Color(190, 0, 190)));
            put('6', MapImage.matchColor(new Color(217, 163, 52)));
            put('7', MapImage.matchColor(new Color(190, 190, 190)));
            put('8', MapImage.matchColor(new Color(63, 63, 63)));
            put('9', MapImage.matchColor(new Color(63, 63, 254)));
            put('a', MapImage.matchColor(new Color(63, 254, 63)));
            put('b', MapImage.matchColor(new Color(63, 254, 254)));
            put('c', MapImage.matchColor(new Color(254, 63, 63)));
            put('d', MapImage.matchColor(new Color(254, 63, 254)));
            put('e', MapImage.matchColor(new Color(254, 254, 63)));
            put('f', MapImage.matchColor(new Color(255, 255, 255)));
        }};

    // Size constants
    private static final int CELLS = 4;

    // Defaults
    private static final String defaultFont   = "Tahoma";
    private static final int    defaultStyle  = Font.BOLD;
    private static final int    defaultSize   = 12;
    private static final String defaultFont2  = "Tahoma";
    private static final int    defaultStyle2 = Font.PLAIN;
    private static final int    defaultSize2  = 9;

    // Buffer to draw to before applying it to the map
    private MapImage      mapImage = new MapImage(128, 128);

    // Positional data for each player as they browse the tree
    private class ScrollData
    {
        public List<PlayerSkill> skills = new ArrayList<PlayerSkill>();
        public int id, scroll, height, button, page, smooth;
        public Skill skill;
    }

    // The active data for players looking at their skill trees
    private HashMap<String, ScrollData> scrollData = new HashMap<String, ScrollData>();

    // Represents a single loaded scheme with its font details and images
    private class Scheme
    {
        private String name;

        private MapImage u0, u1, d0, d1, b0, b1, m0, m1, bg, np, tl, sr;
        private MapFont lFont, dFont;
        private byte c;
    }

    // The loaded schemes
    public HashMap<String, Scheme> schemes = new HashMap<String, Scheme>();

    // The default scheme to use if a player doesn't have a valid one selected
    private Scheme defaultScheme;

    // The view of the map
    public  ItemStack map;
    public  MapView   view;
    private short     viewId;
    private Object    worldMap;
    private Method flagDirty;
    private int minX, maxX, minY, maxY, last;

    /**
     * A private constructor is used to prevent further
     * instantiation other than the one needed instance
     */
    private TreeRenderer()
    {
        load();

        try
        {
            Plugin api = Bukkit.getPluginManager().getPlugin("SkillAPI");
            view = api.getServer().getMap(viewId);
            if (view == null)
            {
                view = api.getServer().createMap(api.getServer().getWorlds().get(0));
                viewId = view.getId();
            }
            map = new ItemStack(Material.MAP, 1, viewId);
            ItemMeta meta = map.getItemMeta();
            meta.setDisplayName("Skill Tree");
            map.setItemMeta(meta);
            for (MapRenderer r : view.getRenderers())
            {
                view.removeRenderer(r);
            }
            view.addRenderer(this);

            worldMap = Reflection.getValue(view, "worldMap");
            flagDirty = Reflection.getMethod(worldMap, "flagDirty", int.class, int.class);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Moves the player's current button selection to the left
     * if they are on the skill details screen.
     *
     * @param player player to move the selection for
     */
    public void moveLeft(Player player)
    {
        moveDetails(player, -1);
    }

    /**
     * Moves the player's current button selection to the right
     * if they are on the skill details screen.
     *
     * @param player player to move the selection for
     */
    public void moveRight(Player player)
    {
        moveDetails(player, 1);
    }

    /**
     * Handles moving a player's current button selection
     * by making sure they are at the right screen and that
     * there is room to move the selection in the given direction.
     *
     * @param player player to move the selection for
     * @param i      amount to shift the selection by
     */
    private void moveDetails(Player player, int i)
    {
        init(player);
        ScrollData data = scrollData.get(player.getName());
        if (data.skill == null) return;

        data.button += i;
        boolean down = SkillAPI.getSettings().isAllowDowngrade();
        if (data.button < 0) data.button = 0;
        if (data.button == 2 && !down) data.button += i;
        if (data.button > 3) data.button = 3;
    }

    /**
     * Moves the player's skill selection up if
     * they are on the skill list screen.
     *
     * @param player player to move the selection for
     */
    public void moveUp(Player player)
    {
        moveList(player, -1);
    }

    /**
     * Moves the player's skill selection down if
     * they are on the skill list screen.
     *
     * @param player player to move the selection for
     */
    public void moveDown(Player player)
    {
        moveList(player, 1);
    }

    /**
     * Moves a player's skill selection by making
     * sure they are at the correct screen and that
     * there is room to move the selection in the
     * given direction.
     *
     * @param player player to move the selection for
     * @param i      amount to move the selection by
     */
    private void moveList(Player player, int i)
    {
        MapTree tree = getTree(player);
        if (tree == null) return;

        init(player);
        ScrollData data = scrollData.get(player.getName());
        if (data.skill != null) return;

        data.id += i;
        if (data.id < 0) data.id = 0;
        if (data.id >= data.height) data.id = data.height - 1;

        if (data.id - data.scroll == 0 && data.id > 0)
        {
            data.scroll--;
            data.smooth = 20;
        }
        if (data.id - data.scroll == CELLS - 1 && data.id < data.height - 1)
        {
            data.scroll++;
            data.smooth = -20;
        }
    }

    /**
     * Selects the player's currently hovered option in
     * the menu, moving between screens or calling API
     * functions when applicable.
     *
     * @param player player to select the hovered option for
     */
    public void select(Player player)
    {
        init(player);
        ScrollData data = scrollData.get(player.getName());

        // Selecting a skill for more details
        if (data.skill == null)
        {
            MapTree tree = getTree(player);
            int i = 0;
            for (PlayerSkill skill : data.skills)
            {
                if (i++ == data.id)
                {
                    data.skill = skill.getData();
                    data.page = 0;
                    data.button = 0;
                    return;
                }
            }
        }

        // Back button
        else if (data.button == 0)
        {
            data.skill = null;
        }

        // Upgrade button
        else if (data.button == 1)
        {
            SkillAPI.getPlayerData(player).upgradeSkill(data.skill);
        }

        // Downgrade button
        else if (data.button == 2)
        {
            SkillAPI.getPlayerData(player).downgradeSkill(data.skill);
        }

        // More info button
        else
        {
            data.page++;
        }
    }

    /**
     * Retrieves the MapTree for the player. If MapTrees are not
     * enabled or if the player doesn't have a class, this will
     * instead return null.
     *
     * @param player player to get the MapTree for
     * @return the player's MapTree or null if not found
     */
    public MapTree getTree(Player player)
    {
        PlayerClass c = SkillAPI.getPlayerData(player).getMainClass();
        if (c != null && c.getData().getSkillTree() instanceof MapTree)
        {
            return (MapTree) c.getData().getSkillTree();
        }
        return null;
    }

    /**
     * Retrieves the active scheme for a player. If their scheme is not
     * a valid one, this returns the default scheme instead.
     *
     * @param player player to get the scheme for
     * @return scheme of the player
     */
    public Scheme getScheme(Player player)
    {
        Scheme scheme = schemes.get(SkillAPI.getPlayerData(player).getScheme());
        if (scheme == null) return defaultScheme;
        return scheme;
    }

    /**
     * Gets the hovered skill for a player
     *
     * @param player player to get the hovered skill for
     * @return hovered skill
     */
    public PlayerSkill getSkill(Player player)
    {
        init(player);
        ScrollData data = scrollData.get(player.getName());
        if (data.skill != null) return SkillAPI.getPlayerData(player).getSkill(data.skill.getName());
        else return data.skills.get(data.id);
    }

    /**
     * Checks whether or not the player is holding the skill tree map
     *
     * @param player player to check
     * @return true if held, false otherwise
     */
    public boolean isHeld(Player player)
    {
        return player != null && player.getItemInHand() != null && player.getItemInHand().getType() == Material.MAP && player.getItemInHand().getDurability() == view.getId();
    }

    /**
     * Initializes scroll data for the player if it isn't there already
     *
     * @param player player to init the data for
     */
    private void init(Player player)
    {
        MapTree tree = getTree(player);
        if (!scrollData.containsKey(player.getName()) && tree != null)
        {
            ScrollData data = new ScrollData();
            scrollData.put(player.getName(), data);

            PlayerData playerData = SkillAPI.getPlayerData(player);
            for (PlayerSkill skill : playerData.getSkills())
            {
                if (tree.canShow(player, skill.getData()))
                {
                    data.skills.add(skill);
                    data.height++;
                }
            }
            Collections.sort(data.skills, MAP_COMPARATOR);
        }
    }

    /**
     * Clears the data for the given player
     *
     * @param player player to clear for
     */
    public void clearData(Player player)
    {
        scrollData.remove(player.getName());
    }

    private boolean first = false;

    /**
     * Renders the skill tree to the map
     *
     * @param mapView   view the canvas is used by
     * @param mapCanvas canvas to draw to
     * @param player    player holding the map
     */
    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player)
    {
        // No need to render when not visible
        if (!isHeld(player)) return;

        // Prevent it from drawing one vertical line at a time
        first = !first;
        if (first)
        {
            player.sendMap(mapView);
            return;
        }

        MapTree tree = getTree(player);
        init(player);

        // When a player shouldn't be seeing the map skill tree, draw the SkillAPI logo instead
        int id;
        if (tree == null || !SkillAPI.getSettings().isMapTreeEnabled())
        {
            id = drawDefault();
        }

        // When looking at a specific skill, draw the details
        else if (scrollData.get(player.getName()).skill != null)
        {
            id = drawDetails(player);
        }

        // When not looking at a specific skill, draw the arrangement
        else
        {
            id = drawSkillList(player);
        }

        // Refresh entire map when changing screens
        if (id != last) {
            last = id;
            minX = minY = 0;
            maxX = maxY = 127;
        }

        // Try a slightly faster way of setting the data
        boolean fast = false;
        if (flagDirty != null)
        {
            try
            {
                Reflection.setValue(mapCanvas, "buffer", mapImage.getData());
                flagDirty.invoke(worldMap, minX, minY);
                flagDirty.invoke(worldMap, maxX, maxY);
                fast = true;
            }
            catch (Exception ex) { /* Didn't work */ }
        }

        // Otherwise use the tried and true method
        if (!fast)
        {
            byte[] data = mapImage.getData();
            for (int i = 0; i < data.length; i++)
            {
                int x = i & 127;
                int y = i >> 7;

                mapCanvas.setPixel(x, y, data[i]);
            }
        }
    }

    /**
     * Draws the default screen when a player doesn't have a skill tree
     */
    private int drawDefault()
    {
        mapImage.drawImg(defaultScheme.bg, 0, 0);
        mapImage.drawImg(defaultScheme.tl, 0, 0);
        mapImage.drawString(defaultScheme.lFont, defaultScheme.c, "Developed By", 10, 50);
        mapImage.drawString(defaultScheme.lFont, defaultScheme.c, "Eniripsa96", 10, 70);

        minX = 0;
        maxX = 0;
        minY = 1;
        maxY = 1;

        return 0;
    }

    /**
     * Draws an individual skill's details for the player
     *
     * @param player player to draw the details for
     */
    private int drawDetails(Player player)
    {
        ScrollData data = scrollData.get(player.getName());
        Scheme scheme = getScheme(player);

        // Background
        mapImage.drawImg(scheme.bg, 0, 0);

        // Get text to draw
        ItemStack icon = data.skill.getIndicator(SkillAPI.getPlayerData(player).getSkill(data.skill.getName()));
        List<String> lore = icon.getItemMeta().getLore();
        lore.add(0, icon.getItemMeta().getDisplayName());

        // Find page details
        int linesPerPage = 90 / (scheme.dFont.getFont().getSize() + 3);
        int pages = (lore.size() + linesPerPage - 1) / linesPerPage;
        if (data.page >= pages) data.page -= pages;

        // Draw the text
        int y = scheme.dFont.getFont().getSize() + 5;
        int start = linesPerPage * data.page;
        byte color = scheme.c;
        for (int i = start; i < start + linesPerPage && i < lore.size(); i++)
        {
            String line = lore.get(i);
            int first = 0;
            int next = line.indexOf(ChatColor.COLOR_CHAR);
            int x = 7;
            while (next >= 0)
            {
                if (first != next)
                {
                    String part = line.substring(first, next);
                    x = mapImage.drawString(scheme.dFont, color, part, x, y);
                }
                first = next + 2;
                char c = line.charAt(next + 1);
                if (colors.containsKey(c))
                {
                    color = colors.get(c);
                }
                else if (c == 'r')
                {
                    color = scheme.c;
                }
                next = line.indexOf(ChatColor.COLOR_CHAR, first);
            }
            if (first < line.length()) mapImage.drawString(scheme.dFont, color, line.substring(first), x, y);
            y += scheme.dFont.getFont().getSize() + 3;
        }

        // Get position for upgrade arrow
        boolean down = SkillAPI.getSettings().isAllowDowngrade();
        int x;
        if (down) x = 42;
        else x = 56;

        // Draw buttons
        mapImage.drawImg(data.button == 0 ? scheme.b1 : scheme.b0, 6, 95);
        mapImage.drawImg(data.button == 1 ? scheme.u1 : scheme.u0, x, 95);
        if (down) mapImage.drawImg(data.button == 2 ? scheme.d1 : scheme.d0, 68, 95);
        mapImage.drawImg(data.button == 3 ? scheme.m1 : scheme.m0, 90, 95);

        minX = 6;
        maxX = 127;
        minY = 5;
        maxY = 127;

        return 1;
    }

    /**
     * Draws the skill list for the given player
     *
     * @param player player to draw for
     */
    private int drawSkillList(Player player)
    {
        MapTree mapTree = getTree(player);
        ScrollData data = scrollData.get(player.getName());
        Scheme scheme = getScheme(player);

        // Clear the buffer and apply the scheme font
        mapImage.clear();

        if (data.smooth < 0) data.smooth += 2;
        if (data.smooth > 0) data.smooth -= 2;

        // Draw skill list
        for (int i = Math.max(0, data.scroll - 1); i < data.scroll + CELLS + 1 && i < data.skills.size(); i++)
        {
            Skill skill = data.skills.get(i).getData();
            int y = (i - data.scroll) * 20 + 36 - data.smooth;
            mapImage.drawImg(scheme.np, 0, y);
            if (data.id == i) mapImage.drawImg(scheme.sr, 6, y + 5);
            mapImage.drawString(scheme.lFont, scheme.c, skill.getName(), 30, y + 14);
        }

        mapImage.drawImg(scheme.tl, 0, 0);

        minX = 0;
        maxX = 127;
        minY = 30;
        maxY = 127;

        return 2;
    }

    /**
     * Loads the available schemes from the img folder
     */
    private void load()
    {
        // Copy default scheme files over
        ResourceManager.copyResource("back0.png", ResourceManager.SCHEME_FOLDER);
        ResourceManager.copyResource("back1.png", ResourceManager.SCHEME_FOLDER);
        ResourceManager.copyResource("up0.png", ResourceManager.SCHEME_FOLDER);
        ResourceManager.copyResource("up1.png", ResourceManager.SCHEME_FOLDER);
        ResourceManager.copyResource("down0.png", ResourceManager.SCHEME_FOLDER);
        ResourceManager.copyResource("down1.png", ResourceManager.SCHEME_FOLDER);
        ResourceManager.copyResource("more0.png", ResourceManager.SCHEME_FOLDER);
        ResourceManager.copyResource("more1.png", ResourceManager.SCHEME_FOLDER);
        ResourceManager.copyResource("background.png", ResourceManager.SCHEME_FOLDER);
        ResourceManager.copyResource("selector.png", ResourceManager.SCHEME_FOLDER);
        ResourceManager.copyResource("nameplate.png", ResourceManager.SCHEME_FOLDER);
        ResourceManager.copyResource("title.png", ResourceManager.SCHEME_FOLDER);

        SkillAPI api = (SkillAPI) Bukkit.getServer().getPluginManager().getPlugin("SkillAPI");
        ConfigurationSection config = new Config(api, "map").getConfig();
        viewId = (short)config.getInt("VIEW_ID", viewId);
        try
        {
            // Load menu schemes
            File[] files = new File(api.getDataFolder(), "img").listFiles();
            for (File file : files)
            {
                try
                {
                    String name = file.getName();
                    Scheme scheme = new Scheme();
                    scheme.name = name;

                    // Pre-processed images
                    scheme.b0 = new MapImage(new File(file, "back0.png"));
                    scheme.b1 = new MapImage(new File(file, "back1.png"));
                    scheme.u0 = new MapImage(new File(file, "up0.png"));
                    scheme.u1 = new MapImage(new File(file, "up1.png"));
                    scheme.d0 = new MapImage(new File(file, "down0.png"));
                    scheme.d1 = new MapImage(new File(file, "down1.png"));
                    scheme.m0 = new MapImage(new File(file, "more0.png"));
                    scheme.m1 = new MapImage(new File(file, "more1.png"));
                    scheme.bg = new MapImage(new File(file, "background.png"));
                    scheme.np = new MapImage(new File(file, "nameplate.png"));
                    scheme.tl = new MapImage(new File(file, "title.png"));
                    scheme.sr = new MapImage(new File(file, "selector.png"));

                    // Load font data
                    scheme.c = MapImage.matchColor(new Color(255, 255, 255));
                    String font1 = defaultFont;
                    String font2 = defaultFont2;
                    int size1 = defaultSize;
                    int size2 = defaultSize2;
                    int style1 = defaultStyle;
                    int style2 = defaultStyle2;
                    int spacing1 = 2;
                    int spacing2 = 1;
                    if (config.contains(name))
                    {
                        ConfigurationSection data = config.getConfigurationSection(name);
                        font1 = data.getString(FONT_1, defaultFont);
                        size1 = data.getInt(SIZE_1, defaultSize);
                        style1 = data.getInt(STYLE_1, defaultStyle);
                        font2 = data.getString(FONT_2, defaultFont2);
                        size2 = data.getInt(SIZE_2, defaultSize2);
                        style2 = data.getInt(STYLE_2, defaultStyle2);
                        spacing1 = data.getInt(SPACE_1, spacing1);
                        spacing2 = data.getInt(SPACE_2, spacing2);
                        scheme.c = MapImage.matchColor(Color.decode(data.getString(COLOR, "0XFFFFFF")));
                    }
                    Font listFont = new Font(font1, style1, size1);
                    Font detailFont = new Font(font2, style2, size2);

                    // Map-efficient fonts
                    scheme.dFont = new MapFont(detailFont, spacing1);
                    scheme.lFont = new MapFont(listFont, spacing2);

                    schemes.put(name.toLowerCase(), scheme);

                    if (scheme.name.equals("default") || defaultScheme == null) defaultScheme = scheme;
                }
                catch (Exception ex)
                {
                    Bukkit.getLogger().info("Invalid scheme: " + file.getName());
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Saves the scheme data to the map file
     *
     * @param api API reference
     */
    public void save(SkillAPI api)
    {
        Config file = new Config(api, "map");
        file.clear();
        ConfigurationSection config = file.getConfig();
        config.set("VIEW_ID", viewId);
        for (Map.Entry<String, Scheme> entry : schemes.entrySet())
        {
            ConfigurationSection scheme = config.createSection(entry.getKey());
            Scheme s = entry.getValue();
            scheme.set(FONT_1, s.lFont.getFont().getFamily());
            scheme.set(SIZE_1, s.lFont.getFont().getSize());
            scheme.set(STYLE_1, s.lFont.getFont().getStyle());
            scheme.set(FONT_2, s.dFont.getFont().getFamily());
            scheme.set(SIZE_2, s.dFont.getFont().getSize());
            scheme.set(STYLE_2, s.dFont.getFont().getStyle());
            scheme.set(COLOR, "0X" + Integer.toHexString(MapImage.getColor(s.c).getRGB()).substring(2));
        }
        file.save();
    }

    /**
     * Comparator for skills for level trees
     */
    private static final Comparator<PlayerSkill> MAP_COMPARATOR = new Comparator<PlayerSkill>()
    {

        /**
         * Compares skills based on their stats for skill tree arrangement
         *  -> Skills with lower level requirements come first
         *  -> Then its skills alphabetically
         *
         * @param p1 skill being compared
         * @param p2 skill to compare to
         * @return   -1, 0, or 1
         */
        @Override
        public int compare(PlayerSkill p1, PlayerSkill p2)
        {
            Skill skill1 = p1.getData();
            Skill skill2 = p2.getData();
            return skill1.getLevelReq(0) > skill2.getLevelReq(0) ? 1
                    : skill1.getLevelReq(0) < skill2.getLevelReq(0) ? -1
                    : skill1.getName().compareTo(skill2.getName());
        }
    };
}
