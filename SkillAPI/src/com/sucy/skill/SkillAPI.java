package com.sucy.skill;

import com.rit.sucy.scoreboard.BoardManager;
import com.sucy.skill.api.*;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.util.AttributeHelper;
import com.sucy.skill.command.ClassCommander;
import com.sucy.skill.command.TextSizer;
import com.sucy.skill.config.*;
import com.sucy.skill.skills.*;
import com.sucy.skill.skills.SkillTree;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Skill API</p>
 * <p>Developed by Steven Sucy (Eniripsa96)</p>
 * <p>Developed for the BukkitDev community</p>
 */
public class SkillAPI extends JavaPlugin {

    private Hashtable<String, Skill> skills = new Hashtable<String, Skill>();
    private Hashtable<String, SkillTree> trees = new Hashtable<String, SkillTree>();
    private Hashtable<String, PlayerSkills> players = new Hashtable<String, PlayerSkills>();
    private Hashtable<String, Integer> exp = new Hashtable<String, Integer>();
    private Hashtable<String, ClassSkill> registeredSkills = new Hashtable<String, ClassSkill>();
    private Hashtable<String, CustomClass> registeredClasses = new Hashtable<String, CustomClass>();
    private RegisterMode mode = RegisterMode.DONE;
    private Config playerConfig;
    private Config languageConfig;
    private ManaTask task;
    private boolean sbEnabled;
    private boolean mana;
    private boolean reset;

    // ----------------------------- Plugin Methods -------------------------------------- //

    /**
     * Initializes plugin resources
     */
    @Override
    public void onEnable() {

        saveDefaultConfig();
        playerConfig = new Config(this, "players");
        languageConfig = new Config(this, "language");
        languageConfig.saveDefaultConfig();
        reload();
        getLogger().info("Loaded " + skills.size() + " skills and " + trees.size() + " skill trees");

        new ClassCommander(this);
    }

    /**
     * Clears all plugin data after saving
     */
    @Override
    public void onDisable() {

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
            player.setMaxHealth(20);
        }

