package com.sucy.skill.data;

import com.rit.sucy.config.Config;
import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.SkillAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>The management class for SkillAPI's config.yml settings.</p>
 */
public class Settings
{

    private HashMap<String, GroupSettings> groups = new HashMap<String, GroupSettings>();

    private SkillAPI             plugin;
    private ConfigurationSection config;

    /**
     * <p>Initializes a new settings manager.</p>
     * <p>This is already set up by SkillAPI and shouldn't be
     * instantiated elsewhere. Instead, get it from SkillAPI
     * using the SkillAPI.getSettings() method.</p>
     *
     * @param plugin SkillAPI plugin reference
     */
    public Settings(SkillAPI plugin)
    {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();
        config = plugin.getConfig();
        reload();
    }

    /**
     * <p>Reloads the settings from SkillAPI's config.yml file.</p>
     * <p>This will fill in any missing values with default values
     * and trim any values that aren't supposed to be there.</p>
     */
    public void reload()
    {
        plugin.reloadConfig();
        Config.trim(config);
        Config.setDefaults(config);
        plugin.saveConfig();

        loadAccountSettings();
        loadGroupSettings();
        loadClassSettings();
        loadManaSettings();
        loadSkillSettings();
        loadItemSettings();
        loadGUISettings();
        loadExpSettings();
        loadSkillBarSettings();
        loadLoggingSettings();
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                  Group Settings                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    private void loadGroupSettings()
    {
        Config file = new Config(plugin, "groups");
        ConfigurationSection config = file.getConfig();
        groups.clear();

        for (String key : config.getKeys(false))
        {
            groups.put(key.toLowerCase(), new GroupSettings(config.getConfigurationSection(key)));
        }
    }

    /**
     * Retrieves the settings for a class group
     *
     * @param group name of the group to retrieve the settings for
     *
     * @return settings for the class group
     */
    public GroupSettings getGroupSettings(String group)
    {
        if (!groups.containsKey(group.toLowerCase()))
        {
            return new GroupSettings();
        }
        return groups.get(group.toLowerCase());
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                 Account Settings                  //
    //                                                   //
    ///////////////////////////////////////////////////////

    private static final String ACCOUNT_BASE = "Accounts.";
    private static final String ACCOUNT_MAIN = ACCOUNT_BASE + "main-class-group";
    private static final String ACCOUNT_EACH = ACCOUNT_BASE + "one-per-class";
    private static final String ACCOUNT_MAX  = ACCOUNT_BASE + "max-accounts";
    private static final String ACCOUNT_PERM = ACCOUNT_BASE + "perm-accounts";

    private String  mainGroup;
    private boolean onePerClass;
    private int     maxAccounts;

    private HashMap<String, Integer> permAccounts = new HashMap<String, Integer>();

    /**
     * Retrieves the main class group for displaying prefixes
     * or showing account information
     *
     * @return main class group
     */
    public String getMainGroup()
    {
        return mainGroup;
    }

    /**
     * Retrieves whether or not accounts should be initialized with
     * one file per class.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isOnePerClass()
    {
        return onePerClass;
    }

    /**
     * Retrieves the max accounts allowed for most players
     *
     * @return max accounts allowed for most players
     */
    public int getMaxAccounts()
    {
        return maxAccounts;
    }

    /**
     * Retrieves the max amount of accounts allowed for a specific player
     * by checking permissions for additional accounts.
     *
     * @param player player to check the max allowed accounts for
     *
     * @return number of allowed accounts
     */
    public int getMaxAccounts(Player player)
    {
        if (player == null)
        {
            return maxAccounts;
        }
        int max = maxAccounts;
        for (Map.Entry<String, Integer> entry : permAccounts.entrySet())
        {
            if (player.hasPermission(entry.getKey()))
            {
                max = Math.max(max, entry.getValue());
            }
        }
        return max;
    }

    private void loadAccountSettings()
    {
        mainGroup = config.getString(ACCOUNT_MAIN);
        onePerClass = config.getBoolean(ACCOUNT_EACH);
        maxAccounts = config.getInt(ACCOUNT_MAX);

        // Permission account amounts
        List<String> list = config.getStringList(ACCOUNT_PERM);
        for (String item : list)
        {
            if (!item.contains(":"))
            {
                continue;
            }

            String[] pieces = item.split(":");
            if (pieces.length != 2)
            {
                continue;
            }

            try
            {
                permAccounts.put(pieces[0], Integer.parseInt(pieces[1]));
            }
            catch (Exception ex)
            {
                // Invalid setting value
            }
        }
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                  Class Settings                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    private static final String CLASS_BASE = "Classes.";
    private static final String CLASS_HP   = CLASS_BASE + "classless-hp";
    private static final String CLASS_SHOW = CLASS_BASE + "show-auto-skills";

    private int     defaultHealth;
    private boolean showAutoSkills;

    /**
     * <p>Retrieves the default health for players that do not have a class.</p>
     *
     * @return default health for classless players
     */
    public int getDefaultHealth()
    {
        return defaultHealth;
    }

    /**
     * Checks whether or not auto-leveled skills are to be shown.
     *
     * @return true if shown, false otherwise
     */
    public boolean isShowingAutoSkills()
    {
        return showAutoSkills;
    }

    private void loadClassSettings()
    {
        defaultHealth = config.getInt(CLASS_HP);
        showAutoSkills = config.getBoolean(CLASS_SHOW);
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   Mana Settings                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    private static final String MANA_BASE    = "Mana.";
    private static final String MANA_ENABLED = MANA_BASE + "enabled";
    private static final String MANA_FREQ    = MANA_BASE + "freq";

    private boolean manaEnabled;
    private int     gainFreq;

    /**
     * Checks whether or not mana is enabled
     *
     * @return true if enabled, false otherwise
     */
    public boolean isManaEnabled()
    {
        return manaEnabled;
    }

    /**
     * Retrieves the frequency of mana gain
     *
     * @return the frequency of mana gain
     */
    public int getGainFreq()
    {
        return gainFreq;
    }

    private void loadManaSettings()
    {
        manaEnabled = config.getBoolean(MANA_ENABLED);
        gainFreq = (int)(config.getDouble(MANA_FREQ) * 20);
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                  Skill Settings                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    private static final String SKILL_BASE      = "Skills.";
    private static final String SKILL_TYPE      = SKILL_BASE + "tree-type";
    private static final String SKILL_DOWNGRADE = SKILL_BASE + "allow-downgrade";
    private static final String SKILL_MESSAGE   = SKILL_BASE + "show-messages";
    private static final String SKILL_RADIUS    = SKILL_BASE + "message-radius";

    private boolean allowDowngrade;
    private boolean showSkillMessages;
    private int     messageRadius;

    /**
     * Checks whether or not downgrades are allowed
     *
     * @return true if allowed, false otherwise
     */
    public boolean isAllowDowngrade()
    {
        return allowDowngrade;
    }

    /**
     * Checks whether or not skill messages are enabled
     *
     * @return true if enabled, false otherwise
     */
    public boolean isShowSkillMessages()
    {
        return showSkillMessages;
    }

    /**
     * Gets the radius in which skill messages are sent out
     *
     * @return skill message radius
     */
    public int getMessageRadius()
    {
        return messageRadius;
    }

    private void loadSkillSettings()
    {
        allowDowngrade = config.getBoolean(SKILL_DOWNGRADE);
        showSkillMessages = config.getBoolean(SKILL_MESSAGE);
        messageRadius = config.getInt(SKILL_RADIUS);
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   Item Settings                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    private static final String ITEM_BASE   = "Items.";
    private static final String ITEM_LORE   = ITEM_BASE + "lore-requirements";
    private static final String ITEM_DAMAGE = ITEM_BASE + "default-one-damage";
    private static final String ITEM_CHECK  = ITEM_BASE + "players-per-check";

    private boolean checkLore;
    private boolean defaultOneDamage;
    private int     playersPerCheck;

    /**
     * Checks whether or not lore requirements are enabled
     *
     * @return true if enabled, false otherwise
     */
    public boolean isCheckLore()
    {
        return checkLore;
    }

    /**
     * Checks whether or not items are defaulted to one damage when unknown
     *
     * @return true if enabled, false otherwise
     */
    public boolean isDefaultOneDamage()
    {
        return defaultOneDamage;
    }

    /**
     * Retrieves the number of players checked each update
     *
     * @return number of players checked each update
     */
    public int getPlayersPerCheck()
    {
        return playersPerCheck;
    }

    /**
     * Sets whether or not to default unknown items to one damage
     *
     * @param oneDamage whether or not to default unknown items to one damage
     */
    public void setDefaultOneDamage(boolean oneDamage)
    {
        defaultOneDamage = oneDamage;
        config.set(ITEM_DAMAGE, oneDamage);
        plugin.saveConfig();
    }

    /**
     * Sets the number of players to check each update
     *
     * @param players players to check each update
     */
    public void setPlayersPerCheck(int players)
    {
        playersPerCheck = players;
        config.set(ITEM_CHECK, players);
        plugin.saveConfig();
    }

    private void loadItemSettings()
    {
        checkLore = config.getBoolean(ITEM_LORE);
        defaultOneDamage = config.getBoolean(ITEM_DAMAGE);
        playersPerCheck = config.getInt(ITEM_CHECK);
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   GUI Settings                    //
    //                                                   //
    ///////////////////////////////////////////////////////

    private static final String GUI_BASE  = "GUI.";
    private static final String GUI_OLD   = GUI_BASE + "old-health-bar";
    private static final String GUI_BAR   = GUI_BASE + "use-level-bar";
    private static final String GUI_BOARD = GUI_BASE + "scoreboard-enabled";
    private static final String GUI_NAME  = GUI_BASE + "show-class-name";
    private static final String GUI_LEVEL = GUI_BASE + "show-class-level";

    private boolean oldHealth;
    private boolean useLevelBar;
    private boolean showScoreboard;
    private boolean showClassName;
    private boolean showClassLevel;

    /**
     * Checks whether or not old health bars (fixed 10 hearts) are enabled
     *
     * @return true if enabled, false otherwise
     */
    public boolean isOldHealth()
    {
        return oldHealth;
    }

    /**
     * Checks whether or not the level bar is to be used for class level
     *
     * @return true if enabled, false otherwise
     */
    public boolean isUseLevelBar()
    {
        return useLevelBar;
    }

    /**
     * Checks whether or not the stats scoreboard is to be shown
     *
     * @return true if shown, false otherwise
     */
    public boolean isShowScoreboard()
    {
        return showScoreboard;
    }

    /**
     * Checks whether or not a player's class name is to be
     * shown next to their name
     *
     * @return true if shown, false otherwise
     */
    public boolean isShowClassName()
    {
        return showClassName;
    }

    /**
     * Checks whether or not a player's class level is to be
     * shown below their name
     *
     * @return true if shown, false otherwise
     */
    public boolean isShowClassLevel()
    {
        return showClassLevel;
    }

    private void loadGUISettings()
    {
        oldHealth = config.getBoolean(GUI_OLD);
        useLevelBar = config.getBoolean(GUI_BAR);
        showScoreboard = config.getBoolean(GUI_BOARD);
        showClassName = config.getBoolean(GUI_NAME);
        showClassLevel = config.getBoolean(GUI_LEVEL);
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //               Click Combo Settings                //
    //                                                   //
    ///////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////
    //                                                   //
    //                   Exp Settings                    //
    //                                                   //
    ///////////////////////////////////////////////////////

    private final HashMap<String, Double> yields = new HashMap<String, Double>();

    private ExpFormula expFormula;
    private boolean    useOrbs;
    private boolean    blockSpawner;
    private boolean    blockEgg;
    private boolean    blockCreative;
    private boolean    showExpMessages;
    private boolean    showLevelMessages;

    /**
     * Gets the required amount of experience at a given level
     *
     * @param level level of the class
     * @return required experience to gain a level
     */
    public int getRequiredExp(int level)
    {
        return expFormula.calculate(level);
    }

    /**
     * Gets the experience yield of a mob
     *
     * @param mob mob to get the yield of
     * @return experience yield
     */
    public double getYield(String mob)
    {
        mob = mob.toLowerCase();
        if (!yields.containsKey(mob))
        {
            return 0;
        }
        else
        {
            return yields.get(mob);
        }
    }

    /**
     * Checks whether or not experience is to be gained through
     * vanilla experience orbs
     *
     * @return true if enabled, false otherwise
     */
    public boolean isUseOrbs()
    {
        return useOrbs;
    }

    /**
     * Checks whether or not experience from mobs spawned
     * via a mob spawner is to be blocked.
     *
     * @return true if blocked, false otherwise
     */
    public boolean isBlockSpawner()
    {
        return blockSpawner;
    }

    /**
     * Checks whether or not experience from mobs spawned
     * via eggs are to be blocked
     *
     * @return true if blocked, false otherwise
     */
    public boolean isBlockEgg()
    {
        return blockEgg;
    }

    /**
     * Checks whether or not players in creative mode
     * are blocked from receiving experience.
     *
     * @return true if blocked, false otherwise
     */
    public boolean isBlockCreative()
    {
        return blockCreative;
    }

    /**
     * Checks whether or not messages should
     * be displayed when a player gains experience
     *
     * @return true if enabled, false otherwise
     */
    public boolean isShowExpMessages()
    {
        return showExpMessages;
    }

    /**
     * Checks whether or not messages should be displayed
     * when a player gains a level
     *
     * @return true if enabled, false otherwise
     */
    public boolean isShowLevelMessages()
    {
        return showLevelMessages;
    }

    private static final String EXP_BASE = "Experience.";

    private void loadExpSettings()
    {
        this.useOrbs = config.getBoolean(EXP_BASE + "use-exp-orbs");
        this.blockSpawner = config.getBoolean(EXP_BASE + "block-mob-spawner");
        this.blockEgg = config.getBoolean(EXP_BASE + "block-mob-egg");
        this.blockCreative = config.getBoolean(EXP_BASE + "block-creative");
        this.showExpMessages = config.getBoolean(EXP_BASE + "exp-message-enabled");
        this.showLevelMessages = config.getBoolean(EXP_BASE + "level-message-enabled");

        ConfigurationSection formula = config.getConfigurationSection(EXP_BASE + "formula");
        int x = formula.getInt("x");
        int y = formula.getInt("y");
        int z = formula.getInt("z");
        expFormula = new ExpFormula(x, y, z);

        ConfigurationSection yields = config.getConfigurationSection(EXP_BASE + "yields");
        this.yields.clear();
        for (String key : yields.getKeys(false))
        {
            this.yields.put(key, yields.getDouble(key));
        }
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                Skill Bar Settings                 //
    //                                                   //
    ///////////////////////////////////////////////////////

    private boolean   skillBarEnabled;
    private boolean   skillBarCooldowns;
    private ItemStack unassigned;
    private boolean[] defaultBarLayout = new boolean[9];
    private boolean[] lockedSlots      = new boolean[9];

    /**
     * Checks whether or not the skill bar is enabled
     *
     * @return true if enabled, false otherwise
     */
    public boolean isSkillBarEnabled()
    {
        return skillBarEnabled;
    }

    /**
     * Checks whether or not the skill bar is to display cooldowns
     *
     * @return true if enabled, false otherwise
     */
    public boolean isSkillBarCooldowns()
    {
        return skillBarCooldowns;
    }

    /**
     * Retrieves the indicator for an unassigned skill slot
     *
     * @return unassigned indicator
     */
    public ItemStack getUnassigned()
    {
        return unassigned;
    }

    /**
     * Retrieves the default skill bar layout
     *
     * @return default skill bar layout
     */
    public boolean[] getDefaultBarLayout()
    {
        return defaultBarLayout;
    }

    /**
     * Retrieves the list of locked skill bar slots
     *
     * @return list of locked skill bar slots
     */
    public boolean[] getLockedSlots()
    {
        return lockedSlots;
    }

    private void loadSkillBarSettings()
    {
        ConfigurationSection bar = config.getConfigurationSection("Skill Bar");
        skillBarEnabled = bar.getBoolean("enabled", false);
        skillBarCooldowns = bar.getBoolean("show-cooldown", true);

        ConfigurationSection icon = bar.getConfigurationSection("empty-icon");
        Material mat;
        try
        {
            mat = Material.valueOf(icon.getString("material", "PUMPKIN_SEEDS").toUpperCase().replace(' ', '_'));
        }
        catch (Exception ex)
        {
            mat = Material.PUMPKIN_SEEDS;
        }
        unassigned = new ItemStack(mat);
        unassigned.setData(new MaterialData(mat, (byte) icon.getInt("data", 0)));

        ItemMeta meta = unassigned.getItemMeta();
        meta.setDisplayName(TextFormatter.colorString(icon.getString("text", "&7Unassigned")));
        unassigned.setItemMeta(meta);

        ConfigurationSection layout = bar.getConfigurationSection("layout");
        for (int i = 0; i < 9; i++)
        {
            ConfigurationSection slot = layout.getConfigurationSection((i + 1) + "");
            defaultBarLayout[i] = slot.getBoolean("skill", i <= 5);
            lockedSlots[i] = slot.getBoolean("locked", false);
        }
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                 Logging Settings                  //
    //                                                   //
    ///////////////////////////////////////////////////////

    private int loadLogLevel;

    /**
     * Gets the logging level
     *
     * @return logging level
     */
    public int getLoadLogLevel()
    {
        return loadLogLevel;
    }

    private void loadLoggingSettings()
    {
        loadLogLevel = config.getInt("Logging");
    }
}
