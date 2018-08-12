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
import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.log.LogType;
import com.sucy.skill.log.Logger;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles loading and accessing individual
 * attributes from the configuration.
 */
public class AttributeManager {
    // Keys for supported stat modifiers
    public static final String HEALTH           = "health";
    public static final String MANA             = "mana";
    public static final String MANA_REGEN       = "mana-regen";
    public static final String PHYSICAL_DAMAGE  = "physical-damage";
    public static final String PHYSICAL_DEFENSE = "physical-defense";
    public static final String SKILL_DAMAGE     = "skill-damage";
    public static final String SKILL_DEFENSE    = "skill-defense";
    public static final String MOVE_SPEED       = "move-speed";
    public static final String COOLDOWN         = "cooldown";

    private final HashMap<String, Attribute>       attributes  = new LinkedHashMap<>();
    private final HashMap<String, Attribute>       lookup      = new HashMap<>();
    private final HashMap<String, List<Attribute>> byStat      = new HashMap<>();
    private final HashMap<String, List<Attribute>> byComponent = new HashMap<>();

    /**
     * Sets up the attribute manager, loading the attribute
     * data from the configuration. This is handled by SkillAPI
     * automatically so other plugins should not instantiate
     * this class.
     *
     * @param api SkillAPI reference
     */
    public AttributeManager(SkillAPI api) {
        load(api);
    }

    /**
     * Retrieves an attribute template
     *
     * @param key attribute key
     *
     * @return template for the attribute
     */
    public Attribute getAttribute(String key) {
        return lookup.get(key.toLowerCase());
    }

    /**
     * Unsafe getter for the attribute data.
     *
     * Do not use this method or modify it's return value unless
     * you know exactly what you are doing.
     *
     * @return attributes map
     */
    public HashMap<String, Attribute> getAttributes() {
        return attributes;
    }

    public List<Attribute> forStat(final String key) {
        return byStat.get(key.toLowerCase());
    }

    public List<Attribute> forComponent(final EffectComponent component, final String key) {
        return byComponent.get(component.getKey() + "-" + key.toLowerCase());
    }

    /**
     * Retrieves the available attribute keys. This
     * does not include display names for attributes.
     *
     * @return set of available attribute keys
     */
    public Set<String> getKeys() {
        return attributes.keySet();
    }

    /**
     * Retrieves the available attribute keys including
     * both display names and config keys.
     *
     * @return display name and config keys for attributes
     */
    public Set<String> getLookupKeys() {
        return lookup.keySet();
    }

    /**
     * Normalizes a config key or name into the config key
     * for a unified identifier to store stats under.
     *
     * @param key key to normalize
     * @return config key
     */
    public String normalize(String key) {
        final Attribute attribute = lookup.get(key.toLowerCase());
        if (attribute == null) { throw new IllegalArgumentException("Invalid attribute - " + key); }
        return attribute.getKey();
    }

    /**
     * Loads attribute data from the config
     *
     * @param api SkillAPI reference
     */
    private void load(SkillAPI api) {
        CommentedConfig config = new CommentedConfig(api, "attributes");
        config.saveDefaultConfig();

        DataSection data = config.getConfig();
        Logger.log(LogType.ATTRIBUTE_LOAD, 1, "Loading attributes...");
        for (String key : data.keys()) {
            Logger.log(LogType.ATTRIBUTE_LOAD, 2, "  - " + key);
            Attribute attribute = new Attribute(data.getSection(key), key);
            attributes.put(attribute.getKey(), attribute);
            lookup.put(attribute.getKey(), attribute);
            lookup.put(attribute.getName().toLowerCase(), attribute);
        }
    }

    /**
     * A single attribute template
     */
    public class Attribute {
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
        private Map<ComponentType, Map<String, AttributeValue[]>> dynamicModifiers = new EnumMap<>(ComponentType.class);

        // General stat modifiers
        private HashMap<String, Formula> statModifiers = new HashMap<>();

        /**
         * Creates a new attribute, loading the settings from the given
         * config data.
         *
         * @param data config data to load from
         * @param key  the key the attribute was labeled under
         */
        public Attribute(DataSection data, String key) {
            this.key = key.toLowerCase();
            this.display = data.getString(DISPLAY, key).toLowerCase();
            this.icon = Data.parseIcon(data);
            this.max = data.getInt(MAX, 999);

            // Load dynamic global settings
            DataSection globals = data.getSection(GLOBAL);
            if (globals != null) {
                loadGroup(globals.getSection(CONDITION), ComponentType.CONDITION);
                loadGroup(globals.getSection(MECHANIC), ComponentType.MECHANIC);
                loadGroup(globals.getSection(TARGET), ComponentType.TARGET);
            }

            // Load stat settings
            DataSection stats = data.getSection(STATS);
            if (stats != null) {
                for (String stat : stats.keys()) {
                    loadStatModifier(stats, stat);
                }
            }
        }