        clear();
        players.clear();
    }

    // ----------------------------- Data Management Methods -------------------------------------- //

    /**
     * Clears all plugin data
     */
    private void clear() {

        // Tasks
        if (task != null)
            task.cancel();

        // Clear listeners
        HandlerList.unregisterAll(this);

        // Clear scoreboards
        PrefixManager.clearAll();

        // Clear all data
        skills.clear();
        trees.clear();
    }

    /**
     * Saves player data to the config
     *
     * @param player player name
     */
    public void savePlayer(String player) {
        player = player.toLowerCase();
        if (!players.containsKey(player)) return;
        players.get(player).save(playerConfig.getConfig(), PlayerValues.ROOT + "." + player + ".");
    }

    /**
     * Loads all data for the plugin
     */
    public void reload() {

        clear();

        // Request skills first
        mode = RegisterMode.SKILL;
        for (Plugin plugin : getServer().getPluginManager().getPlugins()) {
            if (plugin instanceof SkillPlugin) {
                ((SkillPlugin) plugin).registerSkills(this);
            }
        }

        // Register classes after
        mode = RegisterMode.CLASS;
        for (Plugin plugin : getServer().getPluginManager().getPlugins()) {
            if (plugin instanceof SkillPlugin) {
                ((SkillPlugin) plugin).registerClasses(this);
            }
        }

        // Done registering everything
        mode = RegisterMode.DONE;

        // Load options
        reset = getConfig().getBoolean("profess-reset");
        mana = getConfig().getBoolean("mana-enabled");
        sbEnabled = getConfig().getBoolean("scoreboard-enabled");

        // Set up the mana task
        int manaFreq = getConfig().getInt("mana-gain-freq");
        int manaGain = getConfig().getInt("mana-gain-amount");
        if (mana) task = new ManaTask(this, manaFreq, manaGain);

        // Load experience yields
        if (getConfig().contains("kills")) {
            ConfigurationSection section = getConfig().getConfigurationSection("kills");
            for (String mob : section.getKeys(false)) {
                exp.put(mob, section.getInt(mob));
            }
        }
        else getLogger().severe("No experience yields found! - Players will not be able to level up!");

        // Load skill data
        for (ClassSkill skill : registeredSkills.values()) {
            try {
                Config skillConfig = new Config(this, "skill\\" + skill.getName());
                skills.put(skill.getName().toLowerCase(), new Skill(this, skill.getName(), skillConfig.getConfig()));
            }
            catch (Exception e) {
                getLogger().severe("Failed to load skill: " + skill);
            }
        }

        // Load skill tree data
        for (CustomClass tree : registeredClasses.values()) {
            try {
                Config treeConfig = new Config(this, "class\\" + tree.getName());
                trees.put(tree.getName().toLowerCase(), new SkillTree(this, tree.getName(), treeConfig.getConfig()));
            }
            catch (SkillTreeException e) {
                getLogger().warning("Failed to load skill tree - " + tree + " - Reason: " + e.getMessage());
            }
        }

        // Load player data
        if (playerConfig.getConfig().contains(PlayerValues.ROOT) && playerConfig.getConfig().getConfigurationSection(PlayerValues.ROOT).getKeys(false) != null) {
            for (String player : playerConfig.getConfig().getConfigurationSection(PlayerValues.ROOT).getKeys(false)) {
                players.put(player.toLowerCase(), new PlayerSkills(this, player, playerConfig.getConfig().getConfigurationSection(PlayerValues.ROOT + "." + player)));
                if (getServer().getPlayer(player) != null)
                    players.get(player.toLowerCase()).updateHealth();
            }
        }

        // Append player data
        for (Player player : getServer().getOnlinePlayers()) {
            if (!players.containsKey(player.getName().toLowerCase()))
                players.put(player.getName().toLowerCase(), new PlayerSkills(this, player.getName()));
        }

        // Listener
        new APIListener(this);
    }

    // ----------------------------- Settings Accessor Methods -------------------------------------- //

    /**
     * @return whether or not mana is enabled
     */
    public boolean isManaEnabled() {
        return mana;
    }

    /**
     * @return whether or not scoreboards are enabled
     */
    public boolean areScoreboardsEnabled() {
        return sbEnabled;
    }

    /**
     * @return whether or not professing resets player levels
     */
    public boolean doProfessionsReset() {
        return reset;
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
        else if (registeredSkills.containsKey(skill.getName().toLowerCase())) {
            getLogger().severe("Duplicate skill names detected! - " + skill.getName());
            return;
        }

        // Detect if default values are needed
        Config configFile = new Config(this, "skill\\" + skill.getName());
        ConfigurationSection config = configFile.getConfig();
        boolean neededOnly = config.getKeys(false).size() != 0;

        // Save default values
        try {
            if (!config.contains(SkillValues.MAX_LEVEL.getKey()))
                config.set(SkillValues.MAX_LEVEL.getKey(), skill.getMaxLevel() < 1 ? 1 : skill.getMaxLevel());
            if (!config.contains(SkillValues.INDICATOR.getKey()))
                config.set(SkillValues.INDICATOR.getKey(), skill.getIndicator().name());
            if (skill.getSkillReq() != null && !neededOnly)
                config.set(SkillValues.SKILL_REQ.getKey(), skill.getSkillReq());
            for (String attribute : AttributeHelper.getAllAttributes(skill)) {
                if (!config.contains(attribute + "-base"))
                    config.set(attribute + "-base", skill.getBase(attribute));
                if (!config.contains(attribute + "-scale"))
                    config.set(attribute + "-scale", skill.getScale(attribute));
            }
            if (!config.contains(SkillValues.DESCRIPTION.getKey())) {
                if (skill.getDescription() == null)
                    config.set(SkillValues.DESCRIPTION.getKey(), new ArrayList<String>());
                else config.set(SkillValues.DESCRIPTION.getKey(), skill.getDescription());
            }

            // Add it to the list
            registeredSkills.put(skill.getName().toLowerCase(), skill);
            configFile.saveConfig();

            // Register any listeners for skills
            if (skill instanceof Listener) {
                Listener listener = (Listener)skill;
                getServer().getPluginManager().registerEvents(listener, this);
            }
        }
        catch (Exception e) {
            getLogger().severe("Failed to register skill: " + skill.getName() + " - invalid returned values");
            config.set(SkillValues.ROOT.getKey() + "." + skill.getName(), null);
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
        else if (registeredClasses.containsKey(customClass.getName().toLowerCase())) {
            getLogger().severe("Duplicate class names detected! - " + customClass.getName());
            return;
        }

        // Detect if default values are needed
        Config configFile = new Config(this, "class\\" + customClass.getName());
        ConfigurationSection config = configFile.getConfig();
        boolean neededOnly = config.getKeys(false).size() != 0;

        // Save values to config
        try {
            if (!config.contains(TreeValues.PREFIX))
                config.set(TreeValues.PREFIX, customClass.getPrefix().replace(ChatColor.COLOR_CHAR, '&'));
            if (customClass.getParent() != null && !neededOnly)
                config.set(TreeValues.PARENT, customClass.getParent());
            if (!config.contains(TreeValues.LEVEL))
                config.set(TreeValues.LEVEL, customClass.getProfessLevel());
            if (customClass.getInheritance() != null && customClass.getInheritance().size() > 0 && !neededOnly)
                config.set(TreeValues.INHERIT, customClass.getInheritance());
            if (!config.contains(TreeValues.HEALTH_BASE))
                config.set(TreeValues.HEALTH_BASE, customClass.getBase(ClassAttributes.HEALTH));
            if (!config.contains(TreeValues.HEALTH_BONUS))
                config.set(TreeValues.HEALTH_BONUS, customClass.getScale(ClassAttributes.HEALTH));
            if (!config.contains(TreeValues.MANA_BASE))
                config.set(TreeValues.MANA_BASE, customClass.getBase(ClassAttributes.MANA));
            if (!config.contains(TreeValues.MANA_BONUS))
                config.set(TreeValues.MANA_BONUS, customClass.getScale(ClassAttributes.MANA));
            if (!config.contains(TreeValues.SKILLS))
                config.set(TreeValues.SKILLS, customClass.getSkills());

            // Add to table
            registeredClasses.put(customClass.getName().toLowerCase(), customClass);
            configFile.saveConfig();
        }
        catch (Exception e) {
            getLogger().severe("Failed to register class - " + customClass.getName() + " - Invalid values");
            config.set(TreeValues.ROOT + "." + customClass.getName(), null);
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
        for (CustomClass customClass : classes)
            addClass(customClass);
    }

    // ----------------------------- Player Methods -------------------------------------- //

    /**
     * Retrieves data for a player
     *
     * @param name player name
     * @return     player class data
     */
    public PlayerSkills getPlayer(String name) {
        return players.get(name.toLowerCase());
    }

    /**
     * Adds player data to the api
     *
     * @param player player data to add
     */
    public void addPlayer(PlayerSkills player) {
        players.put(player.getName().toLowerCase(), player);
    }

    // ----------------------------- Data Accessor Methods -------------------------------------- //

    /**
     * <p>Gets the Class data that manages the skill tree of the class</p>
     *
     * @param name class name
     * @return     class skill tree
     */
    public SkillTree getClass(String name) {
        return trees.get(name.toLowerCase());
    }

    /**
     * Checks if the class with the given name is loaded
     *
     * @param name class name
     * @return     true if loaded, false otherwise
     */
    public boolean hasTree(String name){
        return trees.containsKey(name.toLowerCase());
    }

    /**
     * Gets the config skill
     *
     * @param name skill name
     * @return     config data
     */
    public Skill getSkill(String name) {
        return skills.get(name.toLowerCase());
    }

    /**
     * Gets all child skills of the skill with the given name
     *
     * @param name skill name
     * @return     all child skills
     */
    public ArrayList<Skill> getChildSkills(String name) {
        ArrayList<Skill> skills = new ArrayList<Skill>();
        for (Skill skill : skills) {
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
        return skills.containsKey(name.toLowerCase());
    }

    /**
     * Gets the registered ClassSkill
     *
     * @param name skill name
     * @return     class skill
     */
    public ClassSkill getRegisteredSkill(String name) {
        return registeredSkills.get(name.toLowerCase());
    }

    /**
     * Gets the registered CustomClass
     *
     * @param name class name
     * @return     class
     */
    public CustomClass getRegisteredClass(String name) {
        return registeredClasses.get(name.toLowerCase());
    }

    /**
     * Checks if a skill was registered
     *
     * @param name skill name
     * @return     true if registered, false otherwise
     */
    public boolean isSkillRegistered(String name) {
        return registeredSkills.containsKey(name.toLowerCase());
    }

    /**
     * Gets the children for the skill tree
     *
     * @param tree tree name
     * @return     name of all children
     */
    public ArrayList<String> getChildren(String tree) {
        ArrayList<String> children = new ArrayList<String>();
        for (SkillTree t : trees.values()) {
            if (tree == null) {
                if (t.getParent() == null) children.add(t.getName());
            }
            else {
                if (t.getName().equalsIgnoreCase(tree)) continue;
                if (t.getParent() == null) continue;
                if (t.getParent().equalsIgnoreCase(tree)) {
                    children.add(t.getName());
                }
            }
        }
        return children;
    }

    // ----------------------------- Language Methods -------------------------------------- //

    /**
     * @return language configuration
     */
    public ConfigurationSection getLanguageConfig() {
        languageConfig.reloadConfig();
        return languageConfig.getConfig();
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
     * Applies the global filters to the string
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
