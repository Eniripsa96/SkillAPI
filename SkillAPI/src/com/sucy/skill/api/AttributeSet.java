package com.sucy.skill.api;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Represents a set of attributes that store variable data for an object.</p>
 */
public class AttributeSet
{

    private final HashMap<String, Double> base  = new HashMap<String, Double>();
    private final HashMap<String, Double> scale = new HashMap<String, Double>();

    /**
     * <p>Defines a new attribute</p>
     * <p>Values are overwritten after the configuration
     * is loaded. Using this method is simply to define
     * the default values before configuration changes.</p>
     * <p>You should not use this method after the initial
     * creation of the skill.</p>
     *
     * @param name  attribute name
     * @param base  base value
     * @param scale value scale
     */
    public void set(String name, double base, double scale)
    {
        this.base.put(name, base);
        this.scale.put(name, scale);
    }

    /**
     * <p>Sets the base value of an attribute</p>
     * <p>If the attribute is not set, this will default
     * the scale of the attribute to 0.</p>
     * <p>This is used to override the default values.
     * You should not use this method. When defining attributes,
     * use set(String, double, double)</p>
     *
     * @param attribute attribute name
     * @param value     new value
     */
    public void setBase(String attribute, double value)
    {
        if (!scale.containsKey(attribute))
        {
            scale.put(attribute, 0.0);
        }
        base.put(attribute, value);
    }

    /**
     * <p>Sets the bonus value of an attribute</p>
     * <p>If the attribute is not set, this will default
     * the base of the attribute to 0.</p>
     * <p>This is used by the API to override the default values.
     * You should not use this method. When defining attributes,
     * use set(String, double, double)</p>
     *
     * @param attribute attribute name
     * @param value     new scale value
     */
    public void setScale(String attribute, double value)
    {
        if (!base.containsKey(attribute))
        {
            base.put(attribute, 0.0);
        }
        scale.put(attribute, value);
    }

    /**
     * <p>Calculates a value for an attribute at a given level.</p>
     * <p>If the attribute does not exist, this will instead return 0.</p>
     *
     * @param attribute attribute name
     * @param level     level of the skill
     * @return attribute value
     */
    public double get(String attribute, int level)
    {
        if (!has(attribute))
        {
            return 0;
        }
        return getBase(attribute) + getScale(attribute) * (level - 1);
    }

    /**
     * <p>Gets the base value of an attribute</p>
     * <p>If the attribute is not set, this will return 0.</p>
     *
     * @param attribute attribute name
     * @return base value
     */
    public double getBase(String attribute)
    {
        if (!base.containsKey(attribute))
        {
            return 0;
        }
        else
        {
            return base.get(attribute);
        }
    }

    /**
     * <p>Gets the scale value of an attribute</p>
     * <p>If the attribute is not set, this will return 0.</p>
     *
     * @param attribute attribute name
     * @return change in value per level
     */
    public double getScale(String attribute)
    {
        if (!scale.containsKey(attribute))
        {
            return 0;
        }
        else
        {
            return scale.get(attribute);
        }
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
    public List<String> getNames()
    {
        return new ArrayList<String>(base.keySet());
    }

    /**
     * <p>Checks whether or not the attribute is defined.</p>
     * <p>An attribute is defined when it is set at any point using
     * any of the setter methods or while loading from the configuration.</p>
     *
     * @param name name of the attribute
     * @return true if defined, false otherwise
     */
    public boolean has(String name)
    {
        return base.containsKey(name);
    }

    /**
     * <p>Removes an attribute from the object.</p>
     * <p>If the attribute is not set, this will not do anything.</p>
     *
     * @param name name of the attribute
     */
    public void remove(String name)
    {
        base.remove(name);
        scale.remove(name);
    }

    /**
     * <p>Checks to make sure the attributed object has a default attribute.</p>
     * <p>If the attribute is not set, the attribute will be created with
     * the provided values.</p>
     * <p>If the attribute already exists, this will do nothing.</p>
     *
     * @param attribute    attribute to check
     * @param defaultBase  default base value
     * @param defaultScale default scale value
     */
    public void checkDefault(String attribute, double defaultBase, double defaultScale)
    {
        if (!has(attribute))
        {
            set(attribute, defaultBase, defaultScale);
        }
    }

    /**
     * <p>Saves attributes to a configuration section.</p>
     * <p>If the config section is null, this does not do anything.</p>
     *
     * @param config configuration section to save to
     */
    public void save(ConfigurationSection config)
    {
        if (config == null)
        {
            return;
        }
        for (String key : base.keySet())
        {
            ConfigurationSection section = config.createSection(key);
            section.set("base", base.get(key));
            section.set("scale", scale.get(key));
        }
    }

    /**
     * <p>Loads attributes from a configuration section</p>
     * <p>If the section is null or has no keys, this will not do
     * anything.</p>
     * <p>Keys that do not point to valid sections for the base/scale
     * values will not be loaded.</p>
     * <p>Sections without a base or without a scale value will load
     * what's present and default the missing one to 0.</p>
     *
     * @param config configuration section to load from
     */
    public void load(ConfigurationSection config)
    {
        if (config == null)
        {
            return;
        }

        for (String key : config.getKeys(false))
        {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section != null)
            {
                base.put(key, section.getDouble("base", 0));
                scale.put(key, section.getDouble("scale", 0));
            }
        }
    }
}
