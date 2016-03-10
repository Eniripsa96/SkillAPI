/**
 * SkillAPI
 * com.sucy.skill.data.Settings
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
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
package com.sucy.skill.data;

import com.rit.sucy.config.CommentedConfig;
import com.rit.sucy.config.parse.DataSection;
import com.rit.sucy.player.Protection;
import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>The management class for SkillAPI's config.yml settings.</p>
 */
public class Settings
{

    private HashMap<String, GroupSettings> groups = new HashMap<String, GroupSettings>();

    private SkillAPI    plugin;
    private DataSection config;

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
        CommentedConfig file = new CommentedConfig(plugin, "config");
        file.checkDefaults();
        file.trim();
        file.save();
        config = file.getConfig();
        reload();
    }

    /**
     * <p>Reloads the settings from SkillAPI's config.yml file.</p>
     * <p>This will fill in any missing values with default values
     * and trim any values that aren't supposed to be there.</p>
     */
    public void reload()
    {
        loadAccountSettings();
        loadClassSettings();
        loadManaSettings();
        loadSkillSettings();
        loadItemSettings();
        loadGUISettings();
        loadComboSettings();
        loadExpSettings();
        loadSkillBarSettings();
        loadLoggingSettings();
        loadWorldSettings();
        loadSaveSettings();
        loadTargetingSettings();
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                  Group Settings                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    public void loadGroupSettings()
    {
        CommentedConfig file = new CommentedConfig(plugin, "groups");
        DataSection config = file.getConfig();
        groups.clear();

        for (String key : config.keys())
        {
            groups.put(key.toLowerCase(), new GroupSettings(config.getSection(key)));
        }
        for (String group : SkillAPI.getGroups())
        {
            if (!groups.containsKey(group.toLowerCase()))
            {
                GroupSettings settings = new GroupSettings();
                groups.put(group.toLowerCase(), settings);
                settings.save(config.createSection(group.toLowerCase()));
            }
        }

        file.save();
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
        List<String> list = config.getList(ACCOUNT_PERM);
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
    //                 Targeting Settings                //
    //                                                   //
    ///////////////////////////////////////////////////////

    private static final String TARGET_BASE    = "Targeting.";
    private static final String TARGET_MONSTER = TARGET_BASE + "monsters-enemy";
    private static final String TARGET_PASSIVE = TARGET_BASE + "passive-ally";
    private static final String TARGET_PLAYER  = TARGET_BASE + "player-ally";

    private ArrayList<String> monsterWorlds = new ArrayList<String>();
    private ArrayList<String> passiveWorlds = new ArrayList<String>();
    private ArrayList<String> playerWorlds = new ArrayList<String>();

    private boolean monsterEnemy;
    private boolean passiveAlly;
    private boolean playerAlly;

    /**
     * Checks whether or not something can be attacked
     *
     * @param attacker the attacking entity
     * @param target   the target entity
     *
     * @return true if can be attacked, false otherwise
     */
    public boolean canAttack(LivingEntity attacker, LivingEntity target)
    {
        if (attacker instanceof Player)
        {
            if (target instanceof Animals && !(target instanceof Tameable))
            {
                if (passiveAlly || passiveWorlds.contains(attacker.getWorld().getName()))
                    return false;
            }
            else if (target instanceof Monster)
            {
                if (monsterEnemy || monsterWorlds.contains(attacker.getWorld().getName()))
                    return true;
            }
            else if (target instanceof Player)
            {
                if (playerAlly || playerWorlds.contains(attacker.getWorld().getName()))
                    return false;
            }
        }
        return Protection.canAttack(attacker, target);
    }

    /**
     * Checks whether or not something is an ally
     *
     * @param attacker the attacking entity
     * @param target   the target entity
     *
     * @return true if an ally, false otherwise
     */
    public boolean isAlly(LivingEntity attacker, LivingEntity target)
    {
        return !canAttack(attacker, target);
    }

    private void loadTargetingSettings()
    {
        if (config.isList(TARGET_MONSTER)) {
            monsterWorlds.addAll(config.getList(TARGET_MONSTER));
            monsterEnemy = false;
        }
        else monsterEnemy = config.getBoolean(TARGET_MONSTER);

        if (config.isList(TARGET_PASSIVE)) {
            passiveWorlds.addAll(config.getList(TARGET_PASSIVE));
            passiveAlly = false;
        }
        else passiveAlly = config.getBoolean(TARGET_PASSIVE);

        if (config.isList(TARGET_PLAYER)) {
            playerWorlds.addAll(config.getList(TARGET_PLAYER));
            playerAlly = false;
        }
        else playerAlly = config.getBoolean(TARGET_PLAYER);
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                  Saving Settings                  //
    //                                                   //
    ///////////////////////////////////////////////////////

    private static final String SAVE_BASE = "Saving.";
    private static final String SAVE_AUTO = SAVE_BASE + "auto-save";
    private static final String SAVE_MINS = SAVE_BASE + "minutes";
    private static final String SAVE_SQL  = SAVE_BASE + "sql-database";
    private static final String SAVE_SQLD = SAVE_BASE + "sql-details";

    private boolean auto;
    private boolean useSql;
    private int     minutes;

    private String sqlHost;
    private String sqlPort;
    private String sqlDatabase;
    private String sqlUser;
    private String sqlPass;

    /**
     * Checks whether or not auto saving is enabled
     *
     * @return true if enabled, false otherwise
     */
    public boolean isAutoSave()
    {
        return auto;
    }

    /**
     * Retrieves the amount of ticks in between each auto save
     *
     * @return frequency of saves
     */
    public int getSaveFreq()
    {
        return minutes * 60 * 20;
    }

    /**
     * Checks whether or not the plugin is using SQL Database saving
     *
     * @return true if enabled, false otherwise
     */
    public boolean isUseSql()
    {
        return useSql;
    }

    /**
     * Retrieves the host IP for the database
     *
     * @return host IP for SQL database
     */
    public String getSQLHost()
    {
        return sqlHost;
    }

    /**
     * Retrieves the host port for the database
     *
     * @return host port for SQL database
     */
    public String getSQLPort()
    {
        return sqlPort;
    }

    /**
     * Retrieves the name of the SQL database
     *
     * @return SQL database name
     */
    public String getSQLDatabase()
    {
        return sqlDatabase;
    }

    /**
     * Retrieves the username for the database credentials
     *
     * @return SQL database username
     */
    public String getSQLUser()
    {
        return sqlUser;
    }

    /**
     * Retrieves the password for the database credentials
     *
     * @return SQL database password
     */
    public String getSQLPass()
    {
        return sqlPass;
    }

    private void loadSaveSettings()
    {
        auto = config.getBoolean(SAVE_AUTO);
        minutes = config.getInt(SAVE_MINS);
        useSql = config.getBoolean(SAVE_SQL);

        if (useSql)
        {
            DataSection details = config.getSection(SAVE_SQLD);
            sqlHost = details.getString("host");
            sqlPort = details.getString("port");
            sqlDatabase = details.getString("database");
            sqlUser = details.getString("username");
            sqlPass = details.getString("password");
        }
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                  Class Settings                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    private static final String CLASS_BASE   = "Classes.";
    private static final String CLASS_MODIFY = CLASS_BASE + "modify-health";
    private static final String CLASS_HP     = CLASS_BASE + "classless-hp";
    private static final String CLASS_SHOW   = CLASS_BASE + "show-auto-skills";
    private static final String CLASS_ATTRIB = CLASS_BASE + "attributes-enabled";
    private static final String CLASS_REFUND = CLASS_BASE + "attributes-downgrade";
    private static final String CLASS_LEVEL  = CLASS_BASE + "level-up-skill";

    private boolean modifyHealth;
    private int     defaultHealth;
    private boolean showAutoSkills;
    private boolean attributesEnabled;
    private boolean attributesDowngrade;
    private String  levelUpSkill;

    /**
     * Checks whether or not SkillAPI should modify the max health of players
     *
     * @return true if enabled, false otherwise
     */
    public boolean isModifyHealth()
    {
        return modifyHealth;
    }

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

    /**
     * Checks whether or not attributes are enabled
     *
     * @return true if enabled, false otherwise
     */
    public boolean isAttributesEnabled()
    {
        return attributesEnabled;
    }

    /**
     * Checks whether or not attribute points can be refunded
     *
     * @return true if can refund, false otherwise
     */
    public boolean isAttributesDowngrade()
    {
        return attributesDowngrade;
    }

    /**
     * Checks whether or not the plugin has a valid skill for
     * level up effects loaded.
     *
     * @return true if one is available, false otherwise
     */
    public boolean hasLevelUpEffect()
    {
        return getLevelUpSkill() != null;
    }

    /**
     * Retrieves the skill used for level up effects
     *
     * @return skill for level up effects
     */
    public DynamicSkill getLevelUpSkill()
    {
        Skill skill = SkillAPI.getSkill(levelUpSkill);
        return (skill instanceof DynamicSkill) ? (DynamicSkill) skill : null;
    }

    private void loadClassSettings()
    {
        modifyHealth = config.getBoolean(CLASS_MODIFY);
        defaultHealth = config.getInt(CLASS_HP);
        showAutoSkills = config.getBoolean(CLASS_SHOW);
        attributesEnabled = config.getBoolean(CLASS_ATTRIB);
        attributesDowngrade = config.getBoolean(CLASS_REFUND);
        levelUpSkill = config.getString(CLASS_LEVEL);
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
        gainFreq = (int) (config.getDouble(MANA_FREQ) * 20);
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                  Skill Settings                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    private static final String SKILL_BASE      = "Skills.";
    private static final String SKILL_DOWNGRADE = SKILL_BASE + "allow-downgrade";
    private static final String SKILL_MESSAGE   = SKILL_BASE + "show-messages";
    private static final String SKILL_RADIUS    = SKILL_BASE + "message-radius";
    private static final String SKILL_BLOCKS    = SKILL_BASE + "block-filter";

    private ArrayList<Material> filteredBlocks;

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

    /**
     * Retrieves the list of filtered blocks
     *
     * @return list of blocks
     */
    public List<Material> getFilteredBlocks()
    {
        return filteredBlocks;
    }

    private void loadSkillSettings()
    {
        allowDowngrade = config.getBoolean(SKILL_DOWNGRADE);
        showSkillMessages = config.getBoolean(SKILL_MESSAGE);
        messageRadius = config.getInt(SKILL_RADIUS);

        filteredBlocks = new ArrayList<Material>();
        List<String> list = config.getList(SKILL_BLOCKS);
        for (String item : list)
        {
            try
            {
                Material mat = Material.valueOf(item.toUpperCase().replace(' ', '_'));
                filteredBlocks.add(mat);
            }
            catch (Exception ex)
            {
                Bukkit.getLogger().info("Invalid block type \"" + item + "\"");
            }
        }
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   Item Settings                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    private static final String ITEM_BASE    = "Items.";
    private static final String ITEM_LORE    = ITEM_BASE + "lore-requirements";
    private static final String ITEM_CLASS   = ITEM_BASE + "lore-class-text";
    private static final String ITEM_LEVEL   = ITEM_BASE + "lore-level-text";
    private static final String ITEM_EXCLUDE = ITEM_BASE + "lore-exclude-text";
    private static final String ITEM_CHECK   = ITEM_BASE + "players-per-check";

    private boolean checkLore;
    private String  loreClassText;
    private String  loreLevelText;
    private String  loreExcludeText;
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
     * Retrieves the text used for class requirements on items
     *
     * @return lore text for class requirements
     */
    public String getLoreClassText()
    {
        return loreClassText;
    }

    /**
     * Retrieves the text used for level requirements on items
     *
     * @return lore text for level requirements
     */
    public String getLoreLevelText()
    {
        return loreLevelText;
    }

    /**
     * Retrieves the text used for excluded classes on items
     *
     * @return lore text for excluded classes
     */
    public String getLoreExcludeText()
    {
        return loreExcludeText;
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

    private void loadItemSettings()
    {
        checkLore = config.getBoolean(ITEM_LORE);
        loreClassText = config.getString(ITEM_CLASS);
        loreLevelText = config.getString(ITEM_LEVEL);
        loreExcludeText = config.getString(ITEM_EXCLUDE);
        playersPerCheck = config.getInt(ITEM_CHECK);
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   GUI Settings                    //
    //                                                   //
    ///////////////////////////////////////////////////////

    private static final String GUI_BASE   = "GUI.";
    private static final String GUI_OLD    = GUI_BASE + "old-health-bar";
    private static final String GUI_LVLBAR = GUI_BASE + "level-bar";
    private static final String GUI_FOOD   = GUI_BASE + "food-bar";
    private static final String GUI_ACTION = GUI_BASE + "use-action-bar";
    private static final String GUI_TEXT   = GUI_BASE + "action-bar-text";
    private static final String GUI_BOARD  = GUI_BASE + "scoreboard-enabled";
    private static final String GUI_NAME   = GUI_BASE + "show-class-name";
    private static final String GUI_LEVEL  = GUI_BASE + "show-class-level";
    private static final String GUI_MAP    = GUI_BASE + "map-tree-enabled";

    private boolean oldHealth;
    private String  levelBar;
    private String  foodBar;
    private boolean useActionBar;
    private String  actionText;
    private boolean showScoreboard;
    private boolean showClassName;
    private boolean showClassLevel;
    private boolean showMapTree;
    private boolean showTree;

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
     * Gets the setting for using the level bar
     *
     * @return level bar setting
     */
    public String getLevelBar()
    {
        return levelBar;
    }

    /**
     * Gets the setting for using the food bar
     *
     * @return food bar setting
     */
    public String getFoodBar()
    {
        return foodBar;
    }

    /**
     * Checks whether or not the action bar is being used
     *
     * @return true if used, false otherwise
     */
    public boolean isUseActionBar()
    {
        return useActionBar;
    }

    /**
     * Gets the text to display on the action bar
     *
     * @return action bar text
     */
    public String getActionText()
    {
        return actionText;
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

    /**
     * Checks whether or not map trees are enabled on the server
     *
     * @return true if enabled, false otherwise
     */
    public boolean isMapTreeEnabled()
    {
        return showMapTree;
    }

    public boolean isMapTreeAvailable()
    {
        return showTree;
    }

    private void loadGUISettings()
    {
        oldHealth = config.getBoolean(GUI_OLD);
        levelBar = config.getString(GUI_LVLBAR);
        foodBar = config.getString(GUI_FOOD);
        useActionBar = config.getBoolean(GUI_ACTION);
        actionText = config.getString(GUI_TEXT);
        showScoreboard = config.getBoolean(GUI_BOARD);
        showClassName = config.getBoolean(GUI_NAME);
        showClassLevel = config.getBoolean(GUI_LEVEL);
        showMapTree = config.getString(GUI_MAP).equalsIgnoreCase("TRUE");
        showTree = showMapTree || config.getString(GUI_MAP).equalsIgnoreCase("PARTIAL");
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //               Click Combo Settings                //
    //                                                   //
    ///////////////////////////////////////////////////////

    private static final String COMBO_BASE    = "Click Combos.";
    private static final String COMBO_ENABLED = COMBO_BASE + "enabled";
    private static final String COMBO_LEFT    = COMBO_BASE + "use-click-left";
    private static final String COMBO_RIGHT   = COMBO_BASE + "use-click-right";
    private static final String COMBO_SHIFT   = COMBO_BASE + "use-click-shift";
    private static final String COMBO_SIZE    = COMBO_BASE + "combo-size";
    private static final String COMBO_TIME    = COMBO_BASE + "click-time";

    private boolean combosEnabled;
    private boolean comboLeft;
    private boolean comboRight;
    private boolean comboShift;
    private int     comboSize;
    private int     clickTime;

    /**
     * Checks whether or not click combos are enabled
     *
     * @return true if enabled, false otherwise
     */
    public boolean isCombosEnabled()
    {
        return combosEnabled;
    }

    /**
     * Checks whether or not left clicks are enabled for combos
     *
     * @return true if enabled, false otherwise
     */
    public boolean isComboLeft()
    {
        return comboLeft;
    }

    /**
     * Checks whether or not right clicks are enabled for combos
     *
     * @return true if enabled, false otherwise
     */
    public boolean isComboRight()
    {
        return comboRight;
    }

    /**
     * Checks whether or not shift clicks are enabled for combos
     *
     * @return true if enabled, false othewise
     */
    public boolean isComboShift()
    {
        return comboShift;
    }

    /**
     * Retrieves the max length of combos to be used
     *
     * @return max length of combos to be used
     */
    public int getComboSize()
    {
        return comboSize;
    }

    /**
     * Retrieves the amount of seconds allowed between clicks before the combo resets
     *
     * @return number of seconds before a click combo resets
     */
    public int getClickTime()
    {
        return clickTime;
    }

    private void loadComboSettings()
    {
        combosEnabled = config.getBoolean(COMBO_ENABLED);
        comboLeft = config.getBoolean(COMBO_LEFT);
        comboRight = config.getBoolean(COMBO_RIGHT);
        comboShift = config.getBoolean(COMBO_SHIFT);
        comboSize = config.getInt(COMBO_SIZE);
        clickTime = (int) (1000 * config.getDouble(COMBO_TIME));
    }

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
     *
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
     *
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

        DataSection formula = config.getSection(EXP_BASE + "formula");
        int x = formula.getInt("x");
        int y = formula.getInt("y");
        int z = formula.getInt("z");
        expFormula = new ExpFormula(x, y, z);

        DataSection yields = config.getSection(EXP_BASE + "yields");
        this.yields.clear();
        for (String key : yields.keys())
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
        DataSection bar = config.getSection("Skill Bar");
        skillBarEnabled = bar.getBoolean("enabled", false);
        skillBarCooldowns = bar.getBoolean("show-cooldown", true);

        DataSection icon = bar.getSection("empty-icon");
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

        DataSection layout = bar.getSection("layout");
        int skillCount = 0;
        for (int i = 0; i < 9; i++)
        {
            DataSection slot = layout.getSection((i + 1) + "");
            defaultBarLayout[i] = slot.getBoolean("skill", i <= 5);
            lockedSlots[i] = slot.getBoolean("locked", false);
            if (defaultBarLayout[i])
            {
                skillCount++;
            }
        }
        if (skillCount == 9)
        {
            Bukkit.getLogger().severe("Invalid Skill Bar Setup - Cannot have all 9 skill slots!");
            Bukkit.getLogger().severe("  -> Setting last slot to be a weapon slot");
            defaultBarLayout[8] = false;
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

    ///////////////////////////////////////////////////////
    //                                                   //
    //                  World Settings                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    private static final String WORLD_BASE   = "Worlds.";
    private static final String WORLD_ENABLE = WORLD_BASE + "enable";
    private static final String WORLD_TYPE   = WORLD_BASE + "use-as-enabling";
    private static final String WORLD_LIST   = WORLD_BASE + "worlds";

    private List<String> worlds;
    private boolean      worldEnabled;
    private boolean      worldEnableList;

    /**
     * Checks whether or not SkillAPI is active in the world
     *
     * @param world world to check
     *
     * @return true if active, false otherwise
     */
    public boolean isWorldEnabled(World world)
    {
        return isWorldEnabled(world.getName());
    }

    /**
     * Checks whether or not SkillAPI is active in the world with
     * the given name.
     *
     * @param world world name
     *
     * @return true if active, false otherwise
     */
    public boolean isWorldEnabled(String world)
    {
        return !worldEnabled || (worldEnableList == worlds.contains(world));
    }

    private void loadWorldSettings()
    {
        worldEnabled = config.getBoolean(WORLD_ENABLE);
        worldEnableList = config.getBoolean(WORLD_TYPE);
        worlds = config.getList(WORLD_LIST);
    }
}
