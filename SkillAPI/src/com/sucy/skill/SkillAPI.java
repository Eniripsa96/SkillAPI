package com.sucy.skill;

import com.sucy.skill.api.*;
import com.sucy.skill.api.dynamic.DynamicClass;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.TargetSkill;
import com.sucy.skill.api.util.TextSizer;
import com.sucy.skill.api.util.effects.DOTHelper;
import com.sucy.skill.click.ClickListener;
import com.sucy.skill.command.ClassCommander;
import com.sucy.skill.config.*;
import com.sucy.skill.mccore.CoreChecker;
import com.sucy.skill.mccore.PrefixManager;
import com.sucy.skill.task.InventoryTask;
import com.sucy.skill.task.ManaTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
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
    private final Hashtable<String, PlayerSkills> players = new Hashtable<String, PlayerSkills>();
    private final Hashtable<String, Integer> exp = new Hashtable<String, Integer>();
    private final Hashtable<String, ClassSkill> skills = new Hashtable<String, ClassSkill>();
    private final Hashtable<String, CustomClass> classes = new Hashtable<String, CustomClass>();
    private final HashMap<Integer, StatusHolder> holders = new HashMap<Integer, StatusHolder>();

    // Utility
    private DOTHelper dotHelper;

    // Register mode
    private RegisterMode mode = RegisterMode.DONE;

    // Tasks
    private InventoryTask invTask;
    private ManaTask manaTask;

    // Configurations
    private Config playerConfig;
    private Config languageConfig;
    private Config skillConfig;
    private Config classConfig;

    // Settings
    private String treeType;
    private boolean mana;
    private boolean reset;
    private boolean oldHealth;
    private boolean levelBar;
    private boolean clickCombo;
    private int startingPoints;
    private int pointsPerLevel;
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

        reloadConfig();
        playerConfig = new Config(this, "players");
        languageConfig = new Config(this, "language");
        skillConfig = new Config(this, "dynamic/skills");
        classConfig = new Config(this, "dynamic/classes");
        languageConfig.saveDefaultConfig();

        // Make sure default config values are set
        for (SettingValues value : SettingValues.values()) {
            if (!getConfig().isSet(value.path())) {
                getConfig().set(value.path(), getConfig().get(value.path()));
            }
        }
        saveConfig();

        // Load options
        treeType = getConfig().getString(SettingValues.TREE_TYPE.path(), "requirement");
        reset = getConfig().getBoolean(SettingValues.PROFESS_RESET.path(), false);
        mana = getConfig().getBoolean(SettingValues.MANA_ENABLED.path(), true);
        startingPoints = getConfig().getInt(SettingValues.STARTING_POINTS.path(), 1);
        pointsPerLevel = getConfig().getInt(SettingValues.POINTS_PER_LEVEL.path(), 1);
        oldHealth = getConfig().getBoolean(SettingValues.OLD_HEALTH_BAR.path(), false);
        levelBar = getConfig().getBoolean(SettingValues.USE_LEVEL_BAR.path(), false);
        clickCombo = getConfig().getBoolean(SettingValues.USE_CLICK_COMBOS.path(), false);

        // Make sure dynamic files are created
        if (!skillConfig.getConfigFile().exists()) skillConfig.saveConfig();
        if (!classConfig.getConfigFile().exists()) classConfig.saveConfig();

        // Request skills first
        mode = RegisterMode.SKILL;
        for (Plugin plugin : getServer().getPluginManager().getPlugins()) {
            if (plugin instanceof SkillPlugin) {
                ((SkillPlugin) plugin).registerSkills(this);
            }
        }

        // Load dynamic skills
        for (String key : skillConfig.getConfig().getKeys(false)) {
            if (!skills.containsKey(key.toLowerCase())) {
                skills.put(key.toLowerCase(), new DynamicSkill(key));
            }
        }

        // Register classes after
        mode = RegisterMode.CLASS;
        for (Plugin plugin : getServer().getPluginManager().getPlugins()) {
            if (plugin instanceof SkillPlugin) {
                ((SkillPlugin) plugin).registerClasses(this);
            }
        }

        // Load dynamic classes
        for (String key : classConfig.getConfig().getKeys(false)) {
            if (!classes.containsKey(key.toLowerCase())) {
                classes.put(key.toLowerCase(), new DynamicClass(key));
            }
        }

        // Done registering everything
        mode = RegisterMode.DONE;

        // Experience formula
        ConfigurationSection formula = getConfig().getConfigurationSection(SettingValues.EXP_FORMULA.path());
        x = formula.getInt("x");
        y = formula.getInt("y");
        z = formula.getInt("z");
        w = formula.getInt("w");

        // Set up the mana task
        int manaFreq = getConfig().getInt(SettingValues.MANA_GAIN_FREQ.path());
        int manaGain = getConfig().getInt(SettingValues.MANA_GAIN_AMOUNT.path());
        if (mana) manaTask = new ManaTask(this, manaFreq, manaGain);

        // Set up the inventory task
        int playersPerTick = getConfig().getInt(SettingValues.PLAYERS_PER_CHECK.path());
        invTask = new InventoryTask(this, playersPerTick);

        // Load experience yields
        ConfigurationSection section = getConfig().getConfigurationSection(SettingValues.KILLS.path());
        for (String mob : section.getKeys(false)) {
            exp.put(mob, section.getInt(mob));
        }

        // Load skill data
        for (ClassSkill skill : skills.values()) {
            try {
                if (skill instanceof DynamicSkill) {
                    skill.update(skillConfig.getConfig().getConfigurationSection(skill.getName()));
                }
                else skill.update(new Config(this, "skill\\" + skill.getName()).getConfig());
            }
            catch (Exception e) {
                getLogger().severe("Failed to load skill: " + skill.getName());
                e.printStackTrace();
            }
        }

        // Load skill tree data
        for (CustomClass tree : classes.values()) {
            if (tree instanceof DynamicClass) {
                tree.update(classConfig.getConfig().getConfigurationSection(tree.getName()));
            }
            else tree.update(new Config(this, "class\\" + tree.getName()).getConfig());
        }

        // Arrange skill trees
        List<CustomClass> classList = new ArrayList<CustomClass>(this.classes.values());
        for (CustomClass tree : classList) {
            try {
                tree.getTree().arrange();
            }
            catch (Exception ex) {
                getLogger().severe("Failed to arrange skill tree for the class " + tree.getName() + " - " + ex.getMessage());
                classes.remove(tree.getName().toLowerCase());
            }
        }

        // Save dynamic skills
        for (Map.Entry<String, ClassSkill> entry : skills.entrySet()) {
            if (entry.getValue() instanceof DynamicSkill) {
                DynamicSkill skill = (DynamicSkill)entry.getValue();
                skill.save(skillConfig.getConfig().createSection(skill.getName()));
            }
        }
        skillConfig.saveConfig();

        // Save dynamic classes
        for (Map.Entry<String, CustomClass> entry : classes.entrySet()) {
            if (entry.getValue() instanceof DynamicClass) {
                DynamicClass c = (DynamicClass)entry.getValue();
                c.save(classConfig.getConfig().createSection(c.getName()));
            }
        }
        classConfig.saveConfig();

        getLogger().info("Loaded " + skills.size() + " skills and " + classes.size() + " skill trees");

        // Load player data
        if (playerConfig.getConfig().contains(PlayerValues.ROOT) && playerConfig.getConfig().getConfigurationSection(PlayerValues.ROOT).getKeys(false) != null) {
            for (String player : playerConfig.getConfig().getConfigurationSection(PlayerValues.ROOT).getKeys(false)) {
                PlayerSkills data = new PlayerSkills(this, player, playerConfig.getConfig().getConfigurationSection(PlayerValues.ROOT + "." + player));
                players.put(player.toLowerCase(), data);
                data.updateHealth();
            }
        }

        // Append player data
        for (Player player : getServer().getOnlinePlayers()) {
            if (!players.containsKey(player.getName().toLowerCase()))
                players.put(player.getName().toLowerCase(), new PlayerSkills(this, player.getName()));
        }

        // Listeners and Commands
        new APIListener(this);
        new ClassCommander(this);
        if (clickCombo) new ClickListener(this);
        dotHelper = new DOTHelper(this);
    }

    /**
     * <p>Clears all plugin data after saving</p>
     * <p>Do not call this method</p>
     */
    @Override
    public void onDisable() {

        // Clear listeners
        HandlerList.unregisterAll(this);

        // Tasks
        if (manaTask != null) {
            manaTask.cancel();
            manaTask = null;
        }
        if (invTask != null) {
            invTask.cancel();
            invTask = null;
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
        if (CoreChecker.isCoreActive())
            PrefixManager.clearAll();

        // Clear all data
        skills.clear();
        classes.clear();
        exp.clear();
        players.clear();
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

    // ----------------------------- Settings Accessor Methods -------------------------------------- //

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
        return mana;
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
        return clickCombo;
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

    // ----------------------------- Registration methods -------------------------------------- //

    /**
     * <p>Registers a skill with the game</p>
     * <p>This must be done in the SkillPlugin.registerSkills(SkillAPI) method when the API calls it.
     * This is to ensure the correct assigning of skills to classes by registering them in the proper sequence.</p>
     *
     * @param skill skill to add
     */
    public void addSkill(ClassSkill skill) {

        if (mode != RegisterMode.SKILL) throw new IllegalStateException("Cannot register skills outside of the registerSkills method");

        // Null names are not allowed
        if (skill.getName() == null) {
            getLogger().severe("Unable to register skill - " + skill.getClass().getName() + " - null name");
            return;
        }

        // Don't allow duplicate names
        else if (skills.containsKey(skill.getName().toLowerCase())) {
            getLogger().severe("Duplicate skill names detected! - " + skill.getName());
            return;
        }

        // Make sure the right attributes are there
        skill.checkDefault(SkillAttribute.LEVEL, 1, 0);
        skill.checkDefault(SkillAttribute.COST, 1, 0);
        if (skill instanceof SkillShot || skill instanceof TargetSkill) {
            skill.checkDefault(SkillAttribute.MANA, 0, 0);
            skill.checkDefault(SkillAttribute.COOLDOWN, 0, 0);
            if (skill instanceof TargetSkill) {
                skill.checkDefault(SkillAttribute.RANGE, 6, 0);
            }
        }

        // Detect if default values are needed
        Config configFile = new Config(this, "skill\\" + skill.getName());
        ConfigurationSection config = configFile.getConfig();
        boolean neededOnly = config.getKeys(false).size() != 0;

        // Save default values
        try {
            if (!config.contains(SkillValues.MAX_LEVEL))
                config.set(SkillValues.MAX_LEVEL, skill.getMaxLevel() < 1 ? 1 : skill.getMaxLevel());
            if (!config.contains(SkillValues.INDICATOR))
                config.set(SkillValues.INDICATOR, skill.getIndicator().name());
            if (skill.getSkillReq() != null && !neededOnly) {
                config.set(SkillValues.SKILL_REQ, skill.getSkillReq());
                config.set(SkillValues.SKILL_REQ_LEVEL, skill.getSkillReqLevel());
            }
            for (String attribute : skill.getAttributeNames()) {
                if (!config.contains(attribute + "-base"))
                    config.set(attribute + "-base", skill.getBase(attribute));
                if (!config.contains(attribute + "-scale"))
                    config.set(attribute + "-scale", skill.getScale(attribute));
            }
            if (!config.contains(SkillValues.DESCRIPTION)) {
                if (skill.getDescription() == null)
                    config.set(SkillValues.DESCRIPTION, new ArrayList<String>());
                else config.set(SkillValues.DESCRIPTION, skill.getDescription());
            }

            // Add it to the list
            skills.put(skill.getName().toLowerCase(), skill);
            configFile.saveConfig();

            // Register any listeners for skills
            if (skill instanceof Listener) {
                Listener listener = (Listener)skill;
                getServer().getPluginManager().registerEvents(listener, this);
            }
        }
        catch (Exception e) {
            getLogger().severe("Failed to register skill: " + skill.getName() + " - invalid returned values");
            config.set(SkillValues.ROOT + "." + skill.getName(), null);
        }
    }

    /**
     * <p>Adds multiple skills to the game</p>
     * <p>This must be done in the SkillPlugin.registerSkills(SkillAPI) method when the API calls it.
     * This is to ensure the correct assigning of skills to classes by registering them in the proper sequence.</p>
     *
     * @param skills skills to add
     */
    public void addSkills(ClassSkill ... skills) {
        for (ClassSkill skill : skills)
            addSkill(skill);
    }

    /**
     * <p>Copies dynamic skill data from the config into the API</p>
     * <p>This must be done in SkillPlugin.registerSkills(SkillAPI) else the copied data will not be loaded
     * and will be overwritten when the plugin is disabled.</p>
     *
     * @param config config containing dynamic skill data
     */
    public void loadDynamicSkills(ConfigurationSection config) {
        for (String key : config.getKeys(false)) {
            if (!skillConfig.getConfig().contains(key)) {
                skillConfig.getConfig().set(key, config.getConfigurationSection(key));
            }
        }
        skillConfig.saveConfig();
    }

    /**
     * <p>Adds a new class to the game</p>
     * <p>This must be done in the SkillPlugin.registerClasses(SkillAPI) method when the API calls it.
     * This is to ensure the correct assigning of skills to classes by registering them in the proper sequence.</p>
     *
     * @param customClass class to add
     */
    public void addClass(CustomClass customClass) {

        if (mode != RegisterMode.CLASS) throw new IllegalStateException("Cannot register classes outside of the registerClasses method");

        // Validate the name
        if (customClass.getName() == null) {
            getLogger().severe("Could not register class - " + customClass.getClass().getName() + " - null name");
            return;
        }

        // Don't allow duplicate names
        else if (classes.containsKey(customClass.getName().toLowerCase())) {
            getLogger().severe("Duplicate class names detected! - " + customClass.getName());
            return;
        }

        // Make sure the class has the right attributes
        customClass.checkDefault(ClassAttribute.HEALTH, 20, 0);
        customClass.checkDefault(ClassAttribute.MANA, 100, 0);

        // Detect if default values are needed
        Config configFile = new Config(this, "class\\" + customClass.getName());
        ConfigurationSection config = configFile.getConfig();
        boolean neededOnly = config.getKeys(false).size() != 0;

        // Save values to config
        try {
            if (!config.contains(ClassValues.PREFIX))
                config.set(ClassValues.PREFIX, customClass.getPrefix().replace(ChatColor.COLOR_CHAR, '&'));
            if (customClass.getParent() != null && !neededOnly)
                config.set(ClassValues.PARENT, customClass.getParent());
            if (!config.contains(ClassValues.LEVEL))
                config.set(ClassValues.LEVEL, customClass.getProfessLevel());
            if (customClass.getInheritance() != null && customClass.getInheritance().size() > 0 && !neededOnly)
                config.set(ClassValues.INHERIT, customClass.getInheritance());
            if (!config.contains(ClassValues.HEALTH_BASE))
                config.set(ClassValues.HEALTH_BASE, customClass.getBase(ClassAttribute.HEALTH));
            if (!config.contains(ClassValues.HEALTH_BONUS))
                config.set(ClassValues.HEALTH_BONUS, customClass.getScale(ClassAttribute.HEALTH));
            if (!config.contains(ClassValues.MANA_BASE))
                config.set(ClassValues.MANA_BASE, customClass.getBase(ClassAttribute.MANA));
            if (!config.contains(ClassValues.MANA_BONUS))
                config.set(ClassValues.MANA_BONUS, customClass.getScale(ClassAttribute.MANA));
            if (!config.contains(ClassValues.SKILLS))
                config.set(ClassValues.SKILLS, customClass.getSkills());
            if (!config.contains(ClassValues.MAX_LEVEL))
                config.set(ClassValues.MAX_LEVEL, customClass.getMaxLevel());
            if (!config.contains(ClassValues.MANA_NAME))
                config.set(ClassValues.MANA_NAME, customClass.getManaName());
            if (!config.contains(ClassValues.PASSIVE_MANA_GAIN))
                config.set(ClassValues.PASSIVE_MANA_GAIN, customClass.gainsMana());

            // Add to table
            classes.put(customClass.getName().toLowerCase(), customClass);
            configFile.saveConfig();
        }
        catch (Exception e) {
            getLogger().severe("Failed to register class - " + customClass.getName() + " - Invalid values");
            config.set(ClassValues.ROOT + "." + customClass.getName(), null);
        }
    }

    /**
     * <p>Adds multiple classes to the game</p>
     * <p>This must be done in the SkillPlugin.registerClasses(SkillAPI) method when the API calls it.
     * This is to ensure the correct assigning of skills to classes by registering them in the proper sequence.</p>
     *
     * @param classes classes to add
     */
    public void addClasses(CustomClass ... classes) {
        for (CustomClass customClass : classes) {
            addClass(customClass);
        }
    }

    /**
     * <p>Copies dynamic skill data from the config into the API</p>
     * <p>This must be done in SkillPlugin.registerClasses(SkillAPI) else the copied data will not be loaded
     * and will be overwritten when the plugin is disabled.</p>
     *
     * @param config config containing dynamic skill data
     */
    public void loadDynamicClasses(ConfigurationSection config) {
        for (String key : config.getKeys(false)) {
            if (!classConfig.getConfig().contains(key)) {
                classConfig.getConfig().set(key, config.getConfigurationSection(key));
            }
        }
        classConfig.saveConfig();
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

        String lower = name.toLowerCase();

        // If the player data doesn't exist, create a new instance
        if (!players.containsKey(lower)) {
            players.put(lower, new PlayerSkills(this, name));
        }

        // Return the player data
        return players.get(lower);
    }

    // ----------------------------- Data Accessor Methods -------------------------------------- //

    /**
     * @return DOTHelper used by the API
     */
    public DOTHelper getDOTHelper() {
        return dotHelper;
    }

    /**
     * <p>Checks if a class is loaded with the given name</p>
     * <p>The name is not case-sensitive</p>
     *
     * @param name class name
     * @return     true if loaded, false otherwise
     */
    public boolean hasClass(String name){
        return classes.containsKey(name.toLowerCase());
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
        return skills.get(name.toLowerCase()) != null;
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
        if (name == null) return null;
        return skills.get(name.toLowerCase());
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
        if (name == null) return null;
        return classes.get(name.toLowerCase());
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
        return skills.containsKey(name.toLowerCase());
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
        for (CustomClass t : classes.values()) {
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
        return sender.hasPermission(PermissionNodes.CLASS) || sender.hasPermission(PermissionNodes.CLASS + "." + t.getName());
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

    // ----------------------------- Language Methods -------------------------------------- //

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
            return message;
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
        return message;
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
