package com.sucy.skill;

import com.rit.sucy.config.Config;
import com.sucy.skill.api.ClassAttribute;
import com.sucy.skill.api.CustomClass;
import com.sucy.skill.api.SkillPlugin;
import com.sucy.skill.api.dynamic.DynamicClass;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.TargetSkill;
import com.sucy.skill.config.ClassValues;
import com.sucy.skill.config.SkillValues;
import com.sucy.skill.mccore.PrefixManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

/**
 * <p>Registration manager for SkillAPI</p>
 * <p>Do not instantiate this class. It is for the API use only.</p>
 */
public class RegistrationManager {

    // Data
    private final Hashtable<String, ClassSkill> skills = new Hashtable<String, ClassSkill>();
    private final Hashtable<String, CustomClass> classes = new Hashtable<String, CustomClass>();

    // API reference
    private final SkillAPI api;

    // Configs
    private Config skillConfig;
    private Config classConfig;

    // Register mode
    private RegisterMode mode = RegisterMode.DONE;

    /**
     * <p>Constructor</p>
     * <p>Do not use this</p>
     *
     * @param api API reference
     */
    public RegistrationManager(SkillAPI api) {
        this.api = api;
        skillConfig = new Config(api, "dynamic" + File.separator + "skills");
        classConfig = new Config(api, "dynamic" + File.separator + "classes");
    }

