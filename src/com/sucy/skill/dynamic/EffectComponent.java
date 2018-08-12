/**
 * SkillAPI
 * com.sucy.skill.dynamic.EffectComponent
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
package com.sucy.skill.dynamic;

import com.rit.sucy.config.parse.DataSection;
import com.rit.sucy.mobs.MobManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.cast.IIndicator;
import com.sucy.skill.cast.IndicatorType;
import com.sucy.skill.log.Logger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A component for dynamic skills which takes care of one effect
 */
public abstract class EffectComponent {
    private static final String ICON_KEY   = "icon-key";
    private static final String COUNTS_KEY = "counts";

    private static boolean passed;

    /**
     * Child components
     */
    public final ArrayList<EffectComponent> children = new ArrayList<>();

    /**
     * The settings for the component
     */
    protected final Settings settings = new Settings();

    /**
     * Parent class of the component
     */
    protected DynamicSkill skill;

    /**
     * Type of indicators to show
     */
    protected IndicatorType indicatorType;

    /**
     * Whether or not the component has preview effects
     */
    public boolean hasEffect;

    private String instanceKey;

    /**
     * Retrieves the config key for the component
     *
     * @return config key of the component
     */
    public String getInstanceKey() {
        return instanceKey;
    }

    public abstract String getKey();

    /**
     * Retrieves the type of the component
     *
     * @return component type
     */
    public abstract ComponentType getType();

    /**
     * Retrieves the settings of the dynamic component
     *
     * @return settings of the dynamic component
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Checks whether or not the component or its children have an effect
     *
     * @return true if has an effect, false otherwise
     */
    private boolean hasEffect() {
        if (indicatorType != IndicatorType.NONE) { return true; }

        for (EffectComponent child : children) { if (child.hasEffect()) { return true; } }

        return false;
    }

    /**
     * Retrieves an attribute value while applying attribute
     * data if enabled and a player is using the skill
     *
     * @param caster   caster of the skill
     * @param key      key of the value to grab
     * @param level    level of the skill
     * @param fallback default value for the attribute
     *
     * @return the value with attribute modifications if applicable
     */
    protected double parseValues(LivingEntity caster, String key, int level, double fallback) {
        double base = getNum(caster, key + "-base", fallback);
        double scale = getNum(caster, key + "-scale", 0);
        double value = base + (level - 1) * scale;

        // Apply global modifiers
        if (SkillAPI.getSettings().isAttributesEnabled() && caster instanceof Player) {
            PlayerData data = SkillAPI.getPlayerData((Player) caster);
            value = data.scaleDynamic(this, key, value);
        }

        return value;
    }

    /**
     * Retrieves a numerical value while using non-numerical values as
     * keys for the cast data. If the value doesn't exist, this will
     * return the default value. If it is a key that doesn't have an
     * attached value, it will return 0. Otherwise, it will return
     * the appropriate value.
     *
     * @param caster   the caster of the skill
     * @param key      key of the value
     * @param fallback fallback value in case the settings don't have it
     *
     * @return the settings value or, if not a number, the cast data value
     */
    protected double getNum(LivingEntity caster, String key, double fallback) {
        String val = settings.getString(key);
        if (val == null) { return fallback; }

        try {
            return Double.parseDouble(val);
        } catch (Exception ex) { /* Not a number */ }

        final Map<String, Object> map = DynamicSkill.getCastData(caster);
        if (map.containsKey(val)) {
            final String mapVal = map.get(val).toString();
            try {
                return Double.parseDouble(mapVal);
            } catch (Exception ex) { /* Not a number */ }
        }

        try {
            final int mid = val.indexOf('-', 1);
            final double min = Double.parseDouble(val.substring(0, mid));
            final double max = Double.parseDouble(val.substring(mid + 1));
            return Math.random() * (max - min) + min;
        } catch (Exception ex) { /* Not a range */ }

        return 0;
    }

    /**
     * Checks whether or not the last component passed or not
     *
     * @return true if passed, false otherwise
     */
    protected boolean lastPassed() {
        return passed;
    }

    /**
     * Executes the children of the component using the given targets
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to execute on
     *
     * @return true if executed, false if conditions not met
     */
    protected boolean executeChildren(LivingEntity caster, int level, List<LivingEntity> targets) {
        if (targets.isEmpty()) {
            return false;
        }

        boolean worked = false;
        for (EffectComponent child : children) {
            boolean counts = !child.settings.getString(COUNTS_KEY, "true").toLowerCase().equals("false");
            passed = child.execute(caster, level, targets);
            worked = (passed && counts) || worked;
        }
        return worked;
    }

