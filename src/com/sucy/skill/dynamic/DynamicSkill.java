/**
 * SkillAPI
 * com.sucy.skill.dynamic.DynamicSkill
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
import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import com.sucy.skill.dynamic.trigger.TriggerComponent;
import com.sucy.skill.log.Logger;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sucy.skill.dynamic.ComponentRegistry.getTrigger;

/**
 * A skill implementation for the Dynamic system
 */
public class DynamicSkill extends Skill implements SkillShot, PassiveSkill, Listener {
    private final List<TriggerHandler>         triggers   = new ArrayList<>();
    private final Map<String, EffectComponent> attribKeys = new HashMap<>();
    private final Map<Integer, Integer>        active     = new HashMap<>();

    private static final HashMap<Integer, HashMap<String, Object>> castData   = new HashMap<>();

    private TriggerComponent castTrigger;
    private TriggerComponent initializeTrigger;
    private TriggerComponent cleanupTrigger;

    private boolean cancel     = false;
    private double  multiplier = 1;
    private double  bonus      = 0;

    /**
     * Initializes a new dynamic skill
     *
     * @param name name of the skill
     */
    public DynamicSkill(final String name) {
        super(name, "Dynamic", Material.JACK_O_LANTERN, 1);
    }

    /**
     * Checks whether or not the dynamic skill can be cast
     *
     * @return true if can cast, false otherwise
     */
    public boolean canCast() {
        return castTrigger != null;
    }

    /**
     * Checks whether or not the caster's passives are currently active
     *
     * @param caster caster to check for
     *
     * @return true if active, false otherwise
     */
    public boolean isActive(final LivingEntity caster) {
        return active.containsKey(caster.getEntityId());
    }

    /**
     * Retrieves the active level of the caster for the skill
     *
     * @param caster caster of the skill
     *
     * @return active level of the skill
     */
    public int getActiveLevel(final LivingEntity caster) {
        return active.get(caster.getEntityId());
    }

    /**
     * Sets an attribute key for obtaining attributes used
     * in the skill indicator.
     *
     * @param key       key string
     * @param component component to grab attributes from
     */
    void setAttribKey(final String key, final EffectComponent component) {
        attribKeys.put(key, component);
    }

    /**
     * Cancels the event causing a trigger to go off
     */
    public void cancelTrigger() {
        cancel = true;
    }

    void applyCancelled(final Cancellable event) {
        if (checkCancelled()) {
            event.setCancelled(true);
        }
    }

    public boolean checkCancelled() {
        final boolean result = cancel;
        cancel = false;
        return result;
    }

    /**
     * Retrieves the cast data for the caster
     *
     * @param caster caster to get the data for
     *
     * @return cast data for the caster
     */
    public static HashMap<String, Object> getCastData(final LivingEntity caster) {
        if (caster == null) { return null; }
        HashMap<String, Object> map = castData.get(caster.getEntityId());
        if (map == null) {
            map = new HashMap<>();
            map.put("caster", caster);
            castData.put(caster.getEntityId(), map);
        }
        return map;
    }

    /**
     * Clears any stored cast data for the entity
     *
     * @param entity entity to clear cast data for
     */
    public static void clearCastData(final LivingEntity entity) {
        castData.remove(entity.getEntityId());
    }

    /**
     * Registers needed events for the skill, ignoring any unused events for efficiency
     *
     * @param plugin plugin reference
     */
    public void registerEvents(final SkillAPI plugin) {
        for (final TriggerHandler triggerHandler : triggers) {
            triggerHandler.register(plugin);
        }
    }

    /**
     * Updates the skill effects
     *
     * @param user      user to refresh the effect for
     * @param prevLevel previous skill level
     * @param newLevel  new skill level
     */
    @Override
    public void update(final LivingEntity user, final int prevLevel, final int newLevel) {
        active.put(user.getEntityId(), newLevel);
        for (final TriggerHandler triggerHandler : triggers) {
            triggerHandler.init(user, newLevel);
        }
    }

    /**
     * Initializes any corresponding effects when the skill is unlocked/enabled
     *
     * @param user  user to initialize the effects for
     * @param level skill level
     */
    @Override
    public void initialize(final LivingEntity user, final int level) {
        trigger(user, user, level, initializeTrigger);
        active.put(user.getEntityId(), level);
        for (final TriggerHandler triggerHandler : triggers) {
            triggerHandler.init(user, level);
        }
    }

