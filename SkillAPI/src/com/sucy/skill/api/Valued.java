package com.sucy.skill.api;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Set;

/**
 * Base class for objects that have dynamic values
 */
public abstract class Valued {

    private HashMap<String, Integer> values = new HashMap<String, Integer>();

    /**
     * Gets a value from the player
     *
     * @param key value key
     * @return    value
     */
    public int getValue(String key) {
        return values.get(key);
    }

    /**
     * Sets a value for the player
     *
     * @param key   value key
     * @param value value
     */
    public void setValue(String key, int value) {
        values.put(key, value);
    }

    /**
     * Checks if the value is set
     *
     * @param key value key
     * @return    true if set, false otherwise
     */
    public boolean isSet(String key) {
        return values.containsKey(key);
    }

    /**
     * Adds to a value for the player
     *
     * @param key   value key
     * @param value amount to add
     */
    public void addValue(String key, int value) {
        values.put(key, values.get(key) + value);
    }

    /**
     * Subtracts from a value for the player
     *
     * @param key   value key
     * @param value amount to subtract
     */
    public void subtractValue(String key, int value) {
        values.put(key, values.get(key) - value);
    }

    /**
     * Checks if the player's value has at least the deignated amount
     *
     * @param key   value key
     * @param value amount required
     * @return      true if has at least that much, false otherwise
     */
    public boolean hasValue(String key, int value) {
        return values.get(key) >= value;
    }

    /**
     * Names of all values attached to this object
     *
     * @return value names
     */
    public Set<String> getValueNames() {
        return values.keySet();
    }

    /**
     * Saves values to a configuration section
     *
     * @param config configuration section to save to
     */
    public void saveValues(ConfigurationSection config) {
        for (String key : values.keySet()) {
            config.set(key, values.get(key));
        }
    }

    /**
     * Loads values from a configuration section
     *
     * @param config configuration section to load from
     */
    public void loadValues(ConfigurationSection config) {
        if (config == null) return;

        // Load values
        for (String key : config.getKeys(false)) {
            values.put(key, config.getInt(key));
        }
    }
}
