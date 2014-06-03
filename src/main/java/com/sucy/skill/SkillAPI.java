package com.sucy.skill;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.SenderType;
import com.rit.sucy.config.Config;
import com.rit.sucy.text.TextSizer;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.api.CustomClass;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.StatusHolder;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Mechanic;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.util.effects.DOTHelper;
import com.sucy.skill.api.util.effects.ParticleHelper;
import com.sucy.skill.click.ClickListener;
import com.sucy.skill.command.admin.*;
import com.sucy.skill.command.basic.*;
import com.sucy.skill.command.console.CmdProfessConsole;
import com.sucy.skill.command.console.CmdResetConsole;
import com.sucy.skill.config.ConfigConverter;
import com.sucy.skill.config.PlayerValues;
import com.sucy.skill.config.SettingValues;
import com.sucy.skill.example.ExampleClasses;
import com.sucy.skill.language.OtherNodes;
import com.sucy.skill.language.StatNodes;
import com.sucy.skill.mccore.PrefixManager;
import com.sucy.skill.mechanic.HealthMechanic;
import com.sucy.skill.quests.QuestsModuleManager;
import com.sucy.skill.skillbar.PlayerSkillBar;
import com.sucy.skill.skillbar.SkillBarListener;
import com.sucy.skill.task.InventoryTask;
import com.sucy.skill.task.ManaTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Skill API</p>
 * <p>Developed by Steven Sucy (Eniripsa96)</p>
 * <p>Developed for the BukkitDev community</p>
 * <br/>
 * <p>Do not create an instance of this class, only use references
 * provided for you through accessor methods, the SkillPlugin interface,
 * or directly obtaining it through the PluginManager.</p>
 */
public class SkillAPI extends JavaPlugin {

    // Data
    private final HashMap<String, PlayerSkills> players = new HashMap<String, PlayerSkills>();
    private final HashMap<String, Integer> exp = new HashMap<String, Integer>();
    private final HashMap<String, UUID> ids = new HashMap<String, UUID>();
    private final HashMap<Integer, StatusHolder> holders = new HashMap<Integer, StatusHolder>();
    private final HashMap<String, OfflinePlayer> scoreboardStats = new HashMap<String, OfflinePlayer>();

    // Utility
    private RegistrationManager registration;
    private SkillBarListener barListener;
    private ClickListener clickListener;
    private DOTHelper dotHelper;

    // Tasks
    private InventoryTask invTask;
    private ManaTask manaTask;

    // Configurations
    private Config playerConfig;
    private Config languageConfig;

    // Settings
    private ExampleClasses examples;
    private String treeType;
    private String defaultClass;
    private boolean[] bar;
    private boolean reset;
    private boolean oldHealth;
    private boolean levelBar;
    private boolean expOrbs;
    private boolean allowDowngrade;
    private boolean defaultOneDamage;
    private boolean blockSpawnerExp;
    private boolean blockEggExp;
    private boolean blockCreativeExp;
    private boolean showExpMessage;
    private boolean showLvlMessage;
    private double expLost;
    private int startingPoints;
    private int pointsPerLevel;
    private int messageRadius;
    private int logging;
    private int baseHp;
    private int x;
    private int y;
    private int z;
    private int w;

    // ----------------------------- Plugin Methods -------------------------------------- //

