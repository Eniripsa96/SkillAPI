/**
 * SkillAPI
 * com.sucy.skill.tools.GUITool
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.tools;

import com.rit.sucy.config.CommentedConfig;
import com.rit.sucy.config.parse.DataSection;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.skills.Skill;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GUITool implements ToolMenu
{
    private static boolean inUse = false;

    public static boolean isInUse()
    {
        return inUse;
    }

    private static HashMap<String, GUIData> setups = new HashMap<String, GUIData>();

    private static CommentedConfig config;

    private static ItemStack
        NEXT,
        PREV,
        SHRINK,
        GROW,
        ADD_PAGE,
        DEL_PAGE,
        NEXT_CLASS,
        PREV_CLASS,
        NEXT_PROFESSION,
        PREV_PROFESSION,
        NEXT_SKILL,
        PREV_SKILL;

    private static RPGClass[] availableClasses;
    private static RPGClass[] availableProfesses;
    private static String[] availableGroups;

    private static void init()
    {
        NEXT = make(Material.BOOK, ChatColor.GOLD + "Next Menu");
        PREV = make(Material.BOOK, ChatColor.GOLD + "Previous Menu");
        SHRINK = make(Material.MELON_SEEDS, ChatColor.GOLD + "Shrink", "", "Removes a row from the GUI");
        GROW = make(Material.MELON, ChatColor.GOLD + "Grow", "", "Adds a row to the GUI");
        ADD_PAGE = make(Material.PAPER, ChatColor.GOLD + "Add Page", "", "Adds another page to the GUI", "right after the current one");
        DEL_PAGE = make(Material.PAPER, ChatColor.GOLD + "Delete Page", "", "Deletes the currently", "viewed page");
        NEXT_CLASS = make(Material.DIAMOND_SWORD, ChatColor.GOLD + "Next Class");
        PREV_CLASS = make(Material.IRON_SWORD, ChatColor.GOLD + "Previous Class");
        NEXT_PROFESSION = make(Material.DIAMOND_HOE, ChatColor.GOLD + "Next Profession");
        PREV_PROFESSION = make(Material.IRON_HOE, ChatColor.GOLD + "Previous Profession");

        availableClasses = SkillAPI.getClasses().values().toArray(new RPGClass[SkillAPI.getClasses().size()]);
        ArrayList<RPGClass> professes = new ArrayList<RPGClass>();
        ArrayList<String> groups = new ArrayList<String>();
        professes.add(null);
        for (RPGClass c : availableClasses)
        {
            if (c.hasParent() && !professes.contains(c.getParent()))
                professes.add(c.getParent());
            if (!groups.contains(c.getGroup()))
                groups.add(c.getGroup());
        }
        availableProfesses = professes.toArray(new RPGClass[professes.size()]);

        config = SkillAPI.getConfig("gui");
        DataSection data = config.getConfig();
        for (String key : data.keys())
        {
            setups.put(key, new GUIData(data.getSection(key)));
        }
    }

    public static boolean hasData(String key)
    {
        return setups.containsKey(key) && setups.get(key).isValid();
    }

    public static GUIData getActiveData()
    {
        String key = type.getPrefix();
        switch (type)
        {
            case CLASS_SELECTION:
                key += availableProfesses[professId];
                break;
            case CLASS_DETAILS:
                key = type.name();
                break;
            case SKILL_TREE:
                key += availableClasses[classId];
        }
        if (!setups.containsKey(key))
            setups.put(key, new GUIData());
        return setups.get(key);
    }

    private static ItemStack make(Material mat, String name, String... lore)
    {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    private static GUIType type;
    private static int classId;
    private static int professId;

    private final Player player;

    private InventoryData data;

    private Inventory inventory;

    private RPGClass rpgClass;
    private Skill    skill;
    private GUIData  guiData;

    private ItemStack[] playerContents;
    private ItemStack[] inventoryContents;

    public GUITool(Player player)
    {
        if (NEXT == null)
            init();

        this.player = player;
        playerContents = new ItemStack[36];
    }

    public void open()
    {
        if (data != null && !inUse)
        {
            this.data = new InventoryData(player);
            setType(GUIType.CLASS_SELECTION);
            inUse = true;
        }
    }

    public void setType(GUIType type)
    {
        GUITool.type = type;
        guiData = getActiveData();
        inventoryContents = new ItemStack[guiData.getSize()];
        player.openInventory(inventory);
        String title = populate();
        inventory = player.getServer().createInventory(this, guiData.getSize(), title);
    }

    private void update()
    {
        inventoryContents = inventory.getContents();
        guiData.load(inventoryContents);
    }

    private String populate()
    {
        player.getInventory().clear();
        playerContents = player.getInventory().getContents();
        inventory.clear();
        inventoryContents = inventory.getContents();

        playerContents[0] = PREV;
        playerContents[1] = NEXT;
        playerContents[2] = SHRINK;
        playerContents[3] = GROW;
        playerContents[4] = ADD_PAGE;
        playerContents[5] = DEL_PAGE;

        String name;

        switch (type)
        {
            case CLASS_DETAILS:
                name = populateClassDetails();
                break;
            case SKILL_TREE:
                name = populateSkillTree();
                break;
            default: // CLASS_SELECTION
                name = populateClassSelection();
                break;
        }

        player.getInventory().setContents(playerContents);

        return name;
    }

    private String populateClassSelection()
    {
        playerContents[7] = PREV_PROFESSION;
        playerContents[8] = NEXT_PROFESSION;

        GUIPage page = guiData.getPage();
        RPGClass profession = availableProfesses[professId];
        int i = 9;
        for (RPGClass c : availableClasses)
        {
            if (c.getParent() != profession)
                continue;

            int index = page.getIndex(c.getName());
            if (index == -1)
                playerContents[i++] = c.getToolIcon();
            else
                inventoryContents[index] = c.getToolIcon();
        }

        if (profession == null)
            return "GUI Editor - Class Selection";
        else
            return "GUI Editor - " + profession.getName() + " Subprofession";
    }

    private String populateClassDetails()
    {
        int i = 9;
        GUIPage page = guiData.getPage();
        for (String group : availableGroups)
        {
            int index = page.getIndex(group);
            ItemStack item = make(Material.DRAGON_EGG, group, "", "Spot for the player's current", "class in the group should", "be placed in the GUI");
            if (index == -1)
                playerContents[i++] = item;
            else
                inventoryContents[index] = item;
        }

        return "GUI Editor - Class Details";
    }

    private String populateSkillTree()
    {
        playerContents[7] = PREV_CLASS;
        playerContents[8] = NEXT_CLASS;

        RPGClass current = availableClasses[classId];
        GUIPage page = guiData.getPage();
        int i = 9;
        for (Skill skill : current.getSkills())
        {
            int index = page.getIndex(skill.getName());
            if (index == -1)
                playerContents[i++] = skill.getToolIndicator();
            else
                inventoryContents[index] = skill.getToolIndicator();
        }

        return "GUI Editor - " + availableClasses[classId] + " Skill Tree";
    }

    @Override
    public void handleClick(InventoryClickEvent event)
    {
        boolean top = event.getRawSlot() < event.getView().getTopInventory().getSize();
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
            || event.getAction() == InventoryAction.HOTBAR_SWAP
            || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD)
            event.setCancelled(true);
        else if (!top)
        {
            event.setCancelled(true);
            switch (event.getSlot())
            {
                case 0:
                    update();
                    setType(type.prev());
                    break;
                case 1:
                    update();
                    setType(type.next());
                    break;
                case 2:
                    guiData.shrink();
                    sett
            }
        }
    }

    @Override
    public void restore()
    {
        if (data != null)
        {
            update();
            data.restore(player);
            data = null;
            inUse = false;
        }
    }

    @Override
    public Inventory getInventory()
    {
        return inventory;
    }
}
