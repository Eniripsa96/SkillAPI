/**
 * SkillAPI
 * com.sucy.skill.manager.AttributeManager
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
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
package com.sucy.skill.manager;

import com.rit.sucy.config.CommentedConfig;
import com.rit.sucy.config.parse.DataSection;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.util.Data;
import com.sucy.skill.data.Formula;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.log.LogType;
import com.sucy.skill.log.Logger;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Handles loading and accessing individual
 * attributes from the configuration.
 */
public class AttributeManager
{
    // Keys for supported stat modifiers
    public static final String HEALTH           = "health";
    public static final String MANA             = "mana";
    public static final String MANA_REGEN       = "mana-regen";
    public static final String PHYSICAL_DAMAGE  = "physical-damage";
    public static final String PHYSICAL_DEFENSE = "physical-defense";
    public static final String SKILL_DAMAGE     = "skill-damage";
    public static final String SKILL_DEFENSE    = "skill-defense";
    public static final String MOVE_SPEED       = "move-speed";

    private HashMap<String, Attribute> attributes = new HashMap<String, Attribute>();

    /**
     * Sets up the attribute manager, loading the attribute
     * data from the configuration. This is handled by SkillAPI
     * automatically so other plugins should not instantiate
     * this class.
     *
     * @param api SkillAPI reference
     */
    public AttributeManager(SkillAPI api)
    {
        load(api);
    }

    /**
     * Retrieves an attribute template
     *
     * @param key attribute key
     *
     * @return template for the attribute
     */
    public Attribute getAttribute(String key)
    {
        return attributes.get(key.toLowerCase());
    }

    /**
     * Retrieves the available attribute keys
     *
     * @return set of available attribute keys
     */
    public Set<String> getKeys()
    {
        return attributes.keySet();
    }

    /**
     * Loads attribute data from the config
     *
     * @param api SkillAPI reference
     */
    private void load(SkillAPI api)
    {
        CommentedConfig config = new CommentedConfig(api, "attributes");
        config.saveDefaultConfig();

        DataSection data = config.getConfig();
        Logger.log(LogType.ATTRIBUTE_LOAD, 1, "Loading attributes...");
        for (String key : data.keys())
        {
            Logger.log(LogType.ATTRIBUTE_LOAD, 2, "  - " + key);
            attributes.put(key.toLowerCase(), new Attribute(data.getSection(key), key));
        }
    }

    /**
     * A single attribute template
     */
    public class Attribute
    {
        private static final String DISPLAY   = "display";
        private static final String GLOBAL    = "global";
        private static final String CONDITION = "condition";
        private static final String MECHANIC  = "mechanic";
        private static final String TARGET    = "target";
        private static final String STATS     = "stats";
        private static final String MAX       = "max";

        // Attribute description
        private String    key;
        private String    display;
        private ItemStack icon;
        private int       max;

        // Dynamic global modifiers
        private HashMap<String, AttributeValue[]> conditions = new HashMap<String, AttributeValue[]>();
        private HashMap<String, AttributeValue[]> mechanics  = new HashMap<String, AttributeValue[]>();
        private HashMap<String, AttributeValue[]> targets    = new HashMap<String, AttributeValue[]>();

        // General stat modifiers
        private HashMap<String, Formula> statModifiers = new HashMap<String, Formula>();

        /**
         * Creates a new attribute, loading the settings from the given
         * config data.
         *
         * @param data config data to load from
         * @param key  the key the attribute was labeled under
         */
        public Attribute(DataSection data, String key)
        {
            this.key = key;
            this.display = data.getString(DISPLAY, key).toLowerCase();
            this.icon = Data.parseIcon(data);
            this.max = data.getInt(MAX, 999);

            // Load dynamic global settings
            DataSection globals = data.getSection(GLOBAL);
            if (globals != null)
            {
                loadGroup(globals.getSection(CONDITION), conditions);
                loadGroup(globals.getSection(MECHANIC), mechanics);
                loadGroup(globals.getSection(TARGET), targets);
            }

            // Load stat settings
            DataSection stats = data.getSection(STATS);
            if (stats != null)
            {
                loadStatModifier(stats, HEALTH);
                loadStatModifier(stats, MANA);
                loadStatModifier(stats, MANA_REGEN);
                loadStatModifier(stats, PHYSICAL_DAMAGE);
                loadStatModifier(stats, PHYSICAL_DEFENSE);
                loadStatModifier(stats, SKILL_DAMAGE);
                loadStatModifier(stats, SKILL_DEFENSE);
                loadStatModifier(stats, MOVE_SPEED);
            }
        }