    /**
     * <p>Initializes plugin resources</p>
     * <p>Do not call this method</p>
     */
    @Override
    public void onEnable() {

        QuestsModuleManager.copyQuestsModule();

        saveDefaultConfig();
        reloadConfig();
        ConfigConverter.convert(getConfig());
        Config.trim(getConfig());
        Config.setDefaults(getConfig());
        saveConfig();
        playerConfig = new Config(this, "players");
        languageConfig = new Config(this, "language");
        languageConfig.saveDefaultConfig();
        ConfigConverter.convertCommands(this);
        languageConfig.trim();
        languageConfig.checkDefaults();

        // Pre-load stat offline players
        scoreboardStats.put(StatNodes.EXP_KEY, getServer().getOfflinePlayer(getMessage(StatNodes.EXP, true)));
        scoreboardStats.put(StatNodes.HEALTH_KEY, getServer().getOfflinePlayer(getMessage(StatNodes.HEALTH, true)));
        scoreboardStats.put(StatNodes.LEVEL_KEY, getServer().getOfflinePlayer(getMessage(StatNodes.LEVEL, true)));
        scoreboardStats.put(StatNodes.MANA_KEY, getServer().getOfflinePlayer(getMessage(StatNodes.MANA, true)));
        scoreboardStats.put(StatNodes.POINTS_KEY, getServer().getOfflinePlayer(getMessage(StatNodes.POINTS, true)));

        // Class options
        defaultClass = getConfig().getString(SettingValues.CLASS_DEFAULT, "none");
        reset = getConfig().getBoolean(SettingValues.CLASS_RESET, false);
        startingPoints = getConfig().getInt(SettingValues.CLASS_STARTING_POINTS, 1);
        pointsPerLevel = getConfig().getInt(SettingValues.CLASS_POINTS_PER_LEVEL, 1);
        baseHp = getConfig().getInt(SettingValues.CLASS_HP, 20);
        if (getConfig().getBoolean(SettingValues.CLASS_EXAMPLES, false)) {
            examples = new ExampleClasses(this);
        }

        // Skill options
        treeType = getConfig().getString(SettingValues.SKILL_TREE_TYPE, "requirement");
        allowDowngrade = getConfig().getBoolean(SettingValues.SKILL_ALLOW_DOWNGRADE, true);
        messageRadius = getConfig().getInt(SettingValues.SKILL_MESSAGE_RADIUS, 20);

        // Mana options
        if (getConfig().getBoolean(SettingValues.MANA_ENABLED, true)) {
            int manaFreq = getConfig().getInt(SettingValues.MANA_GAIN_FREQ);
            int manaGain = getConfig().getInt(SettingValues.MANA_GAIN_AMOUNT);
            manaTask = new ManaTask(this, manaFreq, manaGain);
        }

        // GUI options
        oldHealth = getConfig().getBoolean(SettingValues.GUI_OLD_HEALTH, false);
        levelBar = getConfig().getBoolean(SettingValues.GUI_LEVEL_BAR, false);
        PrefixManager.registerText(getMessage(StatNodes.LEVEL, true));
        PrefixManager.showClasses = getConfig().getBoolean(SettingValues.GUI_CLASS_NAME, true);
        PrefixManager.showLevels = getConfig().getBoolean(SettingValues.GUI_CLASS_LEVEL, true);
        PrefixManager.showSidebar = getConfig().getBoolean(SettingValues.GUI_SCOREBOARD, true);

        // Item options
        defaultOneDamage = getConfig().getBoolean(SettingValues.ITEM_DEFAULT_ONE_DAMAGE, false);
        if (getConfig().getBoolean(SettingValues.ITEM_LORE_REQUIREMENTS, true)) {
            int playersPerTick = getConfig().getInt(SettingValues.ITEM_PLAYERS_PER_CHECK);
            invTask = new InventoryTask(this, playersPerTick);
        }

        // Experience options
        blockSpawnerExp = getConfig().getBoolean(SettingValues.EXP_BLOCK_SPAWNER, true);
        blockEggExp = getConfig().getBoolean(SettingValues.EXP_BLOCK_EGG, true);
        blockCreativeExp = getConfig().getBoolean(SettingValues.EXP_BLOCK_CREATIVE, true);
        expOrbs = getConfig().getBoolean(SettingValues.EXP_USE_ORBS, false);
        expLost = getConfig().getDouble(SettingValues.EXP_LOST_ON_DEATH, 0);
        showExpMessage = getConfig().getBoolean(SettingValues.EXP_MESSAGE_ENABLED, true);
        showLvlMessage = getConfig().getBoolean(SettingValues.EXP_LVL_MESSAGE_ENABLED, true);
        ConfigurationSection formula = getConfig().getConfigurationSection(SettingValues.EXP_FORMULA);
        x = formula.getInt("x");
        y = formula.getInt("y");
        z = formula.getInt("z");
        w = formula.getInt("w");
        ConfigurationSection section = getConfig().getConfigurationSection(SettingValues.EXP_YIELDS);
        for (String mob : section.getKeys(false)) {
            exp.put(mob, section.getInt(mob));
        }

        // Skill bar options
        bar = new boolean[9];
        boolean hasWeapon = false;
        for (int i = 1; i <= 9; i++) {
            if (getConfig().getString(SettingValues.SKILL_BAR + "." + i, "skill").equalsIgnoreCase("skill")) {
                bar[i - 1] = true;
            }
            else {
                bar[i - 1] = false;
                hasWeapon = true;
            }
        }
        if (!hasWeapon) bar[8] = false;

        // Logging options
        logging = getConfig().getInt(SettingValues.LOG_LOAD, 0);


        // Register classes and skills
        registration = new RegistrationManager(this);
        registration.initialize();

        // Load player data
        if (playerConfig.getConfig().contains(PlayerValues.ROOT) && playerConfig.getConfig().getConfigurationSection(PlayerValues.ROOT).getKeys(false) != null) {
            for (String player : playerConfig.getConfig().getConfigurationSection(PlayerValues.ROOT).getKeys(false)) {
                VersionPlayer vp = new VersionPlayer(player);
                PlayerSkills data = new PlayerSkills(this, vp, playerConfig.getConfig().getConfigurationSection(PlayerValues.ROOT + "." + player));
                players.put(vp.getIdString(), data);
                data.updateHealth();
                if (logging >= 2) {
                    getLogger().info("Loaded the player: " + vp.getName() + " with ID " + vp.getIdString());
                }
            }
        }

        // Append player data
        for (Player player : getServer().getOnlinePlayers()) {
            VersionPlayer p = new VersionPlayer(player);
            if (!players.containsKey(p.getIdString()))
                players.put(p.getIdString(), new PlayerSkills(this, p));
            else getPlayer(p).startPassiveAbilities();
        }

        // Cast options
        if (getConfig().getBoolean(SettingValues.CAST_CLICK_COMBOS, false)) {
            clickListener = new ClickListener(this);
        }
        if (getConfig().getBoolean(SettingValues.CAST_SKILL_BARS, false)) {
            barListener = new SkillBarListener(this);
        }

        // Set up Helper classes
        new SkillListener(this);
        dotHelper = new DOTHelper(this);
        ParticleHelper.initialize();

        // Set up listeners
        for (IMechanic mechanic : Mechanic.MECHANICS.values()) {
            if (mechanic instanceof Listener) {
                getServer().getPluginManager().registerEvents((Listener)mechanic, this);
            }
        }

        // Set up commands
        ConfigurableCommand root = new ConfigurableCommand(this, "class", SenderType.ANYONE);
        root.addSubCommands(
            new ConfigurableCommand(this, "forceprofess", SenderType.CONSOLE_ONLY, new CmdProfessConsole(), "Professes a player", "<class> <player>"),
            new ConfigurableCommand(this, "forcereset", SenderType.CONSOLE_ONLY, new CmdResetConsole(), "Resets a player", "<player>"),
            new ConfigurableCommand(this, "bind", SenderType.PLAYER_ONLY, new CmdBind(), "Binds skill to held item", "<skill>", PermissionNodes.BASIC),
            new ConfigurableCommand(this, "cast", SenderType.PLAYER_ONLY, new CmdCast(), "Casts a skill", "<skill>", PermissionNodes.BASIC),
            new ConfigurableCommand(this, "exp", SenderType.ANYONE, new CmdExp(), "Gives a player exp", "<amount> [player]", PermissionNodes.LEVEL),
            new ConfigurableCommand(this, "info", SenderType.ANYONE, new CmdInfo(), "Views details of player", "[player]", PermissionNodes.BASIC),
            new ConfigurableCommand(this, "level", SenderType.ANYONE, new CmdLevel(), "Levels a player up", "<amount> [player]", PermissionNodes.LEVEL),
            new ConfigurableCommand(this, "mana", SenderType.ANYONE, new CmdMana(), "Gives a player mana", "<amount> [player]", PermissionNodes.MANA),
            new ConfigurableCommand(this, "options", SenderType.PLAYER_ONLY, new CmdOptions(), "Views profession options", "", PermissionNodes.BASIC),
            new ConfigurableCommand(this, "points", SenderType.ANYONE, new CmdPoints(), "Gives skill points", "<amount> [player]", PermissionNodes.POINTS),
            new ConfigurableCommand(this, "profess", SenderType.PLAYER_ONLY, new CmdProfess(), "Professes as a class", "<class>", PermissionNodes.BASIC),
            new ConfigurableCommand(this, "reload", SenderType.ANYONE, new CmdReload(), "Reloads the plugin", "", PermissionNodes.RELOAD),
            new ConfigurableCommand(this, "reset", SenderType.PLAYER_ONLY, new CmdReset(), "Resets all stats", "", PermissionNodes.RESET),
            new ConfigurableCommand(this, "skills", SenderType.PLAYER_ONLY, new CmdSkills(), "Opens skill tree", "", PermissionNodes.BASIC),
            new ConfigurableCommand(this, "unbind", SenderType.PLAYER_ONLY, new CmdUnbind(), "Unbinds held item", "", PermissionNodes.BASIC)
        );
        if (isUsingSkillBars()) {
            root.addSubCommand(new ConfigurableCommand(this, "bar", SenderType.PLAYER_ONLY, new CmdToggleBar(), "Toggles the skill bar", "", PermissionNodes.BASIC));
        }
        CommandManager.registerCommand(root);
    }

