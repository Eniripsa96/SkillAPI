package com.sucy.skill;

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
import com.sucy.skill.config.Config;
import com.sucy.skill.config.SkillValues;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

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
                ((SkillPlugin) plugin).registerSkills(api);
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
        for (Plugin plugin : api.getServer().getPluginManager().getPlugins()) {
            if (plugin instanceof SkillPlugin) {
                ((SkillPlugin) plugin).registerClasses(api);
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

        // Load skill data
        for (ClassSkill skill : skills.values()) {
            try {
                if (skill instanceof DynamicSkill) {
                    skill.update(skillConfig.getConfig().getConfigurationSection(skill.getName()));
                }
                else skill.update(new Config(api, "skill" + File.separator + skill.getName()).getConfig());
            }
            catch (Exception e) {
                api.getLogger().severe("Failed to load skill: " + skill.getName());
                e.printStackTrace();
            }
        }

        // Load skill tree data
        for (CustomClass tree : classes.values()) {
            if (tree instanceof DynamicClass) {
                tree.update(classConfig.getConfig().getConfigurationSection(tree.getName()));
            }
            else tree.update(new Config(api, "class" + File.separator + tree.getName()).getConfig());
        }

        // Arrange skill trees
        List<CustomClass> classList = new ArrayList<CustomClass>(this.classes.values());
        for (CustomClass tree : classList) {
            try {
                tree.getTree().arrange();
            }
            catch (Exception ex) {
                api.getLogger().severe("Failed to arrange skill tree for the class " + tree.getName() + " - " + ex.getMessage());
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

        api.getLogger().info("Loaded " + skills.size() + " skills and " + classes.size() + " skill trees");
    }

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
                api.getServer().getPluginManager().registerEvents(listener, api);
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

            // Add to table
            classes.put(customClass.getName().toLowerCase(), customClass);
            configFile.saveConfig();
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
}
