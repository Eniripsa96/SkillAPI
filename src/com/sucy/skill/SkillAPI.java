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

import com.rit.sucy.config.LanguageConfig;
import com.rit.sucy.player.PlayerUUIDs;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.data.ComboManager;
import com.sucy.skill.data.Settings;
import com.sucy.skill.data.io.ConfigIO;
import com.sucy.skill.data.io.IOManager;
import com.sucy.skill.listener.*;
import com.sucy.skill.manager.CmdManager;
import com.sucy.skill.manager.RegistrationManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * <p>The main class of the plugin which has the accessor methods into most of the API.</p>
 * <p>You can retrieve a reference to this through Bukkit the same way as any other plugin.</p>
 */
public class SkillAPI extends JavaPlugin
{
    private static SkillAPI singleton;

    private final HashMap<String, Skill>        skills  = new HashMap<String, Skill>();
    private final HashMap<String, RPGClass>     classes = new HashMap<String, RPGClass>();
    private final HashMap<UUID, PlayerAccounts> players = new HashMap<UUID, PlayerAccounts>();
    private final ArrayList<String>             groups  = new ArrayList<String>();

    private LanguageConfig language;
    private Settings       settings;

    private IOManager           io;
    private CmdManager          cmd;
    private ComboManager        comboManager;
    private RegistrationManager registrationManager;

    private boolean enabled = false;

    /**
     * <p>Enables SkillAPI, setting up listeners, managers, and loading data. This
     * should not be called by other plugins.</p>
     */
    @Override
    public void onEnable()
    {
        // Set up the singleton
        if (singleton != null)
        {
            throw new IllegalStateException("Cannot enable SkillAPI twice!");
        }
        singleton = this;

        // Ensure only one enable at a time
        if (enabled)
        {
            throw new IllegalStateException("Cannot enable SkillAPI when already enabled!");
        }
        enabled = true;

        // Load settings
        language = new LanguageConfig(this, "language");
        settings = new Settings(this);

        // Set up managers
        comboManager = new ComboManager();
        registrationManager = new RegistrationManager(this);
        cmd = new CmdManager(this);
        io = new ConfigIO(this);

        // Set up listeners
        new MainListener(this);
        new StatusListener(this);
        new CastListener(this);
        new TreeListener(this);
        if (settings.isSkillBarEnabled()) new BarListener(this);

        // Load classes and skills
        registrationManager.initialize();

        // Load player data
        for (Player player : getServer().getOnlinePlayers())
        {
            loadPlayerData(player.getName());
        }
    }

    /**
     * <p>Disables SkillAPI, saving data before unloading everything and disconnecting
     * listeners. This should not be called by other plugins.</p>
     */
    @Override
    public void onDisable()
    {
        if (singleton != this)
        {
            throw new IllegalStateException("This is not a valid, enabled SkillAPI copy!");
        }

        io.saveAll();

        skills.clear();
        classes.clear();
        players.clear();

        HandlerList.unregisterAll(this);
        cmd.clear();

        enabled = false;
        singleton = null;
    }

    /**
     * Retrieves the settings data controlling SkillAPI
     *
     * @return SkillAPI settings data
     */
    public static Settings getSettings()
    {
        if (singleton == null)
        {
            return null;
        }
        return singleton.settings;
    }

    /**
     * Retrieves the language file data for SkillAPI
     *
     * @return SkillAPI language file data
     */
    public static LanguageConfig getLanguage()
    {
        if (singleton == null)
        {
            return null;
        }
        return singleton.language;
    }

    /**
     * Retrieves the manager for click cast combos
     *
     * @return click combo manager
     */
    public static ComboManager getComboManager()
    {
        if (singleton == null)
        {
            return null;
        }
        return singleton.comboManager;
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
        if (name == null || singleton == null)
        {
            return null;
        }
        return singleton.skills.get(name.toLowerCase());
    }

    /**
     * Retrieves the registered skill data for SkillAPI. It is recommended that you
     * don't edit this map. Instead, use "addSkill" and "addSkills" instead.
     *
     * @return the map of registered skills
     */
    public static HashMap<String, Skill> getSkills()
    {
        if (singleton == null)
        {
            return null;
        }
        return singleton.skills;
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
        if (name == null || singleton == null)
        {
            return null;
        }
        return singleton.classes.get(name.toLowerCase());
    }

    /**
     * Retrieves the registered class data for SkillAPI. It is recommended that you
     * don't edit this map. Instead, use "addClass" and "addClasses" instead.
     *
     * @return the map of registered skills
     */
    public static HashMap<String, RPGClass> getClasses()
    {
        if (singleton == null)
        {
            return null;
        }
        return singleton.classes;
    }

    /**
     * Retrieves a list of base classes that don't profess from another class
     *
     * @return the list of base classes
     */
    public static ArrayList<RPGClass> getBaseClasses()
    {
        ArrayList<RPGClass> list = new ArrayList<RPGClass>();
        for (RPGClass c : singleton.classes.values())
        {
            if (!c.hasParent())
            {
                list.add(c);
            }
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
        return getPlayerAccountData(player).getActiveData();
    }

    /**
     * Loads the data for a player when they join the server. This is handled
     * by the API and doesn't need to be used elsewhere unless you want to
     * load a player's data without them logging on. This should be run
     * asynchronously since it is loading configuration files.
     *
     * @param name name of the player to load
     */
    public static void loadPlayerData(String name)
    {
        if (singleton == null) return;
        OfflinePlayer player = PlayerUUIDs.getOfflinePlayer(name);
        PlayerAccounts data = singleton.io.loadData(player);
        singleton.players.put(player.getUniqueId(), data);
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
        if (singleton == null)
        {
            return null;
        }
        else if (!singleton.players.containsKey(player.getUniqueId()))
        {
            PlayerAccounts data = new PlayerAccounts(player);
            singleton.players.put(player.getUniqueId(), data);
            return data;
        }
        else
        {
            return singleton.players.get(player.getUniqueId());
        }
    }

    /**
     * Retrieves all the player data of SkillAPI. It is recommended not to
     * modify this map. Instead, use helper methods within individual player data.
     *
     * @return all SkillAPI player data
     */
    public static HashMap<UUID, PlayerAccounts> getPlayerAccountData()
    {
        if (singleton == null)
        {
            return null;
        }
        return singleton.players;
    }

    /**
     * Retrieves the list of active class groups used by
     * registered classes
     *
     * @return list of active class groups
     */
    public static List<String> getGroups()
    {
        if (singleton == null)
        {
            return null;
        }
        return singleton.groups;
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
        {
            skills.put(skill.getName().toLowerCase(), skill);
        }
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
        {
            addSkill(skill);
        }
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
            if (!groups.contains(rpgClass.getGroup()))
            {
                groups.add(rpgClass.getGroup());
            }
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
        {
            addClass(rpgClass);
        }
    }
}