    /**
     * <p>Clears all plugin data after saving</p>
     * <p>Do not call this method</p>
     */
    @Override
    public void onDisable() {

        // Clear listeners
        HandlerList.unregisterAll(this);

        ((HealthMechanic)Mechanic.MECHANICS.get("HEALTH")).clear();

        // Tasks
        if (manaTask != null) {
            manaTask.cancel();
            manaTask = null;
        }
        if (invTask != null) {
            invTask.cancel();
            invTask = null;
        }

        // Save skill bar data
        if (isUsingSkillBars()) {
            barListener.disable();
        }

        // Save player data
        for (String key : playerConfig.getConfig().getKeys(false)) {
            playerConfig.getConfig().set(key, null);
        }
        for (String player : players.keySet()) {
            savePlayer(player);
        }
        playerConfig.saveConfig();

        // Stop passive abilities
        for (PlayerSkills player : players.values()) {
            player.stopPassiveAbilities();
            player.applyMaxHealth(20);
        }

        // Clear scoreboards
        PrefixManager.clearAll();

        // Clear all data
        registration.clearData();
        exp.clear();
        players.clear();
        getServer().getScheduler().cancelTasks(this);
        if (examples != null) {
            examples.disable();
        }
    }

    // ----------------------------- Data Management Methods -------------------------------------- //

