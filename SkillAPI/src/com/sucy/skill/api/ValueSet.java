package com.sucy.skill.api;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Set;

/**
 * <p>Represents a set of values that store variable data for an object.</p>
 */
public class ValueSet
{

    private HashMap<String, Integer> values = new HashMap<String, Integer>();

    /**
     * <p>Retrieves a set value.</p>
     * <p>If the value is not set, this returns 0.</p>
     *
     * @param key value key
     * @return value
     */
    public int getValue(String key)
    {
        return values.get(key);
    }

    /**
     * <p>Sets a value.</p>
     * <p>If the value doesn't exist, it is created.</p>
     *
     * @param key   value key
     * @param value value
     */
    public void setValue(String key, int value)
    {
        values.put(key, value);
    }

    /**
     * <p>Checks if a value has been set.</p>
     *
     * @param key value key
     * @return true if set, false otherwise
     */
    public boolean isValueSet(String key)
    {
        return values.containsKey(key);
    }

    /**
     * <p>Adds an amount to a value.</p>
     * <p>If the value doesn't exist, it creates the value with the amount.</p>
     *
     * @param key   value key
     * @param value amount to add
     */
    public void addValue(String key, int value)
    {
        values.put(key, values.get(key) + value);
    }

    /**
     * <p>Subtracts an amount from a value.</p>
     * <p>If the value doesn't exist, it creates the value with the negative amount.</p>
     *
     * @param key   value key
     * @param value amount to subtract
     */
    public void subtractValue(String key, int value)
    {
        values.put(key, values.get(key) - value);
    }

    /**
     * <p>Checks if the value is set with an amount of at least the provided amount.</p>
     * <p>If the value doesn't exist, it treats the value as having an amount of 0.</p>
     *
     * @param key    value key
     * @param amount amount required
     * @return true if has at least that much, false otherwise
     */
    public boolean hasValue(String key, int amount)
    {
        return values.get(key) >= amount;
    }

    /**
     * <p>Retrieves the list of the names of all set values.</p>
     *
     * @return names of all set values
     */
    public Set<String> getValueNames()
    {
        return values.keySet();
    }

    /**
     * <p>Saves all set values to the configuration section</p>
     * <p>This will overwrite any data that was there before that
     * also shares a key with one of the values. Any data that doesn't
     * conflict with any of the keys will not be changed.</p>
     * <p>If the config is null, this does not do anything.</p>
     *
     * @param config configuration section to save to
     */
    public void saveValues(ConfigurationSection config)
    {
        if (config == null)
        {
            return;
        }

        for (String key : values.keySet())
        {
            config.set(key, values.get(key));
        }
    }

    /**
     * <p>Loads all values from the configuration section,
     * overwriting any already set values if there are conflicts.</p>
     * <p>If the configuration is null, this does not do anything.</p>
     * <p>Any keys that do not point to an integer are ignored.</p>
     *
     * @param config configuration section to load from
     */
    public void loadValues(ConfigurationSection config)
    {
        if (config == null)
        {
            return;
        }

        // Load values
        for (String key : config.getKeys(false))
        {
            if (config.isInt(key))
            {
                values.put(key, config.getInt(key));
            }
        }
    }
}