    public void cleanUp(final LivingEntity caster) {
        doCleanUp(caster);
        children.forEach(child -> child.cleanUp(caster));
    }

    protected void doCleanUp(final LivingEntity caster) { }

    /**
     * Gets the skill data for the caster
     *
     * @param caster caster of the skill
     *
     * @return skill data for the caster or null if not found
     */
    protected PlayerSkill getSkillData(LivingEntity caster) {
        if (caster instanceof Player) {
            return SkillAPI.getPlayerData((Player) caster).getSkill(skill.getName());
        } else {
            return null;
        }
    }

    protected String filter(LivingEntity caster, LivingEntity target, String text) {
        // Grab values
        int i = text.indexOf('{');
        if (i < 0) { return text; }

        int j = text.indexOf('}', i);
        if (j < 0) { return text; }

        StringBuilder builder = new StringBuilder();
        HashMap<String, Object> data = DynamicSkill.getCastData(caster);

        int k = 0;
        while (i >= 0 && j > i) {
            String key = text.substring(i + 1, j);
            if (data.containsKey(key)) {
                Object obj = data.get(key);
                if (obj instanceof Player) { obj = ((Player) obj).getName(); } else if (obj instanceof LivingEntity) {
                    obj = MobManager.getName((LivingEntity) obj);
                }
                builder.append(text.substring(k, i));
                builder.append(obj);

                k = j + 1;
            } else if (key.equals("player")) {
                builder.append(text.substring(k, i));
                builder.append(caster.getName());

                k = j + 1;
            } else if (key.equals("target")) {
                builder.append(text.substring(k, i));
                builder.append(target.getName());

                k = j + 1;
            }
            i = text.indexOf('{', j);
            j = text.indexOf('}', i);
        }
        builder.append(text.substring(k));
        return builder.toString();
    }

    /**
     * Executes the component (to be implemented)
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to execute on
     *
     * @return true if executed, false if conditions not met
     */
    public abstract boolean execute(LivingEntity caster, int level, List<LivingEntity> targets);

    /**
     * Creates the list of indicators for the skill
     *
     * @param list    list to store indicators in
     * @param caster  caster reference
     * @param targets location to base location on
     * @param level   the level of the skill to create for
     */
    public void makeIndicators(List<IIndicator> list, Player caster, List<LivingEntity> targets, int level) {
        if (hasEffect) {
            for (EffectComponent component : children) { component.makeIndicators(list, caster, targets, level); }
        }
    }

    private static final String TYPE      = "type";
    private static final String INDICATOR = "indicator";

    /**
     * Saves the component and its children to the config
     *
     * @param config config to save to
     */
    public void save(DataSection config) {
        config.set(TYPE, getType().name().toLowerCase());
        config.set(INDICATOR, indicatorType.getKey());
        settings.save(config.createSection("data"));
        DataSection children = config.createSection("children");
        for (EffectComponent child : this.children) {
            child.save(children.createSection(child.instanceKey));
        }
    }

    /**
     * Loads component data from the configuration
     *
     * @param skill  owning skill of the component
     * @param config config data to load from
     */
    public void load(DynamicSkill skill, DataSection config) {
        this.skill = skill;
        if (config == null) {
            return;
        }
        settings.load(config.getSection("data"));
        if (settings.has(ICON_KEY)) {
            String key = settings.getString(ICON_KEY);
            if (!key.equals("")) {
                skill.setAttribKey(key, this);
            }
        }
        indicatorType = IndicatorType.getByKey(settings.getString(INDICATOR, "2D"));

        DataSection children = config.getSection("children");
        if (children != null) {
            for (String key : children.keys()) {
                final String typeName = children.getSection(key).getString(TYPE, "missing").toUpperCase();
                final ComponentType type = ComponentType.valueOf(typeName);
                final String mkey = key.replaceAll("-.+", "");
                try {
                    final EffectComponent child = ComponentRegistry.getComponent(type, mkey);
                    if (child != null) {
                        child.instanceKey = key;
                        child.load(skill, children.getSection(key));
                        this.children.add(child);
                    } else {
                        Logger.invalid("Invalid " + type + " component: " + mkey);
                    }
                } catch (Exception ex) {
                    // Failed to create the component, just don't add it
                    Logger.bug("Failed to create " + type + " component: " + key);
                }
            }
        }

        hasEffect = hasEffect();
    }
}