    /**
     * <p>Saves the data of the player with the given name</p>
     * <p>The name is not cast-sensitive</p>
     * <p>If no data is found for the player, nothing happens</p>
     * <p>This does not update the config, only adds their data to the buffer</p>
     *
     * @param player player name
     */
    public void savePlayer(String player) {
        player = player.toLowerCase();
        if (!players.containsKey(player)) return;
        players.get(player).save(playerConfig.getConfig(), PlayerValues.ROOT + "." + player + ".");
    }

    /**
     * <p>Forces the player configuration to save</p>
     * <p>This does not update player data, only causes the config to save</p>
     */
    public void savePlayerConfig() {
        playerConfig.saveConfig();
    }

    /**
     * <p>Updates all player data and forces the configuration to save</p>
     */
    public void savePlayerData() {
        for (String playerName : players.keySet()) {
            savePlayer(playerName);
        }
        playerConfig.saveConfig();
    }

    /**
     * <p>Applies default values to a configuration section</p>
     * <p>This copies over all unset default values that were added</p>
     *
     * @param config configuration section to apply default values for
     */
    private void setDefaults(ConfigurationSection config) {
        if (config.getDefaultSection() == null) return;
        for (String key : config.getDefaultSection().getKeys(false)) {
            if (config.isConfigurationSection(key)) {
                setDefaults(config.getConfigurationSection(key));
            }
            else if (!config.isSet(key)) {
                config.set(key, config.get(key));
            }
        }
    }

    // ----------------------------- Settings Accessor Methods -------------------------------------- //

    /**
     * Retrieves the default class for the plugin
     *
     * @return default class
     */
    public CustomClass getDefaultClass() {
        return getClass(defaultClass);
    }

    /**
     * @return base health for classless players
     */
    public int getBaseHp() {
        return baseHp;
    }

    /**
     * @return type of tree arrangement being used
     */
    public String getTreeType() {
        if (treeType == null) return "Requirement";
        return treeType;
    }

    /**
     * @return whether or not mana is enabled
     */
    public boolean isManaEnabled() {
        return manaTask != null;
    }

    /**
     * @return whether or not professing resets player levels
     */
    public boolean doProfessionsReset() {
        return reset;
    }

    /**
     * @return number of skill points players start with
     */
    public int getStartingPoints() {
        return startingPoints;
    }

    /**
     * @return number of points gained each level
     */
    public int getPointsPerLevel() {
        return pointsPerLevel;
    }

    /**
     * @return whether or not downgrading skills is allowed
     */
    public boolean isAllowingDowngrade() {
        return allowDowngrade;
    }

    /**
     * Retrieves the exp yield for the given mob
     *
     * @param mob mob name
     * @return    exp yield
     */
    public int getExp(String mob) {
        if (!exp.containsKey(mob.toLowerCase())) return 0;
        return exp.get(mob.toLowerCase());
    }

