/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
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

package com.sucy.skill;

import com.rit.sucy.config.CommentedConfig;
import com.rit.sucy.config.CommentedLanguageConfig;
import com.rit.sucy.version.VersionManager;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.particle.EffectManager;
import com.sucy.skill.api.particle.Particle;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.data.PlayerStats;
import com.sucy.skill.data.Settings;
import com.sucy.skill.data.io.ConfigIO;
import com.sucy.skill.data.io.IOManager;
import com.sucy.skill.data.io.SQLIO;
import com.sucy.skill.dynamic.DynamicClass;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.gui.tool.GUITool;
import com.sucy.skill.hook.BungeeHook;
import com.sucy.skill.hook.PlaceholderAPIHook;
import com.sucy.skill.hook.PluginChecker;
import com.sucy.skill.listener.*;
import com.sucy.skill.manager.*;
import com.sucy.skill.packet.PacketInjector;
import com.sucy.skill.task.CooldownTask;
import com.sucy.skill.task.GUITask;
import com.sucy.skill.task.ManaTask;
import com.sucy.skill.task.SaveTask;
import com.sucy.skill.thread.MainThread;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>The main class of the plugin which has the accessor methods into most of the API.</p>
 * <p>You can retrieve a reference to this through Bukkit the same way as any other plugin.</p>
 */
public class SkillAPI extends JavaPlugin {
    private static SkillAPI singleton;

    private final HashMap<String, Skill>          skills  = new HashMap<>();
    private final HashMap<String, RPGClass>       classes = new HashMap<>();
    public final HashMap<String, PlayerAccounts> players = new HashMap<>();
    private final ArrayList<String>               groups  = new ArrayList<>();

    private final List<SkillAPIListener> listeners = new ArrayList<>();

    private CommentedLanguageConfig language;
    private Settings                settings;

    private IOManager           io;
    private CmdManager          cmd;
    private ComboManager        comboManager;
    private RegistrationManager registrationManager;
    private AttributeManager    attributeManager;

    private MainThread mainThread;
    private BukkitTask manaTask;

    private boolean loaded = false;
    private boolean disabling = false;
    private boolean enabledSkillBars = true;

    public boolean isSkillBarsEnabled() {
        return enabledSkillBars;
    }

    public void setSkillBarsEnabled(boolean newValue) {
        enabledSkillBars = newValue;
    }

    /**
     * <p>Enables SkillAPI, setting up listeners, managers, and loading data. This
     * should not be called by other plugins.</p>
     */
    @Override
    public void onEnable() {
        // Set up the singleton
        if (singleton != null) { throw new IllegalStateException("Cannot enable SkillAPI twice!"); }
        singleton = this;

        mainThread = new MainThread();
        Particle.init();
        EffectManager.init();

        // Load settings
        settings = new Settings(this);
        language = new CommentedLanguageConfig(this, "language");
        language.checkDefaults();
        language.trim();
        language.save();

        // Hook plugins
        if (PluginChecker.isBungeeActive()) { BungeeHook.init(this); }

        // Set up managers
        comboManager = new ComboManager();
        registrationManager = new RegistrationManager(this);
        cmd = new CmdManager(this);
        io = settings.isUseSql() ? new SQLIO(this) : new ConfigIO(this);
        PlayerStats.init();
        ClassBoardManager.registerText();
        if (settings.isAttributesEnabled()) { attributeManager = new AttributeManager(this); }

        // Load classes and skills
        registrationManager.initialize();

        // Load group settings after groups are determined
        settings.loadGroupSettings();

        // Set up listeners
        listen(new BindListener(), true);
        listen(new BuffListener(), true);
        listen(new MainListener(this), true);
        listen(new MechanicListener(), true);
        listen(new StatusListener(), true);
        listen(new ToolListener(), true);
        listen(new KillListener(), true);
        listen(new AddonListener(), true);
        listen(new ItemListener(this), settings.isCheckLore());
        listen(new BarListener(), settings.isSkillBarEnabled());
        if (VersionManager.isVersionAtLeast(VersionManager.V1_8_0)) {
            final PacketInjector injector = new PacketInjector(this);
            listen(new PacketListener(injector), true);
            listen(new ClickListener(), settings.isCombosEnabled());
        }
        listen(new NeoComboListener(), true);
        listen(new ComboListener(), settings.isCombosEnabled());
        listen(new AttributeListener(), settings.isAttributesEnabled());
        listen(new CastListener(), settings.isUsingBars());
        listen(
                new CastOffhandListener(),
                settings.isCastEnabled() && VersionManager.isVersionAtLeast(VersionManager.V1_9_0));
        listen(new CastItemListener(), settings.isUsingWand());
        listen(new CastCombatListener(), settings.isUsingCombat());
        listen(new DeathListener(), !VersionManager.isVersionAtLeast(11000));
        listen(new LingeringPotionListener(), VersionManager.isVersionAtLeast(VersionManager.V1_9_0));
        listen(new ExperienceListener(), settings.yieldsEnabled());

        // Set up tasks (Mana task disabled because it must be async)
        // if (settings.isManaEnabled()) { MainThread.register(new ManaTask()); }
        if (settings.isSkillBarCooldowns()) { MainThread.register(new CooldownTask()); }
        if (settings.isAutoSave()) { MainThread.register(new SaveTask(this)); }
        MainThread.register(new GUITask(this));

        GUITool.init();

        // Load player data
        players.putAll(io.loadAll());
        for (PlayerAccounts accounts : players.values()) { accounts.getActiveData().init(accounts.getPlayer()); }

        // Must initialize listeners AFTER player data is loaded since the
        // player objects would otherwise change and mess a lot of things up.
        for (SkillAPIListener listener : listeners) {
            listener.init();
        }
        

	    // Non-task mana gain
        if (settings.isManaEnabled()) {
            if (VersionManager.isVersionAtLeast(11400)) {
                manaTask = Bukkit.getScheduler().runTaskTimer(
                        this,
                        new ManaTask(),
                        SkillAPI.getSettings().getGainFreq(),
                        SkillAPI.getSettings().getGainFreq()
                );
            } else {
                MainThread.register(new ManaTask());
            }
        }

        // Removed due des-use
//        ResourceManager.copyQuestsModule();
        PlaceholderAPIHook.registerPlaceholder(this);

        loaded = true;
    }

