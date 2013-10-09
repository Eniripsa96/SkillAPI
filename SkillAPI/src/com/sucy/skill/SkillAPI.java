package com.sucy.skill;

import com.sucy.skill.api.*;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.TargetSkill;
import com.sucy.skill.command.ClassCommander;
import com.sucy.skill.api.util.TextSizer;
import com.sucy.skill.config.*;
import com.sucy.skill.mccore.CoreChecker;
import com.sucy.skill.mccore.PrefixManager;
import com.sucy.skill.skills.*;
import com.sucy.skill.skills.SkillTree;
import com.sucy.skill.task.InventoryTask;
import com.sucy.skill.task.ManaTask;
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
    private InventoryTask invTask;
    private ManaTask manaTask;
    private Config playerConfig;
    private Config languageConfig;
    private boolean sbEnabled;
    private boolean mana;
    private boolean reset;
    private boolean oldHealth;
    private int startingPoints;

    // ----------------------------- Plugin Methods -------------------------------------- //

    /**
     * Initializes plugin resources
     */
    @Override
    public void onEnable() {

        reloadConfig();
        saveDefaultConfig();
        playerConfig = new Config(this, "players");
        languageConfig = new Config(this, "language");
        languageConfig.saveDefaultConfig();

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
        startingPoints = getConfig().getInt("starting-points");
        oldHealth = getConfig().getBoolean("old-health-bar");

        // Set up the mana task
        int manaFreq = getConfig().getInt("mana-gain-freq");
        int manaGain = getConfig().getInt("mana-gain-amount");
        if (mana) manaTask = new ManaTask(this, manaFreq, manaGain);

        // Set up the inventory task
        int playersPerTick = getConfig().getInt("players-per-check");
        invTask = new InventoryTask(this, playersPerTick);

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

        getLogger().info("Loaded " + skills.size() + " skills and " + trees.size() + " skill trees");

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
    }

    /**
     * Clears all plugin data after saving
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
            player.setMaxHealth(20);
        }

        // Clear scoreboards
        if (CoreChecker.isCoreActive())
            PrefixManager.clearAll();

        // Clear all data
        skills.clear();
        trees.clear();
        registeredSkills.clear();
        registeredClasses.clear();
        exp.clear();
        players.clear();
    }

    // ----------------------------- Data Management Methods -------------------------------------- //

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
     * @return number of skill points players start with
     */
    public int getStartingPoints() {
        return startingPoints;
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

            // Add to table
            registeredClasses.put(customClass.getName().toLowerCase(), customClass);
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
        if (name == null) return null;
        return registeredSkills.get(name.toLowerCase());
    }

    /**
     * Gets the registered CustomClass
     *
     * @param name class name
     * @return     class
     */
    public CustomClass getRegisteredClass(String name) {
        if (name == null) return null;
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

    /**
     * Gets the children for the skill tree considering the player's permissions
     *
     * @param tree   parent skill tree
     * @param player player to check for
     * @return       list of available child skill trees
     */
    public ArrayList<String> getChildren(String tree, Player player) {
        ArrayList<String> children = new ArrayList<String>();
        for (SkillTree t : trees.values()) {
            if (tree == null) {
                if (t.getParent() == null && hasPermission(player, t)) children.add(t.getName());
            }
            else {
                if (t.getName().equalsIgnoreCase(tree)) continue;
                if (t.getParent() == null) continue;
                if (!hasPermission(player, t)) continue;
                if (t.getParent().equalsIgnoreCase(tree)) {
                    children.add(t.getName());
                }
            }
        }
        return children;
    }

    /**
     * Checks if the player has permission to use the given class
     *
     * @param player player to check for
     * @param t      class to check
     * @return       true if the player has permission, false otherwise
     */
    public boolean hasPermission(Player player, SkillTree t) {
        return player.hasPermission(PermissionNodes.CLASS) || player.hasPermission(PermissionNodes.CLASS + "." + t.getName());
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
