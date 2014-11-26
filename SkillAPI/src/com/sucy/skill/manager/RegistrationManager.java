package com.sucy.skill.manager;

import com.rit.sucy.config.Config;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.SkillPlugin;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.skills.Skill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * <p>Skill API Registration Manager.</p>
 * <p>This handles loading skill and class data from cofiguration files and fetching
 * them from other plugins while validating everything to make sure it should be
 * added.</p>
 */
public class RegistrationManager
{
    /**
     * The registration modes used by the manager. These values are used to check
     * what can be registered at any given time.
     */
    public enum Mode
    {
        STARTUP, SKILL, CLASS, DONE
    }

    private final SkillAPI api;

    private Config skillConfig;
    private Config classConfig;

    private Mode mode = Mode.STARTUP;

    /**
     * <p>Creates a new Registration Manager for handling registering new
     * classes or skills.</p>
     *
     * @param api SkillAPI reference
     */
    public RegistrationManager(SkillAPI api)
    {
        this.api = api;
        skillConfig = new Config(api, "dynamic" + File.separator + "skills");
        classConfig = new Config(api, "dynamic" + File.separator + "classes");
    }

    /**
     * Initializes the registration manager, fetching skills and classes from
     * configuration files and other plugins.
     */
    public void initialize()
    {

        // Make sure dynamic files are created
        if (!skillConfig.getConfigFile().exists())
        {
            skillConfig.saveConfig();
        }
        if (!classConfig.getConfigFile().exists())
        {
            classConfig.saveConfig();
        }

        log("Loading skills...", 1);

        // Request plugin skills
        mode = Mode.SKILL;
        for (Plugin plugin : api.getServer().getPluginManager().getPlugins())
        {
            if (plugin instanceof SkillPlugin)
            {
                log(" - " + plugin.getName(), 2);
                ((SkillPlugin) plugin).registerSkills(api);
            }
        }

        // Load example skills if enabled
        if (api.getSettings().isUseExampleSkills())
        {
            log(" - SkillAPI Examples", 1);
            //api.getExampleClasses().registerSkills(api);
        }

        log("Loading classes...", 1);

        // Request plugin classes
        mode = Mode.CLASS;
        for (Plugin plugin : api.getServer().getPluginManager().getPlugins())
        {
            if (plugin instanceof SkillPlugin)
            {
                log(" - " + plugin.getName(), 1);
                ((SkillPlugin) plugin).registerClasses(api);
            }
        }

        // Load example classes if enabled
        if (api.getSettings().isUseExampleClasses())
        {
            log(" - SkillAPI Examples", 1);
            //api.getExampleClasses().registerClasses(api);
        }

        skillConfig.saveConfig();
        classConfig.saveConfig();

        mode = Mode.DONE;

        // TODO
        // Arrange trees

        log("Registration complete", 0);
        log(" - " + api.getSkills().size() + " skills", 0);
        log(" - " + api.getClasses().size() + " classes", 0);
    }

    /**
     * <p>Validates a skill, making sure it is being registered during the
     * appropriate time, it isn't null, and it doesn't conflict with other
     * registered skills.</p>
     *
     * @param skill skill to validate
     *
     * @return the class if valid, null otherwise
     */
    public Skill validate(Skill skill)
    {

        // Cannot register outside the allotted time
        if (mode != Mode.SKILL)
        {
            throw new IllegalStateException("Skills cannot be added outside the provided SkillPlugin method");
        }

        // Cannot be null
        else if (skill == null)
        {
            throw new IllegalArgumentException("Cannot register a null skill");
        }

        // Cannot have multiple skills with the same name
        else if (api.isSkillRegistered(skill.getName()))
        {
            api.getLogger().warning("Duplicate skill name: \"" + skill.getName() + "\" - skipping the duplicate");
        }

        // Save new data to config
        else
        {

            Config singleFile = new Config(api, "skill" + File.separator + skill.getName());
            ConfigurationSection config = singleFile.getConfig();

            try
            {

                // Soft save to ensure optional data starts off in the config
                skill.softSave(config);

                // Load the config data to apply any previous data
                skill.load(config);

                // Finally, do a full save to make sure the config is up to date
                skill.save(config);
                singleFile.saveConfig();

                // Skill is ready to be registered
                return skill;
            }
            catch (Exception ex)
            {
                api.getLogger().severe("Failed to save skill data to config for \"" + skill.getName() + "\" - skipping registration");
            }
        }

        return null;
    }

    /**
     * <p>Validates a class, making sure it is being registered during the
     * appropriate time, it isn't null, and it doesn't conflict with other
     * registered classes.</p>
     *
     * @param rpgClass class to validate
     *
     * @return the class if valid, null otherwise
     */
    public RPGClass validate(RPGClass rpgClass)
    {

        // Cannot register outside the allotted time
        if (mode != Mode.CLASS)
        {
            throw new IllegalStateException("Classes cannot be added outside the provided SkillPlugin method");
        }

        // Cannot be null
        else if (rpgClass == null)
        {
            throw new IllegalArgumentException("Cannot register a null class");
        }

        // Cannot have multiple skills with the same name
        else if (api.isClassRegistered(rpgClass.getName()))
        {
            api.getLogger().warning("Duplicate class name: \"" + rpgClass.getName() + "\" - skipping the duplicate");
        }

        // Save new data to config
        else
        {

            Config singleFile = new Config(api, "class" + File.separator + rpgClass.getName());
            ConfigurationSection config = singleFile.getConfig();

            try
            {

                // Soft save to ensure optional data starts off in the config
                rpgClass.softSave(config);

                // Load the config data to apply any previous data
                rpgClass.load(config);

                // Finally, do a full save to make sure the config is up to date
                rpgClass.save(config);
                singleFile.saveConfig();

                // Skill is ready to be registered
                return rpgClass;
            }
            catch (Exception ex)
            {
                api.getLogger().severe("Failed to save class data to config for \"" + rpgClass.getName() + "\" - skipping registration");
            }
        }

        return null;
    }

    /**
     * Logs a message if the logging level is at least the specified value
     *
     * @param message message to log
     * @param level   required logging level
     */
    private void log(String message, int level)
    {
        if (api.getSettings().getLoadLogLevel() >= level)
        {
            api.getLogger().info(message);
        }
    }
}