    private void listen(SkillAPIListener listener, boolean enabled) {
        if (enabled) {
            Bukkit.getPluginManager().registerEvents(listener, this);
            listeners.add(listener);
        }
    }

    /**
     * <p>Disables SkillAPI, saving data before unloading everything and disconnecting
     * listeners. This should not be called by other plugins.</p>
     */
    @Override
    public void onDisable() {
        // Validate instance
        if (singleton != this) { throw new IllegalStateException("This is not a valid, enabled SkillAPI copy!"); }

        disabling = true;

        GUITool.cleanUp();
        EffectManager.cleanUp();

        mainThread.disable();
        mainThread = null;
        
        if (manaTask != null) {
            manaTask.cancel();
            manaTask = null;
        }


        for (SkillAPIListener listener : listeners) { listener.cleanup(); }
        listeners.clear();

        // Clear scoreboards
        ClassBoardManager.clearAll();

        // Clear skill bars and stop passives before disabling
        for (Player player : VersionManager.getOnlinePlayers()) {
            MainListener.unload(player);
        }

        // Only save on disable if not an instance
        if (!settings.isInstance()) {
            io.saveAll();
        }

        skills.clear();
        classes.clear();
        players.clear();

        HandlerList.unregisterAll(this);
        cmd.clear();

        loaded = false;
        disabling = false;
        singleton = null;
    }

    /**
     * Checks whether or not SkillAPI has all its
     * data loaded and running.
     *
     * @return true if loaded and set up, false otherwise
     */
    public static boolean isLoaded() {
        return singleton != null && singleton.loaded;
    }

    /**
     * @return SkillAPI singleton if available
     *
     * @throws IllegalStateException if SkillAPI isn't enabled
     */
    private static SkillAPI singleton() {
        if (singleton == null) {
            throw new IllegalStateException(
                    "Cannot use SkillAPI methods before it is enabled - add it to your plugin.yml as a dependency");
        }
        return singleton;
    }

    /**
     * Retrieves the settings data controlling SkillAPI
     *
     * @return SkillAPI settings data
     */
    public static Settings getSettings() {
        return singleton().settings;
    }

    /**
     * Retrieves the language file data for SkillAPI
     *
     * @return SkillAPI language file data
     */
    public static CommentedLanguageConfig getLanguage() {
        return singleton().language;
    }

    /**
     * Retrieves the manager for click cast combos
     *
     * @return click combo manager
     */
    public static ComboManager getComboManager() {
        return singleton().comboManager;
    }

    /**
     * Retrieves the attribute manager for SkillAPI
     *
     * @return attribute manager
     */
    public static AttributeManager getAttributeManager() {
        return singleton().attributeManager;
    }

