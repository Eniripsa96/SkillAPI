package com.sucy.skill.manager;

import com.rit.sucy.config.CommentedConfig;
import com.rit.sucy.config.Config;
import com.rit.sucy.config.parse.DataSection;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.SkillPlugin;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.dynamic.DynamicClass;
import com.sucy.skill.dynamic.DynamicSkill;
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

    private static final String SKILL_FOLDER = "dynamic" + File.separator + "skill";
    private static final String CLASS_FOLDER = "dynamic" + File.separator + "class";
    private static final String SKILL_DIR    = SKILL_FOLDER + File.separator;
    private static final String CLASS_DIR    = CLASS_FOLDER + File.separator;

    private final SkillAPI api;

    private CommentedConfig skillConfig;
    private CommentedConfig classConfig;

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
        skillConfig = new CommentedConfig(api, "dynamic" + File.separator + "skills");
        classConfig = new CommentedConfig(api, "dynamic" + File.separator + "classes");
        new File(api.getDataFolder().getAbsolutePath() + File.separator + "dynamic" + File.separator + "skill").mkdirs();
        new File(api.getDataFolder().getAbsolutePath() + File.separator + "dynamic" + File.separator + "class").mkdirs();
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
            skillConfig.save();
        }
        if (!classConfig.getConfigFile().exists())
        {
            classConfig.save();
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

        // Load dynamic skills from skills.yml
        if (!skillConfig.getConfig().getBoolean("loaded", false))
        {
            log("Loading dynamic skills from skills.yml...", 1);
            skillConfig.getConfig().set("loaded", true);
            for (String key : skillConfig.getConfig().keys())
            {
                if (!skillConfig.getConfig().isSection(key))
                {
                    log("Skipping \"" + key + "\" because it isn't a configuration section", 3);
                    continue;
                }
                if (!SkillAPI.isSkillRegistered(key))
                {
                    DynamicSkill skill = new DynamicSkill(key);
                    api.getServer().getPluginManager().registerEvents(skill, api);
                    api.skills.put(key.toLowerCase(), skill);
                    skill.load(skillConfig.getConfig().getSection(key));
                    CommentedConfig sConfig = new CommentedConfig(api, SKILL_DIR + key);
                    sConfig.clear();
                    skill.save(sConfig.getConfig().createSection(key));
                    skill.save(skillConfig.getConfig().createSection(key));
                    sConfig.save();
                    log("Loaded the dynamic skill: " + key, 2);
                }
                else
                {
                    api.getLogger().severe("Duplicate skill detected: " + key);
                }
            }
        }
        else
        {
            log("skills.yml doesn't have any changes, skipping it", 1);
        }

        // Load individual dynamic skills
        log("Loading individual dynamic skill files...", 1);
        File skillRoot = new File(api.getDataFolder().getPath() + File.separator + SKILL_FOLDER);
        if (skillRoot.exists())
        {
            File[] files = skillRoot.listFiles();
            if (files != null)
            {
                for (File file : files)
                {
                    String name = file.getName().replace(".yml", "");
                    try
                    {
                        if (!SkillAPI.isSkillRegistered(name))
                        {
                            CommentedConfig sConfig = new CommentedConfig(api, SKILL_DIR + name);
                            DynamicSkill skill = new DynamicSkill(name);
                            api.getServer().getPluginManager().registerEvents(skill, api);
                            api.skills.put(name.toLowerCase(), skill);
                            skill.load(sConfig.getConfig().getSection(name));
                            sConfig.clear();
                            skill.save(sConfig.getConfig().createSection(name));
                            skill.save(skillConfig.getConfig().createSection(name));
                            sConfig.save();
                            log("Loaded the dynamic skill: " + name, 2);
                        }
                        else if (SkillAPI.getSkill(name) instanceof DynamicSkill)
                        {
                            log(name + " is already loaded, skipping it", 3);
                        }
                        else
                        {
                            api.getLogger().severe("Duplicate skill detected: " + name);
                        }
                    }
                    catch (Exception ex)
                    {
                        api.getLogger().severe("Failed to load skill: " + name + " - " + ex.getMessage());
                    }
                }
            }
        }

        log("Loading classes...", 1);

        // Request plugin classes
        mode = Mode.CLASS;
        for (Plugin plugin : api.getServer().getPluginManager().getPlugins())
        {
            if (plugin instanceof SkillPlugin)
            {
                log(" - " + plugin.getName(), 2);
                ((SkillPlugin) plugin).registerClasses(api);
            }
        }

        // Load dynamic classes from classes.yml
        if (!classConfig.getConfig().getBoolean("loaded", false))
        {
            log("Loading dynamic classes from classes.yml...", 1);
            classConfig.getConfig().set("loaded", true);
            for (String key : classConfig.getConfig().keys())
            {
                if (key.equals("loaded"))
                {
                    continue;
                }
                if (!SkillAPI.isClassRegistered(key))
                {
                    DynamicClass tree = new DynamicClass(api, key);
                    tree.load(classConfig.getConfig().getSection(key));
                    api.addDynamicClass(tree);
                    CommentedConfig cConfig = new CommentedConfig(api, CLASS_DIR + key);
                    cConfig.clear();
                    tree.save(cConfig.getConfig().createSection(key));
                    tree.save(classConfig.getConfig().createSection(key));
                    cConfig.save();
                    log("Loaded the dynamic class: " + key, 2);
                }
                else
                {
                    api.getLogger().severe("Duplicate class detected: " + key);
                }
            }
        }
        else
        {
            log("classes.yml doesn't have any changes, skipping it", 1);
        }

        // Load individual dynamic classes
        log("Loading individual dynamic class files...", 1);
        File classRoot = new File(api.getDataFolder().getPath() + File.separator + CLASS_FOLDER);
        if (classRoot.exists())
        {
            File[] files = classRoot.listFiles();
            if (files != null)
            {
                for (File file : files)
                {
                    try
                    {
                        String name = file.getName().replace(".yml", "");
                        if (!SkillAPI.isClassRegistered(name))
                        {
                            CommentedConfig cConfig = new CommentedConfig(api, CLASS_DIR + name);
                            DynamicClass tree = new DynamicClass(api, name);
                            tree.load(cConfig.getConfig().getSection(name));
                            api.addDynamicClass(tree);
                            cConfig.clear();
                            tree.save(cConfig.getConfig().createSection(name));
                            tree.save(classConfig.getConfig().createSection(name));
                            cConfig.save();
                            log("Loaded the dynamic class: " + name, 2);
                        }
                        else if (SkillAPI.getClass(name) instanceof DynamicClass)
                        {
                            log(name + " is already loaded, skipping it", 3);
                        }
                        else
                        {
                            api.getLogger().severe("Duplicate class detected: " + name);
                        }
                    }
                    catch (Exception ex)
                    {
                        api.getLogger().severe("Failed to load class file: " + file.getName() + " - " + ex.getMessage());
                    }
                }
            }
        }

        skillConfig.save();
        classConfig.save();

        mode = Mode.DONE;

        // Arrange skill trees
        for (RPGClass c : SkillAPI.getClasses().values())
        {
            c.arrange();
        }

        log("Registration complete", 0);
        log(" - " + SkillAPI.getSkills().size() + " skills", 0);
        log(" - " + SkillAPI.getClasses().size() + " classes", 0);
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
        else if (SkillAPI.isSkillRegistered(skill.getName()))
        {
            api.getLogger().warning("Duplicate skill name: \"" + skill.getName() + "\" - skipping the duplicate");
        }

        // Save new data to config
        else
        {

            CommentedConfig singleFile = new CommentedConfig(api, "skill" + File.separator + skill.getName());
            DataSection config = singleFile.getConfig();

            try
            {
                // Soft save to ensure optional data starts off in the config
                skill.softSave(config);

                // Load the config data to apply any previous data
                skill.load(config);

                // Finally, do a full save to make sure the config is up to date
                skill.save(config);
                singleFile.save();

                // Skill is ready to be registered
                return skill;
            }
            catch (Exception ex)
            {
                api.getLogger().severe("Failed to save skill data to config for \"" + skill.getName() + "\" - skipping registration");
                ex.printStackTrace();
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
        else if (SkillAPI.isClassRegistered(rpgClass.getName()))
        {
            api.getLogger().warning("Duplicate class name: \"" + rpgClass.getName() + "\" - skipping the duplicate");
        }

        // Save new data to config
        else
        {

            CommentedConfig singleFile = new CommentedConfig(api, "class" + File.separator + rpgClass.getName());
            DataSection config = singleFile.getConfig();

            try
            {

                // Soft save to ensure optional data starts off in the config
                rpgClass.softSave(config);

                // Load the config data to apply any previous data
                rpgClass.load(config);

                // Finally, do a full save to make sure the config is up to date
                rpgClass.save(config);
                singleFile.save();

                // Skill is ready to be registered
                return rpgClass;
            }
            catch (Exception ex)
            {
                api.getLogger().severe("Failed to save class data to config for \"" + rpgClass.getName() + "\" - skipping registration");
                ex.printStackTrace();
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
        if (SkillAPI.getSettings().getLoadLogLevel() >= level)
        {
            api.getLogger().info(message);
        }
    }
}
