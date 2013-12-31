package com.sucy.skill;

import com.sucy.skill.api.CustomClass;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.StatusHolder;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Mechanic;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.util.TextSizer;
import com.sucy.skill.api.util.effects.DOTHelper;
import com.sucy.skill.api.util.effects.ParticleHelper;
import com.sucy.skill.click.ClickListener;
import com.sucy.skill.command.ClassCommander;
import com.sucy.skill.config.Config;
import com.sucy.skill.config.PlayerValues;
import com.sucy.skill.config.SettingValues;
import com.sucy.skill.language.OtherNodes;
import com.sucy.skill.mccore.CoreChecker;
import com.sucy.skill.mccore.PrefixManager;
import com.sucy.skill.task.InventoryTask;
import com.sucy.skill.task.ManaTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
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
    private final HashMap<Integer, StatusHolder> holders = new HashMap<Integer, StatusHolder>();

    // Utility
    private RegistrationManager registration;
    private DOTHelper dotHelper;

    // Tasks
    private InventoryTask invTask;
    private ManaTask manaTask;

    // Configurations
    private Config playerConfig;
    private Config languageConfig;

    // Settings
    private String treeType;
    private boolean mana;
    private boolean reset;
    private boolean oldHealth;
    private boolean levelBar;
    private boolean clickCombo;
    private boolean expOrbs;
    private boolean blockSpawnerExp;
    private boolean blockEggExp;
    private boolean blockCreativeExp;
    private int startingPoints;
    private int pointsPerLevel;
    private int messageRadius;
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
        languageConfig.saveDefaultConfig();
        BukkitHelper.initialize();

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
        blockSpawnerExp = getConfig().getBoolean(SettingValues.BLOCK_MOB_SPAWNER_EXP.path(), true);
        blockEggExp = getConfig().getBoolean(SettingValues.BLOCK_MOB_EGG_EXP.path(), true);
        blockCreativeExp = getConfig().getBoolean(SettingValues.BLOCK_CREATIVE_EXP.path(), true);
        expOrbs = getConfig().getBoolean(SettingValues.USE_EXP_ORBS.path(), false);
        messageRadius = getConfig().getInt(SettingValues.SKILL_MESSAGE_RADIUS.path(), 20);

        // Experience formula
        ConfigurationSection formula = getConfig().getConfigurationSection(SettingValues.EXP_FORMULA.path());
        x = formula.getInt("x");
        y = formula.getInt("y");
        z = formula.getInt("z");
        w = formula.getInt("w");

        // Register classes and skills
        registration = new RegistrationManager(this);
        registration.initialize();

        // Set up the mana task
        int manaFreq = getConfig().getInt(SettingValues.MANA_GAIN_FREQ.path());
        int manaGain = getConfig().getInt(SettingValues.MANA_GAIN_AMOUNT.path());
        if (mana) manaTask = new ManaTask(this, manaFreq, manaGain);

        // Set up the inventory task
        int playersPerTick = getConfig().getInt(SettingValues.PLAYERS_PER_CHECK.path());
        if (getConfig().getBoolean(SettingValues.LORE_REQUIREMENTS.path(), true)) invTask = new InventoryTask(this, playersPerTick);

        // Load experience yields
        ConfigurationSection section = getConfig().getConfigurationSection(SettingValues.KILLS.path());
        for (String mob : section.getKeys(false)) {
            exp.put(mob, section.getInt(mob));
        }

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

        // Setup Helper classes
        new SkillListener(this);
        new ClassCommander(this);
        if (clickCombo) new ClickListener(this);
        dotHelper = new DOTHelper(this);
        ParticleHelper.initialize();
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
        registration.clearData();
        exp.clear();
        players.clear();
        getServer().getScheduler().cancelTasks(this);
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
     * @return whether or not to use experience orbs instead of the table
     */
    public boolean usingExpOrbs() {
        return expOrbs;
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