    /**
     * Retrieves a skill by name. If no skill is found with the name, null is
     * returned instead.
     *
     * @param name name of the skill
     *
     * @return skill with the name or null if not found
     */
    public static Skill getSkill(String name) {
        if (name == null) { return null; }
        return singleton().skills.get(name.toLowerCase());
    }

    /**
     * Retrieves the registered skill data for SkillAPI. It is recommended that you
     * don't edit this map. Instead, use "addSkill" and "addSkills" instead.
     *
     * @return the map of registered skills
     */
    public static HashMap<String, Skill> getSkills() {
        return singleton().skills;
    }

    /**
     * Checks whether or not a skill is registered.
     *
     * @param name name of the skill
     *
     * @return true if registered, false otherwise
     */
    public static boolean isSkillRegistered(String name) {
        return getSkill(name) != null;
    }

    /**
     * Checks whether or not a skill is registered
     *
     * @param skill the skill to check
     *
     * @return true if registered, false otherwise
     */
    public static boolean isSkillRegistered(PlayerSkill skill) {
        return isSkillRegistered(skill.getData().getName());
    }

    /**
     * Checks whether or not a skill is registered
     *
     * @param skill the skill to check
     *
     * @return true if registered, false otherwise
     */
    public static boolean isSkillRegistered(Skill skill) {
        return isSkillRegistered(skill.getName());
    }

    /**
     * Retrieves a class by name. If no skill is found with the name, null is
     * returned instead.
     *
     * @param name name of the class
     *
     * @return class with the name or null if not found
     */
    public static RPGClass getClass(String name) {
        if (name == null) { return null; }
        return singleton().classes.get(name.toLowerCase());
    }

    /**
     * Retrieves the registered class data for SkillAPI. It is recommended that you
     * don't edit this map. Instead, use "addClass" and "addClasses" instead.
     *
     * @return the map of registered skills
     */
    public static HashMap<String, RPGClass> getClasses() {
        return singleton().classes;
    }

    /**
     * Retrieves a list of base classes that don't profess from another class
     *
     * @return the list of base classes
     */
    public static ArrayList<RPGClass> getBaseClasses(String group) {
        ArrayList<RPGClass> list = new ArrayList<>();
        for (RPGClass c : singleton.classes.values()) {
            if (!c.hasParent() && c.getGroup().equals(group)) { list.add(c); }
        }
        return list;
    }

    /**
     * Checks whether or not a class is registered.
     *
     * @param name name of the class
     *
     * @return true if registered, false otherwise
     */
    public static boolean isClassRegistered(String name) {
        return getClass(name) != null;
    }

    /**
     * Checks whether or not a class is registered.
     *
     * @param playerClass the class to check
     *
     * @return true if registered, false otherwise
     */
    public static boolean isClassRegistered(PlayerClass playerClass) {
        return isClassRegistered(playerClass.getData().getName());
    }

    /**
     * Checks whether or not a class is registered.
     *
     * @param rpgClass the class to check
     *
     * @return true if registered, false otherwise
     */
    public static boolean isClassRegistered(RPGClass rpgClass) {
        return isClassRegistered(rpgClass.getName());
    }

    /**
     * Retrieves the active class data for the player. If no data is found for the
     * player, a new set of data will be created and returned.
     *
     * @param player player to get the data for
     *
     * @return the class data of the player
     */
	public static PlayerData getPlayerData(OfflinePlayer player) {
        if (player == null) { return null; }
        PlayerAccounts account = getPlayerAccountData(player);
        return account == null ? null : account.getActiveData();
    }
    
    public static PlayerAccounts loadPlayerDataSQL(OfflinePlayer player) {
        if (player == null) { return null; }
        
        // Load the data
        return doLoad(player);
    }

    /**
     * Loads the data for a player when they join the server. This is handled
     * by the API and doesn't need to be used elsewhere unless you want to
     * load a player's data without them logging on. This should be run
     * asynchronously since it is loading configuration files.
     *
     * @param player player to load the data for
     */
    public static PlayerAccounts loadPlayerData(OfflinePlayer player) {
        if (player == null) { return null; }

        // Already loaded for some reason, no need to load again
        String id = new VersionPlayer(player).getIdString();
        if (singleton().players.containsKey(id)) { return singleton.players.get(id); }
        
        // Load the data
        return doLoad(player);
    }
    
    private static PlayerAccounts doLoad(OfflinePlayer player) {
        // Load the data
        PlayerAccounts data = singleton.io.loadData(player);
        singleton.players.put(player.getUniqueId().toString(), data);
        return data;
    }