    /**
     * @return whether or not old health bar mechanics are enabled
     */
    public boolean oldHealthEnabled() {
        return oldHealth;
    }

    /**
     * @return whether or not the level bar is being used
     */
    public boolean usingLevelBar() {
        return levelBar;
    }

    /**
     * @return whether or not click combos are being used
     */
    public boolean usingClickCombos() {
        return clickListener != null;
    }

    /**
     * @return whether or not to use experience orbs instead of the table
     */
    public boolean usingExpOrbs() {
        return expOrbs;
    }

    /**
     * @return whether or not unknown items default to one damage
     */
    public boolean isDefaultOneDamage() {
        return defaultOneDamage;
    }

    /**
     * @return whether or not skill bars are being used
     */
    public boolean isUsingSkillBars() {
        return barListener != null;
    }

    /**
     * @return the default layout for the skill bar
     */
    public boolean[] getDefaultBar() {
        return bar;
    }

    /**
     * @return whether or not the experience from mob spawners is being blocked
     */
    public boolean blockingSpawnerExp() {
        return blockSpawnerExp;
    }

    /**
     * @return whether or not the experience from mob eggs is being blocked
     */
    public boolean blockingEggExp() {
        return blockEggExp;
    }

    /**
     * @return whether or not the experience gained while in creative mode is being blocked
     */
    public boolean blockingCreativeExp() {
        return blockCreativeExp;
    }

    /**
     * @return lost experience percentage upon dying
     */
    public double getLostExp() {
        return expLost;
    }

    /**
     * <p>Calculates the required experience for a level</p>
     * <p>Follows the format:</p>
     * <p>exp = x * (level + y) * (level + y) + z * level + w</p>
     * <p>Where x, y, z, and w are values from the config and level is the provided level</p>
     *
     * @param level level
     * @return      required exp
     */
    public int getRequiredExp(int level) {
        int value = level + y;
        return x * value * value + z * level + w;
    }

    /**
     * @return level of logging for loading
     */
    public int getLoggingLevel() {
        return logging;
    }

    /**
     * @return whether or not messages for gaining exp are shown
     */
    public boolean isExpMessageEnabled() {
        return showExpMessage;
    }

    /**
     * @param enabled whether or not to show experience messages
     */
    public void setExpMessageEnabled(boolean enabled) {
        showExpMessage = enabled;
    }

    /**
     * @return whether or not messages for leveling up are shown
     */
    public boolean isLvlMessageEnabled() {
        return showLvlMessage;
    }

    /**
     * @param enabled whether or not to show level messages
     */
    public void setLvlMessageEnabled(boolean enabled) {
        this.showLvlMessage = enabled;
    }

    /**
     * <p>Checks whether or not SkillAPI is using the example classes.</p>
     *
     * @return true if using example classes, false otherwise
     */
    public boolean isUsingExampleClasses() {
        return examples != null;
    }

    /**
     * <p>Retrieves the handler for the example classes.</p>
     *
     * @return the handler for the example classes
     */
    public ExampleClasses getExampleClasses() {
        return examples;
    }

    // ----------------------------- Registration methods -------------------------------------- //

    /**
     * <p>Registers a skill with the game</p>
     * <p>This must be done in the SkillPlugin.registerSkills(SkillAPI) method when the API calls it.
     * This is to ensure the correct assigning of skills to classes by registering them in the proper sequence.</p>
     *
     * @param skill skill to add
     */
    public void addSkill(ClassSkill skill) {
        registration.addSkill(skill);
    }

    /**
     * <p>Adds multiple skills to the game</p>
     * <p>This must be done in the SkillPlugin.registerSkills(SkillAPI) method when the API calls it.
     * This is to ensure the correct assigning of skills to classes by registering them in the proper sequence.</p>
     *
     * @param skills skills to add
     */
    public void addSkills(ClassSkill ... skills) {
        registration.addSkills(skills);
    }

    /**
     * <p>Copies dynamic skill data from the config into the API</p>
     * <p>This must be done in SkillPlugin.registerSkills(SkillAPI) else the copied data will not be loaded
     * and will be overwritten when the plugin is disabled.</p>
     *
     * @param config config containing dynamic skill data
     */
    public void loadDynamicSkills(ConfigurationSection config) {
        registration.loadDynamicSkills(config);
    }