    /**
     * Initializes the registration processes
     */
    public void initialize() {

        // Make sure dynamic files are created
        if (!skillConfig.getConfigFile().exists()) skillConfig.saveConfig();
        if (!classConfig.getConfigFile().exists()) classConfig.saveConfig();

        // Request skills first
        mode = RegisterMode.SKILL;
        for (Plugin plugin : api.getServer().getPluginManager().getPlugins()) {
            if (plugin instanceof SkillPlugin) {
                log("Retrieving skills from " + plugin.getName(), 1);
                ((SkillPlugin) plugin).registerSkills(api);
            }
        }

        // Load dynamic skills from skills.yml
        if (!skillConfig.getConfig().getBoolean("loaded", false)) {
            log("Loading dynamic skills from skills.yml...", 1);
            skillConfig.getConfig().set("loaded", true);
            for (String key : skillConfig.getConfig().getKeys(false)) {
                if (!skillConfig.getConfig().isConfigurationSection(key)) {
                    log("Skipping " + key + " because it isn't a configuration section", 3);
                    continue;
                }
                if (!skills.containsKey(key.toLowerCase())) {
                    DynamicSkill skill = new DynamicSkill(key);
                    skills.put(key.toLowerCase(), skill);
                    skill.update(skillConfig.getConfig().getConfigurationSection(key));
                    Config sConfig = new Config(api, "dynamic" + File.separator + "skill" + File.separator + key);
                    skill.save(sConfig.getConfig().createSection(key));
                    sConfig.saveConfig();
                    log("Loaded the dynamic skill: " + key, 2);
                }
                else api.getLogger().severe("Duplicate skill detected: " + key);
            }
        }
        else log("skills.yml doesn't have any changes, skipping it", 1);

        // Load individual dynamic skills
        log("Loading individual dynamic skill files...", 1);
        File skillRoot = new File(api.getDataFolder().getPath() + File.separator + "dynamic" + File.separator + "skill");
        if (skillRoot.exists()) {
            File[] files = skillRoot.listFiles();
            if (files != null) {
                for (File file : files) {
                    String name = file.getName().replace(".yml", "");
                    try {
                        if (!skills.containsKey(name.toLowerCase())) {
                            Config sConfig = new Config(api, "dynamic" + File.separator + "skill" + File.separator + name);
                            DynamicSkill skill = new DynamicSkill(name);
                            skills.put(name.toLowerCase(), skill);
                            skill.update(sConfig.getConfig().getConfigurationSection(name));
                            skill.save(skillConfig.getConfig().createSection(name));
                            log("Loaded the dynamic skill: " + name, 2);
                        }
                        else if (getSkill(name) instanceof DynamicSkill) log(name + " is already loaded, skipping it", 3);
                        else api.getLogger().severe("Duplicate skill detected: " + name);
                    }
                    catch (Exception ex) {
                        api.getLogger().severe("Failed to load skill: " + name);
                    }
                }
            }
        }

        // Load example skills
        if (api.isUsingExampleClasses()) {
            log("Loading example skills...", 1);
            api.getExampleClasses().registerSkills(api);
        }

        // Register classes after
        mode = RegisterMode.CLASS;
        for (Plugin plugin : api.getServer().getPluginManager().getPlugins()) {
            if (plugin instanceof SkillPlugin) {
                log("Getting classes from: " + plugin.getName(), 1);
                ((SkillPlugin) plugin).registerClasses(api);
            }
        }

        // Load dynamic classes from classes.yml
        if (!classConfig.getConfig().getBoolean("loaded", false)) {
            log("Loading dynamic classes from classes.yml...", 1);
            classConfig.getConfig().set("loaded", true);
            for (String key : classConfig.getConfig().getKeys(false)) {
                if (key.equals("loaded")) continue;
                if (!classes.containsKey(key.toLowerCase())) {
                    DynamicClass tree = new DynamicClass(key);
                    classes.put(key.toLowerCase(), tree);
                    tree.update(classConfig.getConfig().getConfigurationSection(key));
                    Config cConfig = new Config(api, "dynamic" + File.separator + "class" + File.separator + key);
                    tree.save(cConfig.getConfig().createSection(key));
                    cConfig.saveConfig();
                    log("Loaded the dynamic class: " + key, 2);
                }
                else api.getLogger().severe("Duplicate class detected: " + key);
            }
        }
        else log("classes.yml doesn't have any changes, skipping it", 1);

        // Load individual dynamic classes
        log("Loading individual dynamic class files...", 1);
        File classRoot = new File(api.getDataFolder().getPath() + File.separator + "dynamic" + File.separator + "class");
        if (classRoot.exists()) {
            File[] files = classRoot.listFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        String name = file.getName().replace(".yml", "");
                        if (!classes.containsKey(name.toLowerCase())) {
                            Config cConfig = new Config(api, "dynamic" + File.separator + "class" + File.separator + name);
                            DynamicClass tree = new DynamicClass(name);
                            classes.put(name.toLowerCase(), tree);
                            tree.update(cConfig.getConfig().getConfigurationSection(name));
                            tree.save(classConfig.getConfig().createSection(name));
                            log("Loaded the dynamic class: " + name, 2);
                        }
                        else if (getClass(name) instanceof DynamicClass) log(name + " is already loaded, skipping it", 3);
                        else api.getLogger().severe("Duplicate class detected: " + name);
                    }
                    catch (Exception ex) {
                        api.getLogger().severe("Failed to load class file: " + file.getName() + " - Invalid format");
                    }
                }
            }
        }

        // Load example classes
        if (api.isUsingExampleClasses()) {
            log("Loading example classes...", 1);
            api.getExampleClasses().registerClasses(api);
        }

        skillConfig.saveConfig();
        classConfig.saveConfig();

        // Done registering everything
        mode = RegisterMode.DONE;

        // Load skill data
        for (ClassSkill skill : skills.values()) {
            try {
                if (!(skill instanceof DynamicSkill)) {
                    skill.update(new Config(api, "skill" + File.separator + skill.getName()).getConfig());
                }
            }
            catch (Exception e) {
                api.getLogger().severe("Failed to load skill: " + skill.getName());
                e.printStackTrace();
            }
        }

        // Load skill tree data
        for (CustomClass tree : classes.values()) {
            if (!(tree instanceof DynamicClass)) {
                try {
                    tree.update(new Config(api, "class" + File.separator + tree.getName()).getConfig());
                }
                catch (Exception ex) {
                    api.getLogger().severe("Failed to load class: " + tree.getName());
                }
            }
        }

        // Arrange skill trees
        List<CustomClass> classList = new ArrayList<CustomClass>(this.classes.values());
        for (CustomClass tree : classList) {
            PrefixManager.registerClass(tree);
            try {
                log("Arranging the skill tree for the class: " + tree.getName(), 5);
                tree.getTree().arrange();
            }
            catch (Exception ex) {
                api.getLogger().severe("Failed to arrange skill tree for the class " + tree.getName() + " - " + ex.getMessage());
                classes.remove(tree.getName().toLowerCase());
            }
        }

        log("Loaded " + skills.size() + " skills and " + classes.size() + " skill trees", 0);
    }

    /**
     * Clears the skill and class data
     */
    public void clearData() {
        skills.clear();
        classes.clear();
    }

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
            api.getLogger().severe("Unable to register skill - " + skill.getClass().getName() + " - null name");
            return;
        }

        // Don't allow duplicate names
        else if (skills.containsKey(skill.getName().toLowerCase())) {
            api.getLogger().severe("Duplicate skill names detected! - " + skill.getName());
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
        Config configFile = new Config(api, "skill" + File.separator + skill.getName());
        ConfigurationSection config = configFile.getConfig();
        boolean neededOnly = config.getKeys(false).size() != 0;

        // Save default values
        try {
            if (!config.contains(SkillValues.MAX_LEVEL))
                config.set(SkillValues.MAX_LEVEL, skill.getMaxLevel() < 1 ? 1 : skill.getMaxLevel());
            if (!config.contains(SkillValues.INDICATOR))
                config.set(SkillValues.INDICATOR, skill.getIndicator().getType().name() + "," + skill.getIndicator().getDurability());
            if (!config.contains(SkillValues.MESSAGE) && !neededOnly)
                config.set(SkillValues.MESSAGE, skill.getMessage());
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
                config.set(SkillValues.DESCRIPTION, skill.getDescription());
            }
            if (!config.contains(SkillValues.PERMISSIONS)) {
                config.set(SkillValues.PERMISSIONS, skill.getPermissions());
            }

            // Add it to the list
            skills.put(skill.getName().toLowerCase(), skill);
            configFile.saveConfig();
            log("Registered the skill: " + skill.getName(), 2);

            // Register any listeners for skills
            if (skill instanceof Listener) {
                api.getServer().getPluginManager().registerEvents((Listener)skill, api);
            }
        }
        catch (Exception e) {
            api.getLogger().severe("Failed to register skill: " + skill.getName() + " - invalid returned values");
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
            api.getLogger().severe("Could not register class - " + customClass.getClass().getName() + " - null name");
            return;
        }

        // Don't allow duplicate names
        else if (classes.containsKey(customClass.getName().toLowerCase())) {
            api.getLogger().severe("Duplicate class names detected! - " + customClass.getName());
            return;
        }

        // Make sure the class has the right attributes
        customClass.checkDefault(ClassAttribute.HEALTH, 20, 0);
        customClass.checkDefault(ClassAttribute.MANA, 100, 0);

        // Detect if default values are needed
        Config configFile = new Config(api, "class" + File.separator + customClass.getName());
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
            if (!config.contains(ClassValues.PERMISSIONS))
                config.set(ClassValues.PERMISSIONS, customClass.getDeclaredPermissions());

            // Add to table
            classes.put(customClass.getName().toLowerCase(), customClass);
            configFile.saveConfig();
            log("Registered the class: " + customClass.getName(), 2);

            // Register events
            if (customClass instanceof Listener) {
                api.getServer().getPluginManager().registerEvents((Listener)customClass, api);
            }
        }
        catch (Exception e) {
            api.getLogger().severe("Failed to register class - " + customClass.getName() + " - Invalid values");
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
     * @return the collection of loaded classes
     */
    public Collection<CustomClass> getClasses() {
        return classes.values();
    }

    /**
     * Logs a message if the logging level is at least the specified value
     *
     * @param message message to log
     * @param level   required logging level
     */
    private void log(String message, int level) {
        if (api.getLoggingLevel() >= level) {
            api.getLogger().info(message);
        }
    }
}