    /**
     * Used to fake player data until SQL data is loaded when both SQL and the SQL delay are enabled.
     * This should not be used by other plugins. If the player data already exists, this does nothing.
     *
     * @param player player to fake data for
     */
    public static void initFakeData(final OfflinePlayer player) {
        singleton().players.computeIfAbsent(player.getUniqueId().toString(), id -> new PlayerAccounts(player));
    }

    /**
     * Do not use this method outside of onJoin. This will delete any progress a player
     * has made since joining.
     */
    public static void reloadPlayerData(final Player player) {
    	doLoad(player);
    }

    /**
     * Saves all player data to the configs. This
     * should be called asynchronously to avoid problems
     * with the main server loop.
     */
    public static void saveData() {
        singleton().io.saveAll();
    }
    
    public static void saveSingle(Player p) {
    	PlayerAccounts data = getPlayerAccountData(p);
    	singleton().io.saveData(data);
    }
    
    public static boolean isLoaded(Player p) {
    	return !MainListener.loadingPlayers.containsKey(p.getUniqueId());
    }

    /**
     * Checks whether or not SkillAPI currently has loaded data for the
     * given player. This returning false doesn't necessarily mean the
     * player doesn't have any data at all, just not data that is
     * currently loaded.
     *
     * @param player player to check for
     *
     * @return true if has loaded data, false otherwise
     */
    public static boolean hasPlayerData(OfflinePlayer player) {
        return singleton != null && player != null && singleton.players.containsKey(new VersionPlayer(player).getIdString());
    }

    /**
     * Unloads player data from memory, saving it to the config
     * first and then removing it from the map.
     *
     * @param player player to unload data for
     */
    public static void unloadPlayerData(final OfflinePlayer player) {
    	unloadPlayerData(player, false);
    }
    
    public static void unloadPlayerData(final OfflinePlayer player, final boolean skipSaving) {
        if (singleton == null || player == null || singleton.disabling || !singleton.players.containsKey(new VersionPlayer(player).getIdString())) {
            return;
        }

        singleton.getServer().getScheduler().runTaskAsynchronously(singleton, () -> {
            PlayerAccounts accounts = getPlayerAccountData(player);
        	singleton.io.saveData(accounts);
            singleton.players.remove(new VersionPlayer(player).getIdString());
        });
    }

    /**
     * Retrieves all class data for the player. This includes the active and
     * all inactive accounts the player has. If no data is found, a new set
     * of data will be created and returned.
     *
     * @param player player to get the data for
     *
     * @return the class data of the player
     */
    public static PlayerAccounts getPlayerAccountData(OfflinePlayer player) {
        if (player == null) { return null; }

        String id = new VersionPlayer(player).getIdString();
        if (!singleton().players.containsKey(id)) {
            PlayerAccounts data = loadPlayerData(player);
            singleton.players.put(id, data);
            return data;
        } else { return singleton.players.get(id); }
    }

    /**
     * Retrieves all the player data of SkillAPI. It is recommended not to
     * modify this map. Instead, use helper methods within individual player data.
     *
     * @return all SkillAPI player data
     */
    public static HashMap<String, PlayerAccounts> getPlayerAccountData() {
        return singleton().players;
    }

    /**
     * Retrieves the list of active class groups used by
     * registered classes
     *
     * @return list of active class groups
     */
    public static List<String> getGroups() {
        return singleton().groups;
    }

    /**
     * This adds a dynamic skill to the skill list. This should
     * not be called by other plugins.
     *
     * @param skill the dynamic skill to register
     */
    public void addDynamicSkill(DynamicSkill skill) {
        if (registrationManager.isAddingDynamicSkills()) { skills.put(skill.getName().toLowerCase(), skill); } else {
            throw new IllegalStateException("Cannot add dynamic skills from outside SkillAPI");
        }
    }

    /**
     * Registers a new skill with SkillAPI. If this is called outside of the method
     * provided in SkillPlugin, this will throw an error. You should implement SkillPlugin
     * in your main class and call this from the provided "registerSkills" method.
     *
     * @param skill skill to register
     */
    public void addSkill(Skill skill) {
        skill = registrationManager.validate(skill);
        if (skill != null) { skills.put(skill.getName().toLowerCase(), skill); }
    }

    /**
     * Registers multiple new skills with SkillAPI. If this is called outside of the method
     * provided in SkillPlugin, this will throw an error. You should implement SkillPlugin
     * in your main class and call this from the provided "registerSkills" method.
     *
     * @param skills skills to register
     */
    public void addSkills(Skill... skills) {
        for (Skill skill : skills) { addSkill(skill); }
    }

