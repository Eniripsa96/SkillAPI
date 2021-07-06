/**
 * SkillAPI
 * com.sucy.skill.api.particle.EffectManager
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
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
package com.sucy.skill.api.particle;

import com.rit.sucy.config.CommentedConfig;
import com.rit.sucy.config.parse.DataSection;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.particle.direction.XZHandler;
import com.sucy.skill.api.particle.target.EffectTarget;
import com.sucy.skill.api.particle.target.EntityTarget;
import com.sucy.skill.task.EffectTask;
import com.sucy.skill.thread.MainThread;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles the management of particle effects and related components
 */
public class EffectManager {
    private static Map<EffectTarget, EffectData> instances = new ConcurrentHashMap<EffectTarget, EffectData>();
    private static Map<String, ParticleEffect>   effects   = new HashMap<String, ParticleEffect>();
    private static Map<String, PolarSettings>    formulas  = new HashMap<String, PolarSettings>();

    /**
     * Initializes the utility, loading formulas from the config file
     */
    public static void init() {
        CommentedConfig config = SkillAPI.getConfig("effects");
        config.saveDefaultConfig();
        DataSection data = config.getConfig();
        for (String key : data.keys()) {
            formulas.put(key, new PolarSettings(data.getSection(key)));
            if (key.equals("one-circle")) { formulas.get(key).getPoints(XZHandler.instance); }
        }

        MainThread.register(new EffectTask());
    }

    public static void cleanUp() {
        formulas.clear();
        effects.clear();
        instances.clear();
    }

    /**
     * Registers a new particle effect, replacing any conflicting
     * effects already registered under the key
     *
     * @param effect effect to register
     */
    public static void register(ParticleEffect effect) {
        if (effect != null) { effects.put(effect.getName(), effect); }
    }

    /**
     * Registers a new formula for effects
     *
     * @param key     key to register under
     * @param formula formula to register
     */
    public static void register(String key, PolarSettings formula) {
        if (formula != null) { formulas.put(key, formula); }
    }

    /**
     * Gets a formula by key
     *
     * @param key formula key
     *
     * @return formula
     */
    public static PolarSettings getFormula(String key) {
        return formulas.get(key);
    }

    /**
     * Fetches an effect by key
     *
     * @param name name of the effect
     *
     * @return particle effect
     */
    public static ParticleEffect getEffect(String name) {
        return effects.get(name);
    }

    /**
     * Clears effects for a given target
     *
     * @param target target to clear for
     */
    public static void clear(EffectTarget target) {
        instances.remove(target);
    }

    /**
     * Clears effects for a given target
     *
     * @param target target to clear for
     */
    public static void clear(LivingEntity target) {
        instances.entrySet()
                .removeIf(entry -> entry.getKey() instanceof EntityTarget && ((EntityTarget) entry.getKey()).getEntity() == target);
    }

    /**
     * Gets the effect data for the given target
     *
     * @param target target to get the data for
     * @return effect data for the target or null if doesn't exist
     */
    public static EffectData getEffectData(EffectTarget target) {
        return instances.get(target);
    }

    /**
     * Fetches an active effect for a given target
     *
     * @param target target to get the effect for
     * @param key    effect key
     *
     * @return active effect or null if not found
     */
    public static EffectInstance getEffect(EffectTarget target, String key) {
        if (!instances.containsKey(target)) { return null; }

        return instances.get(target).getEffect(key);
    }

    /**
     * Starts running an effect for a target. If the effect is already
     * running for the target, the running effect will be stopped before
     * the new one is started.
     *
     * @param effect effect to run
     * @param target target to run for
     * @param ticks  ticks to run for
     * @param level  effect level
     */
    public static void runEffect(ParticleEffect effect, EffectTarget target, int ticks, int level) {
        if (!instances.containsKey(target)) { instances.put(target, new EffectData(target)); }
        instances.get(target).runEffect(effect, ticks, level);
    }

    /**
     * Ticks all active effects
     */
    public static void tick() {
        Iterator<EffectData> iterator = instances.values().iterator();
        while (iterator.hasNext()) {
            EffectData data = iterator.next();
            if (data.isValid()) { data.tick(); } else { iterator.remove(); }
        }
    }
}
