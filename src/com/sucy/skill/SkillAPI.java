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

import com.rit.sucy.config.CommentedLanguageConfig;
import com.rit.sucy.version.VersionManager;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.api.classes.RPGClass;
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
import com.sucy.skill.gui.Menu;
import com.sucy.skill.hook.PluginChecker;
import com.sucy.skill.listener.AddonListener;
import com.sucy.skill.listener.AttributeListener;
import com.sucy.skill.listener.BarListener;
import com.sucy.skill.listener.BindListener;
import com.sucy.skill.listener.ClickListener;
import com.sucy.skill.listener.DeathListener;
import com.sucy.skill.listener.FallbackClickListener;
import com.sucy.skill.listener.ItemListener;
import com.sucy.skill.listener.KillListener;
import com.sucy.skill.listener.MainListener;
import com.sucy.skill.listener.MechanicListener;
import com.sucy.skill.listener.SkillAPIListener;
import com.sucy.skill.listener.StatusListener;
import com.sucy.skill.listener.TreeListener;
import com.sucy.skill.manager.AttributeManager;
import com.sucy.skill.manager.ClassBoardManager;
import com.sucy.skill.manager.CmdManager;
import com.sucy.skill.manager.ComboManager;
import com.sucy.skill.manager.RegistrationManager;
import com.sucy.skill.manager.ResourceManager;
import com.sucy.skill.packet.PacketInjector;
import com.sucy.skill.task.CooldownTask;
import com.sucy.skill.task.GUITask;
import com.sucy.skill.task.InventoryTask;
import com.sucy.skill.task.ManaTask;
import com.sucy.skill.task.SaveTask;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>The main class of the plugin which has the accessor methods into most of the API.</p>
 * <p>You can retrieve a reference to this through Bukkit the same way as any other plugin.</p>
 */
public class SkillAPI extends JavaPlugin
{
    private static SkillAPI singleton;

    private final HashMap<String, Skill>          skills  = new HashMap<String, Skill>();
    private final HashMap<String, RPGClass>       classes = new HashMap<String, RPGClass>();
    private final HashMap<String, PlayerAccounts> players = new HashMap<String, PlayerAccounts>();
    private final ArrayList<String>               groups  = new ArrayList<String>();

    private final List<SkillAPIListener> listeners = new ArrayList<SkillAPIListener>();

    private CommentedLanguageConfig language;
    private Settings                settings;

    private IOManager           io;
    private CmdManager          cmd;
    private ComboManager        comboManager;
    private RegistrationManager registrationManager;
    private AttributeManager    attributeManager;

    private ManaTask      manaTask;
    private CooldownTask  cdTask;
    private InventoryTask invTask;
    private SaveTask      saveTask;
    private GUITask       guiTask;

    private boolean disabling = false;
    private boolean loaded = false;

    /**
     * <p>Enables SkillAPI, setting up listeners, managers, and loading data. This
     * should not be called by other plugins.</p>
     */
    @Override
    public void onEnable()
    {
        // Set up the singleton
        if (singleton != null)
            throw new IllegalStateException("Cannot enable SkillAPI twice!");
        singleton = this;

        // Load settings
        settings = new Settings(this);
        language = new CommentedLanguageConfig(this, "language");
        language.checkDefaults();
        language.trim();
        language.save();

        // Hook plugins
        PluginChecker.isVaultActive();

        // Set up managers
        comboManager = new ComboManager();
        registrationManager = new RegistrationManager(this);
        cmd = new CmdManager(this);
        io = settings.isUseSql() ? new SQLIO(this) : new ConfigIO(this);
        PlayerStats.init();
        ClassBoardManager.registerText();
        ResourceManager.copyQuestsModule();
        if (settings.isAttributesEnabled())
            attributeManager = new AttributeManager(this);

        // Load classes and skills
        registrationManager.initialize();

        // Load group settings after groups are determined
        settings.loadGroupSettings();

        // Set up listeners
        listen(new BindListener(), true);
        listen(new MainListener(), true);
        listen(new MechanicListener(), true);
        listen(new StatusListener(), true);
        listen(new KillListener(), true);
        listen(new AddonListener(), true);
        listen(new TreeListener(), !settings.isMapTreeEnabled());
        listen(new ItemListener(), settings.isCheckLore());
        listen(new BarListener(), settings.isSkillBarEnabled());
        if (VersionManager.isVersionAtLeast(VersionManager.V1_8_0)) {
            final PacketInjector injector = new PacketInjector(this);
            listen(new ClickListener(injector), settings.isCombosEnabled());
        } else {
            listen(new FallbackClickListener(), settings.isCombosEnabled());
        }
        listen(new AttributeListener(), settings.isAttributesEnabled());
        listen(new DeathListener(), !VersionManager.isVersionAtLeast(11000));

        if (settings.isMapTreeAvailable())
            Menu.initialize(this);

        // Set up tasks
        if (settings.isManaEnabled())
            manaTask = new ManaTask(this);
        if (settings.isSkillBarCooldowns())
            cdTask = new CooldownTask(this);
        if (settings.isCheckLore())
            invTask = new InventoryTask(this, settings.getPlayersPerCheck());
        if (settings.isAutoSave())
            saveTask = new SaveTask(this);
        guiTask = new GUITask(this);

        // Load player data
        players.putAll(io.loadAll());
        for (PlayerAccounts accounts : players.values())
            accounts.getActiveData().init(accounts.getPlayer());

        for (SkillAPIListener listener : listeners)
            listener.init();

        loaded = true;
    }