    /**
     * Registers a new class with SkillAPI. If this is called outside of the method
     * provided in SkillPlugin, this will throw an error. You should implement SkillPlugin
     * in your main class and call this from the provided "registerClasses" method.
     *
     * @param rpgClass class to register
     */
    public void addClass(RPGClass rpgClass) {
        rpgClass = registrationManager.validate(rpgClass);
        if (rpgClass != null) {
            classes.put(rpgClass.getName().toLowerCase(), rpgClass);
            ClassBoardManager.registerClass(rpgClass);
            if (!groups.contains(rpgClass.getGroup())) { groups.add(rpgClass.getGroup()); }
        }
    }

    /**
     * Adds a dynamic class which ignores validation. This should only
     * be used by the API as other plugins should use the regular addClass.
     *
     * @param rpgClass dynamic class to add
     */
    public void addDynamicClass(DynamicClass rpgClass) {
        String key;
        if (rpgClass != null && !classes.containsKey(key = rpgClass.getName().toLowerCase())) {
            classes.put(key, rpgClass);
            ClassBoardManager.registerClass(rpgClass);
            if (!groups.contains(rpgClass.getGroup())) { groups.add(rpgClass.getGroup()); }
        }
    }

    /**
     * Registers a new class with SkillAPI. If this is called outside of the method
     * provided in SkillPlugin, this will throw an error. You should implement SkillPlugin
     * in your main class and call this from the provided "registerClasses" method.
     *
     * @param classes classes to register
     */
    public void addClasses(RPGClass... classes) {
        for (RPGClass rpgClass : classes) { addClass(rpgClass); }
    }

    /**
     * Schedules a delayed task
     *
     * @param runnable the task to schedule
     * @param delay    the delay in ticks
     */
    public static BukkitTask schedule(BukkitRunnable runnable, int delay) {
        return runnable.runTaskLater(singleton(), delay);
    }

    /**
     * Schedules a delayed task
     *
     * @param runnable the task to schedule
     * @param delay    the delay in ticks
     */
    public static BukkitTask schedule(Runnable runnable, int delay) {
        return Bukkit.getScheduler().runTaskLater(singleton, runnable, delay);
    }

    /**
     * Schedules an async delayed task
     *
     * @param runnable the task to schedule
     * @param delay    the delay in ticks
     */
    public static BukkitTask scheduleAsync(Runnable runnable, int delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(singleton, runnable, delay);
    }

    /**
     * Schedules a repeating task
     *
     * @param runnable the task to schedule
     * @param delay    the delay in ticks before the first tick
     * @param period   how often to run in ticks
     */
    public static BukkitTask schedule(BukkitRunnable runnable, int delay, int period) {
        return runnable.runTaskTimer(singleton(), delay, period);
    }

    /**
     * Sets a value to an entity's metadata
     *
     * @param target entity to set to
     * @param key    key to store under
     * @param value  value to store
     */
    public static void setMeta(Metadatable target, String key, Object value) {
        target.setMetadata(key, new FixedMetadataValue(singleton(), value));
    }

    /**
     * Retrieves metadata from an entity
     *
     * @param target entity to retrieve from
     * @param key    key the value was stored under
     *
     * @return the stored value
     */
    public static Object getMeta(Metadatable target, String key) {
        List<MetadataValue> meta = target.getMetadata(key);
        return meta == null || meta.size() == 0 ? null : meta.get(0).value();
    }

    /**
     * Retrieves metadata from an entity
     *
     * @param target entity to retrieve from
     * @param key    key the value was stored under
     *
     * @return the stored value
     */
    public static int getMetaInt(Metadatable target, String key) {
        return target.getMetadata(key).get(0).asInt();
    }

    /**
     * Retrieves metadata from an entity
     *
     * @param target entity to retrieve from
     * @param key    key the value was stored under
     *
     * @return the stored value
     */
    public static double getMetaDouble(Metadatable target, String key) {
        return target.getMetadata(key).get(0).asDouble();
    }

    /**
     * Removes metadata from an entity
     *
     * @param target entity to remove from
     * @param key    key metadata was stored under
     */
    public static void removeMeta(Metadatable target, String key) {
        target.removeMetadata(key, singleton());
    }

    /**
     * Grabs a config for SkillAPI
     *
     * @param name config file name
     *
     * @return config data
     */
    public static CommentedConfig getConfig(String name) {
        return new CommentedConfig(singleton, name);
    }

    /**
     * Reloads the plugin
     */
    public static void reload() {
        SkillAPI inst = singleton();
        inst.onDisable();
        inst.onEnable();
    }
}