        /**
         * Retrieves the config key of the attribute
         *
         * @return config key of the attribute
         */
        public String getKey() {
            return key;
        }

        /**
         * Retrieves the name for the attribute
         *
         * @return name of the attribute
         */
        public String getName() {
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
        public int getMax() {
            return max;
        }

        /**
         * Modifies a dynamic condition's value
         *
         * @param component component to modify for
         * @param key       key of the value to modify
         * @param value     base value
         * @param amount    amount of attribute points
         *
         * @return modified value
         */
        public double modify(EffectComponent component, String key, double value, int amount) {
            key = component.getKey() + "-" + key.toLowerCase();
            final Map<String, AttributeValue[]> map = dynamicModifiers.get(component.getType());
            if (map.containsKey(key)) {
                AttributeValue[] list = map.get(key);
                for (AttributeValue attribValue : list) {
                    if (attribValue.passes(component)) { return attribValue.apply(value, amount); }
                }
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
        public double modifyStat(String key, double base, int amount) {
            if (statModifiers.containsKey(key)) {
                return statModifiers.get(key).compute(base, amount);
            }
            return base;
        }

        /**
         * Loads a dynamic group globals settings into the given map
         *
         * @param data   config data to load from
         * @param type the component type to load for
         */
        private void loadGroup(DataSection data, ComponentType type) {
            if (data == null) { return; }

            final Map<String, AttributeValue[]> target = dynamicModifiers.computeIfAbsent(type, t -> new HashMap<>());
            for (String key : data.keys()) {
                final String lower = key.toLowerCase();
                Logger.log(LogType.ATTRIBUTE_LOAD, 2, "    SkillMod: " + key);
                final String value = data.getString(key);
                final String[] formulas = value.split("\\|");
                final AttributeValue[] values = new AttributeValue[formulas.length];
                int i = 0;
                for (final String formula : formulas) { values[i++] = new AttributeValue(formula); }
                target.put(lower, values);

                if (!byComponent.containsKey(lower)) { byComponent.put(lower, new ArrayList<>()); }
                byComponent.get(lower).add(this);
            }
        }

        /**
         * Loads a stat modifier from the config data
         *
         * @param data config data to load from
         * @param key  key of the stat modifier
         */
        private void loadStatModifier(DataSection data, String key) {
            if (data.has(key)) {
                Logger.log(LogType.ATTRIBUTE_LOAD, 2, "    StatMod: " + key);
                statModifiers.put(key, new Formula(data.getString(key, "v")));

                if (!byStat.containsKey(key)) { byStat.put(key, new ArrayList<>()); }
                byStat.get(key).add(this);
            }
        }
    }

    /**
     * Represents one formula modifier for an attribute
     * that can have conditions
     */
    public class AttributeValue {
        private Formula formula;
        private HashMap<String, String> conditions = new HashMap<>();

        /**
         * Loads the attribute value that starts with the formula
         * and can have as many conditions as desired after
         *
         * @param data data string for the value
         */
        public AttributeValue(String data) {
            String[] pieces = data.split(":");
            formula = new Formula(pieces[0]);
            for (int i = 1; i < pieces.length; i++) {
                String[] sides = pieces[i].split("=");
                conditions.put(sides[0], sides[1]);
                Logger.log(LogType.ATTRIBUTE_LOAD, 3, "      Condition: " + sides[0] + " / " + sides[1]);
            }
        }

        /**
         * Checks whether or not the formula should be applied to the component
         *
         * @param component component to check for conditions against
         *
         * @return true if passes the conditions
         */
        public boolean passes(EffectComponent component) {
            for (String key : conditions.keySet()) {
                if (!component.getSettings().getString(key).equalsIgnoreCase(conditions.get(key))) { return false; }
            }
            return true;
        }

        /**
         * Checks the conditions for the given component
         *
         * @param value  base value
         * @param amount amount of attribute points
         *
         * @return the modified value if the conditions passed or the base value if they failed
         */
        public double apply(double value, int amount) {
            return formula.compute(value, amount);
        }
    }
}