    /**
     * Removes active users from the map
     *
     * @param user  user to stop the effects for
     * @param level skill level
     */
    @Override
    public void stopEffects(final LivingEntity user, final int level) {
        active.remove(user.getEntityId());
        for (final TriggerHandler triggerHandler : triggers) {
            triggerHandler.cleanup(user);
        }
        cleanup(user, castTrigger);
        cleanup(user, initializeTrigger);

        trigger(user, user, 1, cleanupTrigger);
    }

    private void cleanup(final LivingEntity user, final TriggerComponent component) {
        if (component != null) component.cleanUp(user);
    }

    /**
     * Casts the skill if applicable
     *
     * @param user  user of the skill
     * @param level skill level
     *
     * @return true if casted successfully, false if conditions weren't met or no effects are using the cast trigger
     */
    @Override
    public boolean cast(final LivingEntity user, final int level) {
        return trigger(user, user, level, castTrigger);
    }

    /**
     * Retrieves the formatted name of an attribute which ignores the dynamic
     * path overhead.
     *
     * @param key attribute key
     *
     * @return formatted attribute name
     */
    @Override
    protected String getAttrName(String key) {
        if (key.contains(".")) {
            return TextFormatter.format(key.substring(key.lastIndexOf('.') + 1));
        } else { return super.getAttrName(key); }
    }

    /**
     * Retrieves an attribute while supporting dynamic skill attribute paths.
     * Paths are set up by the "icon-key" setting in components. An invalid
     * path will instead return a value of 0. If a path is not provided, this
     * returns a normal attribute on the skill.
     *
     * @param caster owner of the skill
     * @param key    attribute key
     * @param level  skill level
     *
     * @return attribute value or 0 if invalid dynamic path
     */
    @Override
    protected Object getAttr(final LivingEntity caster, final String key, final int level) {
        // Dynamic attribute paths use periods
        if (key.contains(".")) {
            final String[] path = key.split("\\.");
            final String attr = path[1].toLowerCase();
            if (attribKeys.containsKey(path[0]) && attribKeys.get(path[0]).settings.has(attr)) {
                return format(attribKeys.get(path[0]).parseValues(caster, attr, level, 0));
            } else { return 0; }
        }

        // Otherwise get the attribute normally
        else { return super.getAttr(caster, key, level); }
    }

    private boolean trigger(
            final LivingEntity user,
            final LivingEntity target,
            final int level,
            final TriggerComponent component) {
        return component != null && component.trigger(user, target, level);
    }

    /**
     * Loads dynamic components in addition to the normal values
     *
     * @param config config data to load from
     */
    @Override
    public void load(final DataSection config) {
        super.load(config);

        final DataSection triggers = config.getSection("components");
        if (triggers == null) { return; }

        for (final String key : triggers.keys()) {
            final String modified = key.replaceAll("-.+", "");
            try {
                final DataSection settings = triggers.getSection(key);
                if (modified.equalsIgnoreCase("CAST")) {
                    castTrigger = loadComponent(settings);
                } else if (modified.equalsIgnoreCase("INITIALIZE")) {
                    initializeTrigger = loadComponent(settings);
                } else if (modified.equalsIgnoreCase("CLEANUP")) {
                    cleanupTrigger = loadComponent(settings);
                } else {
                    this.triggers.add(new TriggerHandler(this, key, getTrigger(modified), loadComponent(settings)));
                }
            } catch (final Exception ex) {
                // Invalid trigger
                ex.printStackTrace();
                Logger.invalid("Invalid trigger for the skill \"" + getName() + "\" - \"" + key + "\"");
            }
        }
    }

    private TriggerComponent loadComponent(final DataSection data) {
        final TriggerComponent component = new TriggerComponent();
        component.load(this, data);
        return component;
    }

    /**
     * Saves the skill back to the config, appending component data
     * on top of the normal skill data
     *
     * @param config config to save to
     */
    @Override
    public void save(final DataSection config) {
        super.save(config);
        final DataSection triggers = config.createSection("components");
        for (final TriggerHandler triggerHandler : this.triggers) {
            triggerHandler.getComponent()
                    .save(triggers.createSection(TextFormatter.format(triggerHandler.getKey())));
        }
        save(triggers, castTrigger, "Cast");
        save(triggers, initializeTrigger, "Initialize");
        save(triggers, cleanupTrigger, "Cleanup");
    }

    private void save(final DataSection triggers, final TriggerComponent component, final String key) {
        if (component != null) component.save(triggers.createSection(TextFormatter.format(key)));
    }
}
