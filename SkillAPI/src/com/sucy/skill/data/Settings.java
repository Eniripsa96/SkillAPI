package com.sucy.skill.data;

import com.rit.sucy.config.Config;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.TreeType;
import com.sucy.skill.data.io.keys.SettingValues;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

/**
 * <p>The management class for SkillAPI's config.yml settings.</p>
 */
public class Settings
{

    private HashMap<String, GroupSettings> groups = new HashMap<String, GroupSettings>();
    private AccountSettings accountSettings;

    private SkillAPI plugin;

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
        ConfigurationSection config = plugin.getConfig();
        Config.trim(config);
        Config.setDefaults(config);
        plugin.saveConfig();

        loadGroupSettings();
        loadAccountSettings();
        loadClassSettings(config);
        loadManaSettings(config);
        loadSkillSettings(config);
        loadItemSettings(config);
        loadGUISettings(config);
        loadCastSettings(config);
        loadExpSettings(config);
        loadSkillBarSettings(config);
        loadLoggingSettings(config);
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

    public GroupSettings getGroupSettings(String group)
    {
        return groups.get(group.toLowerCase());
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                 Account Settings                  //
    //                                                   //
    ///////////////////////////////////////////////////////

    private void loadAccountSettings()
    {
        Config file = new Config(plugin, "accounts");
        ConfigurationSection config = file.getConfig();
        accountSettings = new AccountSettings(config);
    }

    public AccountSettings getAccountSettings()
    {
        return accountSettings;
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                  Class Settings                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    private int     defaultHealth     = 20;
    private boolean useExampleClasses = false;
    private boolean useExampleSkills  = false;

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
     * <p>Retrieves whether or not example classes are to be loaded.</p>
     *
     * @return true if example classes are to be loaded, false otherwise
     */
    public boolean isUseExampleClasses()
    {
        return useExampleClasses;
    }

    /**
     * <p>Retrieves whether or not example skills are to be loaded.</p>
     *
     * @return true if example skills are to be loaded ,false otherwise
     */
    public boolean isUseExampleSkills()
    {
        return useExampleSkills;
    }

    /**
     * <p>Sets the default health for classless players.</p>
     *
     * @param health the new default health for classless players
     */
    public void setDefaultHealth(int health)
    {
        this.defaultHealth = health;
    }

    private void loadClassSettings(ConfigurationSection config)
    {
        defaultHealth = config.getInt(SettingValues.CLASS_DEFAULT_HP, defaultHealth);
        useExampleClasses = config.getBoolean(SettingValues.CLASS_EXAMPLES, useExampleClasses);
        useExampleSkills = config.getBoolean(SettingValues.CLASS_SKILL_EXAMPLES, useExampleSkills);
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   Mana Settings                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    private boolean manaEnabled = true;
    private int     gainFreq    = 20;
    private int     gainAmount  = 2;

    public boolean isManaEnabled()
    {
        return manaEnabled;
    }

    public int getGainFreq()
    {
        return gainFreq;
    }

    public int getGainAmount()
    {
        return gainAmount;
    }

    public void setManaEnabled(boolean enabled)
    {
        this.manaEnabled = enabled;
    }

    public void setGainFreq(int ticks)
    {
        this.gainFreq = ticks;
    }

    public void setGainAmount(int mana)
    {
        this.gainAmount = mana;
    }

    private void loadManaSettings(ConfigurationSection config)
    {
        manaEnabled = config.getBoolean(SettingValues.MANA_ENABLED, manaEnabled);
        gainFreq = config.getInt(SettingValues.MANA_GAIN_FREQ, gainFreq);
        gainAmount = config.getInt(SettingValues.MANA_GAIN_AMOUNT, gainAmount);
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                  Skill Settings                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    private String  treeType          = TreeType.REQUIREMENT.getKey();
    private boolean allowDowngrade    = true;
    private boolean showSkillMessages = true;
    private int     messageRadius     = 20;

    public String getTreeType()
    {
        return treeType;
    }

    public boolean isAllowDowngrade()
    {
        return allowDowngrade;
    }

    public boolean isShowSkillMessages()
    {
        return showSkillMessages;
    }

    public int getMessageRadius()
    {
        return messageRadius;
    }

    public void setTreeType(TreeType type)
    {
        this.treeType = type.getKey();
    }

    public void setAllowDowngrade(boolean allow)
    {
        this.allowDowngrade = allow;
    }

    public void setShowSkillMessages(boolean enabled)
    {
        this.showSkillMessages = enabled;
    }

    public void setMessageRadius(int radius)
    {
        this.messageRadius = radius;
    }

    private void loadSkillSettings(ConfigurationSection config)
    {
        treeType = config.getString(SettingValues.SKILL_TREE_TYPE, treeType);
        allowDowngrade = config.getBoolean(SettingValues.SKILL_ALLOW_DOWNGRADE, allowDowngrade);
        showSkillMessages = config.getBoolean(SettingValues.SKILL_SHOW_MESSAGE, showSkillMessages);
        messageRadius = config.getInt(SettingValues.SKILL_MESSAGE_RADIUS, messageRadius);
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   Item Settings                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    private boolean checkLore        = true;
    private boolean defaultOneDamage = false;
    private int     playersPerCheck  = 1;

    public boolean isCheckLore()
    {
        return checkLore;
    }

    public boolean isDefaultOneDamage()
    {
        return defaultOneDamage;
    }

    public int getPlayersPerCheck()
    {
        return playersPerCheck;
    }

    public void setDefaultOneDamage(boolean oneDamage)
    {
        defaultOneDamage = oneDamage;
    }

    public void setPlayersPerCheck(int players)
    {
        playersPerCheck = players;
    }

    private void loadItemSettings(ConfigurationSection config)
    {
        checkLore = config.getBoolean(SettingValues.ITEM_LORE_REQUIREMENTS, checkLore);
        defaultOneDamage = config.getBoolean(SettingValues.ITEM_DEFAULT_ONE_DAMAGE, defaultOneDamage);
        playersPerCheck = config.getInt(SettingValues.ITEM_PLAYERS_PER_CHECK, playersPerCheck);
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   GUI Settings                    //
    //                                                   //
    ///////////////////////////////////////////////////////

    private boolean oldHealth      = false;
    private boolean useLevelBar    = false;
    private boolean showScoreboard = true;
    private boolean showClassName  = true;
    private boolean showClassLevel = true;

    public boolean isOldHealth()
    {
        return oldHealth;
    }

    public boolean isUseLevelBar()
    {
        return useLevelBar;
    }

    public boolean isShowScoreboard()
    {
        return showScoreboard;
    }

    public boolean isShowClassName()
    {
        return showClassName;
    }

    public boolean isShowClassLevel()
    {
        return showClassLevel;
    }

    private void loadGUISettings(ConfigurationSection config)
    {
        oldHealth = config.getBoolean(SettingValues.GUI_OLD_HEALTH, oldHealth);
        useLevelBar = config.getBoolean(SettingValues.GUI_LEVEL_BAR, useLevelBar);
        showScoreboard = config.getBoolean(SettingValues.GUI_SCOREBOARD, showScoreboard);
        showClassName = config.getBoolean(SettingValues.GUI_CLASS_NAME, showClassName);
        showClassLevel = config.getBoolean(SettingValues.GUI_CLASS_LEVEL, showClassLevel);
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   Cast Settings                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    private final HashMap<Click, Boolean> enabledClicks = new HashMap<Click, Boolean>();

    private boolean useSkillBars         = true;
    private boolean useSkillBarCooldowns = true;
    private boolean useClickCombos       = false;

    public boolean isUseSkillBars()
    {
        return useSkillBars;
    }

    public boolean isUseSkillBarCooldowns()
    {
        return useSkillBarCooldowns;
    }

    public boolean isUseClickCombos()
    {
        return useClickCombos;
    }

    public boolean isClickEnabled(Click click)
    {
        return useClickCombos && enabledClicks.get(click);
    }

    private void loadCastSettings(ConfigurationSection config)
    {
        useSkillBars = config.getBoolean(SettingValues.CAST_SKILL_BARS, useSkillBars);
        useSkillBarCooldowns = config.getBoolean(SettingValues.CAST_SKILL_BAR_COOLDOWNS, useSkillBarCooldowns);
        useClickCombos = config.getBoolean(SettingValues.CAST_CLICK_COMBOS, useClickCombos);

        enabledClicks.put(Click.LEFT, config.getBoolean(SettingValues.CAST_CLICK_LEFT, true));
        enabledClicks.put(Click.RIGHT, config.getBoolean(SettingValues.CAST_CLICK_RIGHT, true));
        enabledClicks.put(Click.SHIFT, config.getBoolean(SettingValues.CAST_CLICK_SHIFT, false));
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   Exp Settings                    //
    //                                                   //
    ///////////////////////////////////////////////////////

    private final HashMap<String, Double> yields = new HashMap<String, Double>();

    private ExpFormula expFormula;
    private boolean useOrbs           = false;
    private boolean blockSpawner      = true;
    private boolean blockEgg          = true;
    private boolean blockCreative     = true;
    private boolean showExpMessages   = true;
    private boolean showLevelMessages = true;

    public int getRequiredExp(int level)
    {
        return expFormula.calculate(level);
    }

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

    public boolean isUseOrbs()
    {
        return useOrbs;
    }

    public boolean isBlockSpawner()
    {
        return blockSpawner;
    }

    public boolean isBlockEgg()
    {
        return blockEgg;
    }

    public boolean isBlockCreative()
    {
        return blockCreative;
    }

    public boolean isShowExpMessages()
    {
        return showExpMessages;
    }

    public boolean isShowLevelMessages()
    {
        return showLevelMessages;
    }

    public void setExpFormula(ExpFormula formula)
    {
        expFormula = formula;
    }

    public void setBlockSpawner(boolean block)
    {
        blockSpawner = block;
    }

    public void setBlockEgg(boolean block)
    {
        blockEgg = block;
    }

    public void setBlockCreative(boolean block)
    {
        blockCreative = block;
    }

    public void setShowExpMessages(boolean show)
    {
        showExpMessages = show;
    }

    public void setShowLevelMessages(boolean show)
    {
        showLevelMessages = show;
    }

    private void loadExpSettings(ConfigurationSection config)
    {
        this.useOrbs = config.getBoolean(SettingValues.EXP_USE_ORBS, useOrbs);
        this.blockSpawner = config.getBoolean(SettingValues.EXP_BLOCK_SPAWNER, blockSpawner);
        this.blockEgg = config.getBoolean(SettingValues.EXP_BLOCK_EGG, blockEgg);
        this.blockCreative = config.getBoolean(SettingValues.EXP_BLOCK_CREATIVE, blockCreative);
        this.showExpMessages = config.getBoolean(SettingValues.EXP_MESSAGE_ENABLED, showExpMessages);
        this.showLevelMessages = config.getBoolean(SettingValues.EXP_LVL_MESSAGE_ENABLED, showLevelMessages);

        ConfigurationSection formula = config.getConfigurationSection(SettingValues.EXP_FORMULA);
        int x = formula.getInt("x", 1);
        int y = formula.getInt("y", 4);
        int z = formula.getInt("z", 0);
        int w = formula.getInt("w", 0);
        expFormula = new ExpFormula(x, y, z, w);

        ConfigurationSection yields = config.getConfigurationSection(SettingValues.EXP_YIELDS);
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

    private boolean[] defaultBarLayout = new boolean[] { true, true, true, true, true, true, true, false, false };
    private boolean[] lockedSlots      = new boolean[] { false, false, false, false, false, false, false, false, false };

    public boolean[] getDefaultBarLayout()
    {
        return defaultBarLayout;
    }

    public boolean[] getLockedSlots()
    {
        return lockedSlots;
    }

    private void loadSkillBarSettings(ConfigurationSection config)
    {
        ConfigurationSection bar = config.getConfigurationSection(SettingValues.SKILL_BAR);
        for (int i = 0; i < 9; i++)
        {
            ConfigurationSection slot = bar.getConfigurationSection((i + 1) + "");
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

    public int getLoadLogLevel()
    {
        return loadLogLevel;
    }

    public void setLoadLogLevel(int level)
    {
        loadLogLevel = level;
    }

    private void loadLoggingSettings(ConfigurationSection config)
    {
        loadLogLevel = config.getInt(SettingValues.LOG_LOAD, loadLogLevel);
    }
}