    /**
     * <p>Adds a new class to the game</p>
     * <p>This must be done in the SkillPlugin.registerClasses(SkillAPI) method when the API calls it.
     * This is to ensure the correct assigning of skills to classes by registering them in the proper sequence.</p>
     *
     * @param customClass class to add
     */
    public void addClass(CustomClass customClass) {
        registration.addClass(customClass);
    }

    /**
     * <p>Adds multiple classes to the game</p>
     * <p>This must be done in the SkillPlugin.registerClasses(SkillAPI) method when the API calls it.
     * This is to ensure the correct assigning of skills to classes by registering them in the proper sequence.</p>
     *
     * @param classes classes to add
     */
    public void addClasses(CustomClass ... classes) {
        registration.addClasses(classes);
    }

    /**
     * <p>Copies dynamic skill data from the config into the API</p>
     * <p>This must be done in SkillPlugin.registerClasses(SkillAPI) else the copied data will not be loaded
     * and will be overwritten when the plugin is disabled.</p>
     *
     * @param config config containing dynamic skill data
     */
    public void loadDynamicClasses(ConfigurationSection config) {
        registration.loadDynamicClasses(config);
    }

    // ----------------------------- Player Methods -------------------------------------- //

    /**
     * <p>Retrieves data for a player</p>
     * <p>If no data is found for the player, new data is created</p>
     *
     * @param name player name
     * @return     player class data
     */
    public PlayerSkills getPlayer(String name) {
        return getPlayer(new VersionPlayer(name));
    }

    /**
     * <p>Retrieves data for a player</p>
     * <p>If no data is found for the player, new data is created</p>
     *
     * @param player player reference
     */
    public PlayerSkills getPlayer(VersionPlayer player) {

        // If the data doesn't exist, create a new instance
        if (!players.containsKey(player.getIdString())) {
            PlayerSkills data = new PlayerSkills(this, player);
            players.put(player.getIdString(), data);
            return data;
        }

        // When the data is there, just return it
        else return players.get(player.getIdString());
    }

    /**
     * <p>Retrieves data for a player</p>
     * <p>If no data is found for the player, new data is created</p>
     *
     * @param player player reference
     */
    public PlayerSkills getPlayer(OfflinePlayer player) {
        return getPlayer(new VersionPlayer(player));
    }

    /**
     * <p>Retrieves data for a player</p>
     * <p>If no data is found for the player, new data is created</p>
     *
     * @param player player reference
     */
    public PlayerSkills getPlayer(Player player) {
        return getPlayer(new VersionPlayer(player));
    }

    /**
     * <p>Retrieves data for a player</p>
     * <p>If no data is found for the player, new data is created</p>
     *
     * @param player player reference
     */
    public PlayerSkills getPlayer(HumanEntity player) {
        return getPlayer(new VersionPlayer(player));
    }

    /**
     * <p>Retrieves data for a player</p>
     * <p>If no data is found for the player, new data is created</p>
     *
     * @param id player UUID
     */
    public PlayerSkills getPlayer(UUID id) {
        return getPlayer(new VersionPlayer(id));
    }

    /**
     * Refunds all points to a player
     */
    public void refundPlayerSkills() {
        for (PlayerSkills player : players.values()) {
            for (String skillName : player.getSkills().keySet()) {
                player.getSkills().put(skillName, 0);
            }
            player.givePoints(startingPoints + pointsPerLevel * (player.getLevel() - 1) - player.getPoints());
        }
    }

    public UUID getPlayerUUID(String name) {
        return ids.get(name.toLowerCase());
    }

    public void registerPlayer(Player player) {
        ids.put(player.getName().toLowerCase(), player.getUniqueId());
    }

    public void unregisterPlayer(Player player) {
        ids.remove(player.getName().toLowerCase());
    }

    // ----------------------------- Data Accessor Methods -------------------------------------- //

    /**
     * @return DOTHelper used by the API
     */
    public DOTHelper getDOTHelper() {
        return dotHelper;
    }

    /**
     * <p>Retrieves the skill bar for a player</p>
     * <p>If skill bars are disabled or the player
     * doesn't have a class, this returns null</p>
     *
     * @param player player to get the skill bar for
     * @return       the player's skill bar
     */
    public PlayerSkillBar getSkillBar(Player player) {
        if (!isUsingSkillBars()) return null;
        return barListener.getSkillBar(player);
    }

    /**
     * <p>Checks if a class is loaded with the given name</p>
     * <p>The name is not case-sensitive</p>
     *
     * @param name class name
     * @return     true if loaded, false otherwise
     */
    public boolean hasClass(String name){
        return registration.hasClass(name);
    }