    private void listen(SkillAPIListener listener, boolean enabled)
    {
        if (enabled)
        {
            Bukkit.getPluginManager().registerEvents(listener, this);
            listeners.add(listener);
        }
    }

    /**
     * <p>Disables SkillAPI, saving data before unloading everything and disconnecting
     * listeners. This should not be called by other plugins.</p>
     */
    @Override
    public void onDisable()
    {
        // Validate instance
        if (singleton != this)
            throw new IllegalStateException("This is not a valid, enabled SkillAPI copy!");

        disabling = true;

        // Clear tasks
        if (manaTask != null)
        {
            manaTask.cancel();
            manaTask = null;
        }
        if (cdTask != null)
        {
            cdTask.cancel();
            cdTask = null;
        }
        if (invTask != null)
        {
            invTask.cancel();
            invTask = null;
        }
        if (saveTask != null)
        {
            saveTask.cancel();
            saveTask = null;
        }
        if (guiTask != null && guiTask.isRunning())
        {
            guiTask.cancel();
            guiTask = null;
        }

        for (SkillAPIListener listener : listeners)
            listener.cleanup();

        // Clear scoreboards
        ClassBoardManager.clearAll();

        // Clear skill bars and stop passives before disabling
        for (Player player : VersionManager.getOnlinePlayers()) {
            MainListener.unload(player);
        }

        io.saveAll();

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
    public static boolean isLoaded()
    {
        return singleton != null && singleton.loaded;
    }

    /**
     * @return SkillAPI singleton if available
     *
     * @throws IllegalStateException if SkillAPI isn't enabled
     */
    private static SkillAPI singleton()
    {
        if (singleton == null)
            throw new IllegalStateException("Cannot use SkillAPI methods before it is enabled - add it to your plugin.yml as a dependency");
        return singleton;
    }

    /**
     * Retrieves the settings data controlling SkillAPI
     *
     * @return SkillAPI settings data
     */
    public static Settings getSettings()
    {
        return singleton().settings;
    }

    /**
     * Retrieves the language file data for SkillAPI
     *
     * @return SkillAPI language file data
     */
    public static CommentedLanguageConfig getLanguage()
    {
        return singleton().language;
    }

    /**
     * Retrieves the manager for click cast combos
     *
     * @return click combo manager
     */
    public static ComboManager getComboManager()
    {
        return singleton().comboManager;
    }

    /**
     * Retrieves the attribute manager for SkillAPI
     *
     * @return attribute manager
     */
    public static AttributeManager getAttributeManager()
    {
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
    public static Skill getSkill(String name)
    {
        if (name == null)
            return null;
        return singleton().skills.get(name.toLowerCase());
    }

    /**
     * Retrieves the registered skill data for SkillAPI. It is recommended that you
     * don't edit this map. Instead, use "addSkill" and "addSkills" instead.
     *
     * @return the map of registered skills
     */
    public static HashMap<String, Skill> getSkills()
    {
        return singleton().skills;
    }

    /**
     * Checks whether or not a skill is registered.
     *
     * @param name name of the skill
     *
     * @return true if registered, false otherwise
     */
    public static boolean isSkillRegistered(String name)
    {
        return getSkill(name) != null;
    }

    /**
     * Checks whether or not a skill is registered
     *
     * @param skill the skill to check
     *
     * @return true if registered, false otherwise
     */
    public static boolean isSkillRegistered(PlayerSkill skill)
    {
        return isSkillRegistered(skill.getData().getName());
    }

    /**
     * Checks whether or not a skill is registered
     *
     * @param skill the skill to check
     *
     * @return true if registered, false otherwise
     */
    public static boolean isSkillRegistered(Skill skill)
    {
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
    public static RPGClass getClass(String name)
    {
        if (name == null)
            return null;
        return singleton().classes.get(name.toLowerCase());
    }

    /**
     * Retrieves the registered class data for SkillAPI. It is recommended that you
     * don't edit this map. Instead, use "addClass" and "addClasses" instead.
     *
     * @return the map of registered skills
     */
    public static HashMap<String, RPGClass> getClasses()
    {
        return singleton().classes;
    }

    /**
     * Retrieves a list of base classes that don't profess from another class
     *
     * @return the list of base classes
     */
    public static ArrayList<RPGClass> getBaseClasses(String group)
    {
        ArrayList<RPGClass> list = new ArrayList<RPGClass>();
        for (RPGClass c : singleton.classes.values())
            if (!c.hasParent() && c.getGroup().equals(group))
                list.add(c);
        return list;
    }

    /**
     * Checks whether or not a class is registered.
     *
     * @param name name of the class
     *
     * @return true if registered, false otherwise
     */
    public static boolean isClassRegistered(String name)
    {
        return getClass(name) != null;
    }

    /**
     * Checks whether or not a class is registered.
     *
     * @param playerClass the class to check
     *
     * @return true if registered, false otherwise
     */
    public static boolean isClassRegistered(PlayerClass playerClass)
    {
        return isClassRegistered(playerClass.getData().getName());
    }

    /**
     * Checks whether or not a class is registered.
     *
     * @param rpgClass the class to check
     *
     * @return true if registered, false otherwise
     */
    public static boolean isClassRegistered(RPGClass rpgClass)
    {
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
    public static PlayerData getPlayerData(OfflinePlayer player)
    {
        if (player == null)
            return null;
        return getPlayerAccountData(player).getActiveData();
    }

    /**
     * Loads the data for a player when they join the server. This is handled
     * by the API and doesn't need to be used elsewhere unless you want to
     * load a player's data without them logging on. This should be run
     * asynchronously since it is loading configuration files.
     *
     * @param player player to load the data for
     */
    public static PlayerAccounts loadPlayerData(OfflinePlayer player)
    {
        if (player == null)
            return null;

        // Already loaded for some reason, no need to load again
        String id = new VersionPlayer(player).getIdString();
        if (singleton().players.containsKey(id)) return singleton.players.get(id);

        // Load the data
        PlayerAccounts data = singleton.io.loadData(player);
        singleton.players.put(id, data);
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
        singleton().players.remove(player.getUniqueId().toString());
        loadPlayerData(player);
    }

    /**
     * Saves all player data to the configs. This
     * should be called asynchronously to avoid problems
     * with the main server loop.
     */
    public static void saveData()
    {
        singleton().io.saveAll();
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
    public static boolean hasPlayerData(OfflinePlayer player)
    {
        return singleton != null && player != null && singleton.players.containsKey(new VersionPlayer(player).getIdString());
    }

    /**
     * Unloads player data from memory, saving it to the config
     * first and then removing it from the map.
     *
     * @param player player to unload data for
     */
    public static void unloadPlayerData(final OfflinePlayer player)
    {
        if (singleton == null || player == null || singleton.disabling || !singleton.players.containsKey(new VersionPlayer(player).getIdString()))
            return;

        singleton.getServer().getScheduler().runTaskAsynchronously(
            singleton, () -> {
                PlayerAccounts accounts = getPlayerAccountData(player);
                singleton.io.saveData(accounts);
                singleton.players.remove(new VersionPlayer(player).getIdString());
            }
        );
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
    public static PlayerAccounts getPlayerAccountData(OfflinePlayer player)
    {
        if (player == null)
            return null;

        String id = new VersionPlayer(player).getIdString();
        if (!singleton().players.containsKey(id))
        {
            PlayerAccounts data = loadPlayerData(player);
            singleton.players.put(id, data);
            return data;
        }
        else
            return singleton.players.get(id);
    }

    /**
     * Retrieves all the player data of SkillAPI. It is recommended not to
     * modify this map. Instead, use helper methods within individual player data.
     *
     * @return all SkillAPI player data
     */
    public static HashMap<String, PlayerAccounts> getPlayerAccountData()
    {
        return singleton().players;
    }

    /**
     * Retrieves the list of active class groups used by
     * registered classes
     *
     * @return list of active class groups
     */
    public static List<String> getGroups()
    {
        return singleton().groups;
    }

    /**
     * This adds a dynamic skill to the skill list. This should
     * not be called by other plugins.
     *
     * @param skill the dynamic skill to register
     */
    public void addDynamicSkill(DynamicSkill skill)
    {
        if (registrationManager.isAddingDynamicSkills())
            skills.put(skill.getName().toLowerCase(), skill);
        else
            throw new IllegalStateException("Cannot add dynamic skills from outside SkillAPI");
    }

    /**
     * Registers a new skill with SkillAPI. If this is called outside of the method
     * provided in SkillPlugin, this will throw an error. You should implement SkillPlugin
     * in your main class and call this from the provided "registerSkills" method.
     *
     * @param skill skill to register
     */
    public void addSkill(Skill skill)
    {
        skill = registrationManager.validate(skill);
        if (skill != null)
            skills.put(skill.getName().toLowerCase(), skill);
    }

    /**
     * Registers multiple new skills with SkillAPI. If this is called outside of the method
     * provided in SkillPlugin, this will throw an error. You should implement SkillPlugin
     * in your main class and call this from the provided "registerSkills" method.
     *
     * @param skills skills to register
     */
    public void addSkills(Skill... skills)
    {
        for (Skill skill : skills)
            addSkill(skill);
    }

    /**
     * Registers a new class with SkillAPI. If this is called outside of the method
     * provided in SkillPlugin, this will throw an error. You should implement SkillPlugin
     * in your main class and call this from the provided "registerClasses" method.
     *
     * @param rpgClass class to register
     */
    public void addClass(RPGClass rpgClass)
    {
        rpgClass = registrationManager.validate(rpgClass);
        if (rpgClass != null)
        {
            classes.put(rpgClass.getName().toLowerCase(), rpgClass);
            ClassBoardManager.registerClass(rpgClass);
            if (!groups.contains(rpgClass.getGroup()))
                groups.add(rpgClass.getGroup());
        }
    }

    /**
     * Adds a dynamic class which ignores validation. This should only
     * be used by the API as other plugins should use the regular addClass.
     *
     * @param rpgClass dynamic class to add
     */
    public void addDynamicClass(DynamicClass rpgClass)
    {
        String key;
        if (rpgClass != null && !classes.containsKey(key = rpgClass.getName().toLowerCase()))
        {
            classes.put(key, rpgClass);
            ClassBoardManager.registerClass(rpgClass);
            if (!groups.contains(rpgClass.getGroup()))
                groups.add(rpgClass.getGroup());
        }
    }

    /**
     * Registers a new class with SkillAPI. If this is called outside of the method
     * provided in SkillPlugin, this will throw an error. You should implement SkillPlugin
     * in your main class and call this from the provided "registerClasses" method.
     *
     * @param classes classes to register
     */
    public void addClasses(RPGClass... classes)
    {
        for (RPGClass rpgClass : classes)
            addClass(rpgClass);
    }

    /**
     * Schedules a delayed task
     *
     * @param runnable the task to schedule
     * @param delay    the delay in ticks
     */
    public static void schedule(BukkitRunnable runnable, int delay)
    {
        runnable.runTaskLater(singleton(), delay);
    }

    /**
     * Schedules a delayed task
     *
     * @param runnable the task to schedule
     * @param delay    the delay in ticks
     */
    public static void schedule(Runnable runnable, int delay)
    {
        Bukkit.getScheduler().runTaskLater(singleton, runnable, delay);
    }

    /**
     * Schedules a repeating task
     *
     * @param runnable the task to schedule
     * @param delay    the delay in ticks before the first tick
     * @param period   how often to run in ticks
     */
    public static void schedule(BukkitRunnable runnable, int delay, int period)
    {
        runnable.runTaskTimer(singleton(), delay, period);
    }

    /**
     * Sets a value to an entity's metadata
     *
     * @param target entity to set to
     * @param key    key to store under
     * @param value  value to store
     */
    public static void setMeta(Metadatable target, String key, Object value)
    {
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
    public static Object getMeta(Metadatable target, String key)
    {
        List<MetadataValue> metadata = target.getMetadata(key);
        return (metadata == null || metadata.isEmpty()) ? null : metadata.get(0).value();
    }

    /**
     * Retrieves metadata from an entity
     *
     * @param target entity to retrieve from
     * @param key    key the value was stored under
     *
     * @return the stored value
     */
    public static int getMetaInt(Metadatable target, String key)
    {
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
    public static double getMetaDouble(Metadatable target, String key)
    {
        return target.getMetadata(key).get(0).asDouble();
    }

    /**
     * Removes metadata from an entity
     *
     * @param target entity to remove from
     * @param key    key metadata was stored under
     */
    public static void removeMeta(Metadatable target, String key)
    {
        target.removeMetadata(key, singleton());
    }

    /**
     * Reloads the plugin
     */
    public static void reload()
    {
        SkillAPI inst = singleton();
        inst.onDisable();
        inst.onEnable();
    }
}