        /**
         * Retrieves the config key of the attribute
         *
         * @return config key of the attribute
         */
        public String getKey()
        {
            return key;
        }

        /**
         * Retrieves the name for the attribute
         *
         * @return name of the attribute
         */
        public String getName()
        {
            return display;
        }

        /**
         * Retrieves the icon for the attribute
         *
         * @return icon of the attribute
         */
        public ItemStack getIcon()
        {
            return icon;
        }

        /**
         * Retrieves the max amount the attribute can be raised to
         *
         * @return max attribute amount
         */
        public int getMax()
        {
            return max;
        }

        /**
         * Modifies a dynamic condition's value
         *
         * @param component component to modify for
         * @param key       key of the value to modify
         * @param self      whether or not the component is targeting the caster
         * @param value     base value
         * @param amount    amount of attribute points
         *
         * @return modified value
         */
        public double modify(EffectComponent component, String key, boolean self, double value, int amount)
        {
            HashMap<String, AttributeValue[]> map;
            if (component.getType().equals("condition")) map = conditions;
            else if (component.getType().equals("mechanic")) map = mechanics;
            else map = targets;

            key = component.getKey().replaceAll("-.+", "").toLowerCase() + "-" + key.toLowerCase();
            if (map.containsKey(key))
            {
                AttributeValue[] list = map.get(key);
                for (AttributeValue attribValue : list)
                    if (attribValue.passes(component, self))
                        return attribValue.apply(value, amount);
            }
            return value;
        }

        /**
         * Modifies a stat value
         *
         * @param key    key of the stat
         * @param base   base value of the stat
         * @param amount amount of attribute points
         *
         * @return modified stat value
         */
        public double modifyStat(String key, double base, int amount)
        {
            if (statModifiers.containsKey(key))
            {
                return statModifiers.get(key).compute(base, amount);
            }
            return base;
        }

        /**
         * Loads a dynamic group globals settings into the given hashmap
         *
         * @param data   config data to load from
         * @param target target map to store the data in
         */
        private void loadGroup(DataSection data, HashMap<String, AttributeValue[]> target)
        {
            if (data == null) return;
            for (String key : data.keys())
            {
                Logger.log(LogType.ATTRIBUTE_LOAD, 2, "    SkillMod: " + key);
                String value = data.getString(key);
                String[] formulas = value.split("\\|");
                AttributeValue[] values = new AttributeValue[formulas.length];
                int i = 0;
                for (String formula : formulas)
                    values[i] = new AttributeValue(formula);
                target.put(key.toLowerCase(), values);
            }
        }

        /**
         * Loads a stat modifier from the config data
         *
         * @param data config data to load from
         * @param key  key of the stat modifier
         */
        private void loadStatModifier(DataSection data, String key)
        {
            if (data.has(key))
            {
                Logger.log(LogType.ATTRIBUTE_LOAD, 2, "    StatMod: " + key);
                statModifiers.put(key, new Formula(data.getString(key, "v")));
            }
        }
    }

    /**
     * Represents one formula modifier for an attribute
     * that can have conditions
     */
    public class AttributeValue
    {
        private Formula formula;
        private HashMap<String, String> conditions = new HashMap<String, String>();

        /**
         * Loads the attribute value that starts with the formula
         * and can have as many conditions as desired after
         *
         * @param data data string for the value
         */
        public AttributeValue(String data)
        {
            String[] pieces = data.split(":");
            formula = new Formula(pieces[0]);
            for (int i = 1; i < pieces.length; i++)
            {
                String[] sides = pieces[i].split("=");
                conditions.put(sides[0], sides[1]);
                Logger.log(LogType.ATTRIBUTE_LOAD, 3, "      Condition: " + sides[0] + " / " + sides[1]);
            }
        }

        /**
         * Checks whether or not the formula should be applied to the component
         *
         * @param component component to check for conditions against
         * @param self      whether or not the component is targeting the caster
         * @return true if passes the conditions
         */
        public boolean passes(EffectComponent component, boolean self)
        {
            for (String key : conditions.keySet())
            {
                if (key.equals("self"))
                {
                    if (conditions.get(key).equalsIgnoreCase("false") != self)
                        return false;
                }
                else if (!component.getSettings().getString(key).equalsIgnoreCase(conditions.get(key)))
                        return false;
            }
            return true;
        }

        /**
         * Checks the conditions for the given component
         *
         * @param value     base value
         * @param amount    amount of attribute points
         *
         * @return the modified value if the conditions passed or the base value if they failed
         */
        public double apply(double value, int amount)
        {
            return formula.compute(value, amount);
        }
    }
}
