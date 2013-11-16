package com.sucy.skill.api;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Base class for objects with dynamic data
 */
public abstract class Attributed extends Valued {

    private final HashMap<String, Integer> base = new HashMap<String, Integer>();
    private final HashMap<String, Integer> scale = new HashMap<String, Integer>();

    /**
     * <p>Defines a new attribute for the skill</p>
     * <p>Values are overwritten after the configuration
     * is loaded. Using this method is simply to define
     * the default values before configuration changes.</p>
     * <p>You should not use this method after the initial
     * creation of the skill.</p>
     *
     * @param name  attribute name
     * @param base  base value
     * @param scale value scale
     * @throws IllegalArgumentException if attribute is already defined
     */
    public void setAttribute(String name, int base, int scale) {
        if (this.base.containsKey(name)) throw new IllegalArgumentException("Attribute is already defined: " + name);

        this.base.put(name, base);
        this.scale.put(name, scale);
    }

    /**
     * Gets the base value of an attribute
     *
     * @param attribute attribute name
     * @return          base value
     * @throws IllegalArgumentException if the attribute is not defined
     */
    public int getBase(String attribute) {
        if (!hasAttribute(attribute)) throw new IllegalArgumentException("Attribute is not defined: " + attribute);

        return base.get(attribute);
    }

    /**
     * Gets the bonus for an attribute value per level
     *
     * @param attribute attribute name
     * @return          change in value per skill level (adding, can be negative value)
     * @throws IllegalArgumentException if the attribute is not defined
     */
    public int getScale(String attribute) {
        if (!hasAttribute(attribute)) throw new IllegalArgumentException("Attribute is not defined: " + attribute);

        return scale.get(attribute);
    }

    /**
     * <p>Sets the base value of an attribute</p>
     * <p>This is used by the API to override the default values.
     * You should not use this method. When defining attributes,
     * use setAttribute(int, int)</p>
     *
     * @param attribute attribute name
     * @param value     new value
     * @throws IllegalArgumentException if attribute is not defined
     */
    public void setBase(String attribute, int value) {
        if (!hasAttribute(attribute)) throw new IllegalArgumentException("Attribute is not defined - " + attribute);
        base.put(attribute, value);
    }

    /**
     * <p>Sets the bonus value of an attribute</p>
     * <p>This is used by the API to override the default values.
     * You should not use this method. When defining attributes,
     * use setAttribute(int, int)</p>
     *
     * @param attribute attribute name
     * @param value     new scale value
     * @throws IllegalArgumentException if attribute is not defined
     */
    public void setScale(String attribute, int value) {
        if (!hasAttribute(attribute)) throw new IllegalArgumentException("Attribute is not defined - " + attribute);
        scale.put(attribute, value);
    }

    /**
     * Adds to the base value of an attribute
     *
     * @param attribute attribute to add to
     * @param amount    amount to add
     */
    public void addAttribute(String attribute, int amount) {
        setBase(attribute, getBase(attribute) + amount);
    }

    /**
     * <p>Gets the list of the names of all attributes
     * defined for this skill. Normally you should
     * have the names of attributes already and don't
     * need to use this method. This is primarily
     * for generating Skill Tree icons.</p>
     *
     * @return custom attribute names
     */
    public List<String> getAttributeNames() {
        return new ArrayList<String>(base.keySet());
    }

    /**
     * Checks if the attribute is defined
     *
     * @param name name of the attribute
     * @return     true if defined, false otherwise
     */
    public boolean hasAttribute(String name) {
        return base.containsKey(name);
    }

    /**
     * Removes an attribute from the object
     *
     * @param name name of the attribute
     */
    public void removeAttribute(String name) {
        base.remove(name);
        scale.remove(name);
    }

    /**
     * <p>Calculates a value for an attribute at a given level</p>
     *
     * @param attribute  attribute name
     * @param level      level of the skill
     * @return           attribute value
     * @throws IllegalArgumentException if the attribute is not defined
     */
    public int getAttribute(String attribute, int level) {
        if (!hasAttribute(attribute)) throw new IllegalArgumentException("Attribute is not defined: " + attribute);

        return getBase(attribute) + getScale(attribute) * (level - 1);
    }

    /**
     * Checks to make sure the attributed object has a default attribute
     *
     * @param attribute    attribute to check
     * @param defaultBase  default base value
     * @param defaultScale default scale value
     */
    public void checkDefault(String attribute, int defaultBase, int defaultScale) {
        if (!hasAttribute(attribute))
            setAttribute(attribute, defaultBase, defaultScale);
    }

    /**
     * Saves attributes to a configuration section
     *
     * @param config configuration section to save to
     */
    public void saveAttributes(ConfigurationSection config) {
        for (String key : base.keySet()) {
            ConfigurationSection section = config.createSection(key);
            section.set("base", base.get(key));
            section.set("scale", scale.get(key));
        }
    }

    /**
     * Loads attributes from a configuration section
     *
     * @param config configuration section to load from
     */
    public void loadAttributes(ConfigurationSection config) {
        if (config == null) return;

        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            base.put(key, section.getInt("base"));
            scale.put(key, section.getInt("scale"));
        }
    }
}
