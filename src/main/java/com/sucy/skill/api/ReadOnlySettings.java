/**
 * SkillAPI
 * com.sucy.skill.api.ReadOnlySettings
 *
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
package com.sucy.skill.api;

import com.rit.sucy.config.parse.DataSection;

import java.util.List;

/**
 * A wrapper for the API settings that makes it read-only
 */
public class ReadOnlySettings
{
    private Settings settings;

    /**
     * A wrapper for settings that makes it read-only
     *
     * @param settings settings to wrap
     */
    public ReadOnlySettings(Settings settings)
    {
        this.settings = settings;
    }

    /**
     * Retrieves a double value from the settings. If the setting is
     * not set, this will instead return 0.
     *
     * @param key setting key
     *
     * @return double setting value
     */
    public double getDouble(String key)
    {
        return getDouble(key, 0);
    }

    /**
     * Retrieves a double value from the settings. If the setting is
     * not set, this will instead return 0.
     *
     * @param key          setting key
     * @param defaultValue the default value in case not set
     *
     * @return double setting value
     */
    public double getDouble(String key, double defaultValue)
    {
        return settings.getDouble(key, defaultValue);
    }

    /**
     * Retrieves an integer value from the settings. If the setting is
     * not set, this will instead return 0.
     *
     * @param key setting key
     *
     * @return integer setting value
     */
    public int getInt(String key)
    {
        return settings.getInt(key, 0);
    }

    /**
     * Retrieves an integer value from the settings. If the setting is
     * not set, this will instead return the default value.
     *
     * @param key          setting key
     * @param defaultValue the default value in case not set
     *
     * @return integer setting value
     */
    public int getInt(String key, int defaultValue)
    {
        return settings.getInt(key, defaultValue);
    }

    /**
     * Retrieves a boolean value from the settings. If the setting is
     * not set, this will instead return false.
     *
     * @param key setting key
     *
     * @return boolean setting value
     */
    public boolean getBool(String key)
    {
        return settings.getBool(key);
    }

    /**
     * Retrieves a boolean value from the settings. If the setting is
     * not set, this will instead return false.
     *
     * @param key          setting key
     * @param defaultValue the default value in case not set
     *
     * @return boolean setting value
     */
    public boolean getBool(String key, boolean defaultValue)
    {
        return settings.getBool(key, defaultValue);
    }

    /**
     * Retrieves a string value from the settings. If the setting is
     * not set, this will instead return null.
     *
     * @param key setting key
     *
     * @return String setting value
     */
    public String getString(String key)
    {
        return settings.getString(key, null);
    }

    /**
     * Retrieves a string value from the settings. If the setting is
     * not set, this will instead return the default value.
     *
     * @param key          setting key
     * @param defaultValue the default value in case not set
     *
     * @return String setting value
     */
    public String getString(String key, String defaultValue)
    {
        return settings.getString(key, defaultValue);
    }

    /**
     * Retrieves a string list from the settings
     *
     * @param key settings key
     *
     * @return string list or empty list if not found
     */
    public List<String> getStringList(String key)
    {
        return settings.getStringList(key);
    }

    /**
     * <p>Calculates a value for a scaling setting at a given level.</p>
     * <p>If the scaling setting does not exist, this will instead return 0.</p>
     *
     * @param key   scaling setting name
     * @param level level of scaling
     *
     * @return scaled setting value
     */
    public double getAttr(String key, int level)
    {
        return settings.getAttr(key, level, 0);
    }

    /**
     * <p>Calculates a value for a scaling setting at a given level.</p>
     * <p>If the scaling setting does not exist, this will instead return
     * the provided default value.</p>
     *
     * @param key          scaling setting name
     * @param level        level of scaling
     * @param defaultValue the default value in case not set
     *
     * @return scaled setting value
     */
    public double getAttr(String key, int level, double defaultValue)
    {
        return settings.getAttr(key, level, defaultValue);
    }

    /**
     * <p>Gets the base value of an attribute</p>
     * <p>If the attribute is not set, this will return 0.</p>
     *
     * @param key attribute name
     *
     * @return base value
     */
    public double getBase(String key)
    {
        return settings.getBase(key);
    }

    /**
     * <p>Gets the scale value of an attribute</p>
     * <p>If the attribute is not set, this will return 0.</p>
     *
     * @param key attribute name
     *
     * @return change in value per level
     */
    public double getScale(String key)
    {
        return settings.getScale(key);
    }

    /**
     * <p>Retrieves a generic attribute.</p>
     * <p>If the attribute is not set, this will return 0 instead.</p>
     *
     * @param key   attribute name
     * @param level level of scaling
     *
     * @return attribute value or 0 if not found
     */
    public Object getObj(String key, int level)
    {
        return settings.getObj(key, level);
    }

    /**
     * <p>Checks whether or not the setting is defined.</p>
     * <p>A setting is defined when it is set at any point using
     * any of the setter methods or while loading from the configuration.</p>
     *
     * @param key name of the setting
     *
     * @return true if defined, false otherwise
     */
    public boolean has(String key)
    {
        return settings.has(key);
    }

    /**
     * <p>Saves settings to a configuration section.</p>
     * <p>If the config section is null, this does not do anything.</p>
     *
     * @param config configuration section to save to
     */
    public void save(DataSection config)
    {
        settings.save(config);
    }

    /**
     * Dumps the settings to the console for debugging purposes
     */
    public void dumpToConsole()
    {
        settings.dumpToConsole();
    }
}