    /**
     * Gets all child skills of the skill with the given name
     *
     * @param name skill name
     * @return     all child skills
     */
    public ArrayList<ClassSkill> getChildSkills(String name) {
        ArrayList<ClassSkill> skills = new ArrayList<ClassSkill>();
        for (ClassSkill skill : skills) {
            if (skill.getSkillReq() != null && skill.getSkillReq().equalsIgnoreCase(name))
                skills.add(skill);
        }
        return skills;
    }

    /**
     * Checks if the skill is loaded
     *
     * @param name skill name
     * @return     true if loaded, false otherwise
     */
    public boolean hasSkill(String name) {
        return registration.hasSkill(name);
    }

    /**
     * <p>Retrieves the skill with the given name</p>
     * <p>If no loaded skill has the name, null is returned</p>
     * <p>The name is not case-sensitive</p>
     *
     * @param name skill name
     * @return     skill reference
     */
    public ClassSkill getSkill(String name) {
        return registration.getSkill(name);
    }

    /**
     * <p>Retrieves the skill with the given name</p>
     * <p>If no loaded skill has the name, null is returned</p>
     * <p>The name is not case-sensitive</p>
     *
     * @param name skill name
     * @return     skill reference
     * @deprecated use getSkill(String) instead
     */
    @Deprecated
    public ClassSkill getRegisteredSkill(String name) {
        return getSkill(name);
    }

    /**
     * <p>Retrieves the class with the given name</p>
     * <p>If no loaded class has the name, null is returned</p>
     * <p>The name is not case-sensitive</p>
     *
     * @param name class name
     * @return     class reference
     */
    public CustomClass getClass(String name) {
        return registration.getClass(name);
    }

    /**
     * <p>Retrieves the class with the given name</p>
     * <p>If no loaded class has the name, null is returned</p>
     * <p>The name is not case-sensitive</p>
     *
     * @param name class name
     * @return     class reference
     * @deprecated use getClass(String) instead
     */
    @Deprecated
    public CustomClass getRegisteredClass(String name) {
        return getClass(name);
    }

    /**
     * <p>Checks if a skill is loaded with the given name</p>
     * <p>The name is not case-sensitive</p>
     *
     * @param name skill name
     * @return     true if registered, false otherwise
     */
    public boolean isSkillRegistered(String name) {
        return registration.isSkillRegistered(name);
    }

    /**
     * <p>Retrieves the names of all children of the class with the given name</p>
     * <p>The name is not case sensitive</p>
     *
     * @param name parent class name
     * @return     names of all child classes
     */
    public ArrayList<String> getChildren(String name) {
        return getChildren(name, Bukkit.getConsoleSender());
    }

    /**
     * <p>Retrieves the names of all children of the class that the player has permission for</p>
     * <p>The name is not case-sensitive</p>
     *
     * @param name   parent class name
     * @param player player to check for
     * @return       names of all available child classes
     */
    public ArrayList<String> getChildren(String name, Player player) {
        return getChildren(name, (CommandSender)player);
    }

    /**
     * <p>Retrieves the names of all children of the class that the sender has permission for</p>
     * <p>The name is not case-sensitive</p>
     *
     * @param name   parent class name
     * @param sender sender to check permissions
     * @return       names of all available child classes
     */
    private ArrayList<String> getChildren(String name, CommandSender sender) {
        ArrayList<String> children = new ArrayList<String>();
        for (CustomClass t : registration.getClasses()) {
            if (name == null) {
                if (t.getParent() == null && hasPermission(sender, t)) children.add(t.getName());
            }
            else {
                if (t.getName().equalsIgnoreCase(name)) continue;
                if (t.getParent() == null) continue;
                if (!hasPermission(sender, t)) continue;
                if (t.getParent().equalsIgnoreCase(name)) {
                    children.add(t.getName());
                }
            }
        }
        return children;
    }

    /**
     * <p>Checks if the sender has permission to use the class</p>
     *
     * @param sender player to check for
     * @param t      class to check
     * @return       true if the player has permission, false otherwise
     */
    public boolean hasPermission(CommandSender sender, CustomClass t) {
        return !t.needsPermission() ||
                sender.hasPermission(PermissionNodes.CLASS) ||
                sender.hasPermission(PermissionNodes.CLASS + "." + t.getName());
    }

    /**
     * <p>Retrieves the status data for the entity</p>
     * <p>If no data is found, new data is created</p>
     *
     * @param entity entity to retrieve for
     * @return       status data
     */
    public StatusHolder getStatusHolder(LivingEntity entity) {
        if (!holders.containsKey(entity.getEntityId())) {
            holders.put(entity.getEntityId(), new StatusHolder());
        }
        return holders.get(entity.getEntityId());
    }

    /**
     * <p>Clears all status data for an entity</p>
     *
     * @param entity entity to remove for
     */
    public void clearStatusHolder(LivingEntity entity) {
        holders.remove(entity.getEntityId());
    }

    /**
     * <p>Retrieves the OfflinePlayer representing the
     * stat for the scoreboard.</p>
     * @param key StatNode String key
     * @return    OfflinePlayer representation
     */
    public OfflinePlayer getScoreboardStat(String key) {
        return scoreboardStats.get(key);
    }

    // ----------------------------- Language Methods -------------------------------------- //

    /**
     * Sends a message for a skill using the caster's name
     *
     * @param skill  skill being casted
     * @param caster caster of the skill
     */
    public void sendSkillMessage(ClassSkill skill, Player caster) {

        // Custom skill message
        String message;
        if (skill.hasMessage()) {
            message = applyFilters(skill.getMessage());
            message = message.replace("{player}", caster.getName()).replace("{skill}", skill.getName());
        }

        // Universal message
        else {
            message = getMessage(OtherNodes.SKILL_CAST, true);
            message = message.replace("{skill}", skill.getName()).replace("{player}", caster.getName());
        }

        // Send the message
        for (Player player : caster.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(caster.getLocation()) <= messageRadius * messageRadius) {
                player.sendMessage(message);
            }
        }
    }

    /**
     * Gets a message from the language file
     *
     * @param path         path to the message
     * @param applyFilters whether or not to apply filters
     * @return             message
     */
    public String getMessage(String path, boolean applyFilters) {
        String message = languageConfig.getConfig().getString(path);

        // Invalid message
        if (message == null) {
            return null;
        }

        // Apply filters if applicable
        if (applyFilters) {
            message = applyFilters(message);
        }

        return message;
    }

    /**
     * Gets a message group from the language file
     *
     * @param path         path to the message group
     * @param applyFilters whether or not to apply filters
     * @return             message group
     */
    public List<String> getMessages(String path, boolean applyFilters) {

        // No filters just returns the result
        if (!applyFilters) return languageConfig.getConfig().getStringList(path);

        List<String> original = languageConfig.getConfig().getStringList(path);
        List<String> filtered = new ArrayList<String>();

        // Filter each string
        for (String string : original) {
            filtered.add(applyFilters(string));
        }

        // Return the filtered list
        return filtered;
    }

    /**
     * Sends a status message to a player
     *
     * @param player   player to send to
     * @param node     message node
     * @param duration duration left on the status
     */
    public void sendStatusMessage(Player player, String node, int duration) {
        String message = getMessage(node, true);
        message = message.replace("{duration}", "" + duration);
        player.sendMessage(message);
    }

    /**
     * <p>Applies global filters to the string</p>
     * <p>The exact filters can be found in the configuration tutorials
     * on the BukkitDev page</p>
     *
     * @param message message to filter
     * @return        filtered message
     */
    public String applyFilters(String message){

        // Color
        message = message.replaceAll("&([0-9a-fl-orA-FL-OR])", ChatColor.COLOR_CHAR + "$1");

        // Break lines
        message = message.replace("{break}", TextSizer.createLine("", "", "-"));

        // Sizer filters
        message = filterSizer(message, true);
        message = filterSizer(message, false);

        // Return the result
        return message + ChatColor.RESET;
    }

    /**
     * Applies the size filter
     *
     * @param message message to filter
     * @param front   true if apply front sizer, false for back sizer
     * @return        filtered message
     */
    private String filterSizer(String message, boolean front) {
        Pattern regex = Pattern.compile("\\{expand" + (front ? "Front" : "Back") + "\\(([0-9]+),(.+)\\)\\}");
        Matcher match = regex.matcher(message);
        int size = message.length();
        while (match.find()) {
            int length = Integer.parseInt(match.group(1));
            String string = match.group(2);
            message = message.substring(0, match.start() + message.length() - size)
                    + (TextSizer.measureString(string) > length - 2 ? string : TextSizer.expand(string, length, front))
                    + message.substring(match.end());
        }
        return message;
    }
}
