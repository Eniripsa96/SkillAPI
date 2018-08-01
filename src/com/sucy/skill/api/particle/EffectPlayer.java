/**
 * SkillAPI
 * com.sucy.skill.api.particle.EffectPlayer
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 Steven Sucy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.api.particle;

import com.sucy.skill.api.Settings;
import com.sucy.skill.api.particle.direction.Directions;
import com.sucy.skill.api.particle.target.EffectTarget;
import com.sucy.skill.log.Logger;
import org.bukkit.Material;

/**
 * Handles playing effects based on configuration settings
 */
public class EffectPlayer
{
    public static final String SHAPE      = "-shape";
    public static final String SHAPE_DIR  = "-shape-dir";
    public static final String SHAPE_SIZE = "-shape-size";
    public static final String ANIMATION  = "-animation";
    public static final String ANIM_DIR   = "-anim-dir";
    public static final String ANIM_SIZE  = "-anim-size";
    public static final String INTERVAL   = "-interval";
    public static final String VIEW_RANGE = "-view-range";

    public static final String P_TYPE = "-particle-type";
    public static final String MAT    = "-particle-material";
    public static final String DATA   = "-particle-data";
    public static final String AMOUNT = "-particle-amount";
    public static final String DX     = "-particle-dx";
    public static final String DY     = "-particle-dy";
    public static final String DZ     = "-particle-dz";
    public static final String SPEED  = "-particle-speed";

    private Settings settings;

    /**
     * Sets up an effect player that applies effects based of the values in the provided settings.
     * All of the available settings are provided as static values in this class.
     *
     * @param settings settings to read from
     */
    public EffectPlayer(Settings settings)
    {
        this.settings = settings;
    }

    /**
     * Plays a particle effect, grabbing values from the settings data
     *
     * @param target target to play for
     * @param key    effect key to use
     * @param ticks  duration of effect in ticks
     * @param level  level of the effect
     */
    public void start(EffectTarget target, String key, int ticks, int level)
    {
        start(target, key, ticks, level, false);
    }

    /**
     * Plays a particle effect, grabbing values from the settings data
     *
     * @param target   target to play for
     * @param key      effect key to use
     * @param ticks    duration of effect in ticks
     * @param level    level of the effect
     * @param noPrefix exclude prefix when grabbing settings
     */
    public void start(EffectTarget target, String key, int ticks, int level, boolean noPrefix)
    {
        // If the effect is already running, just refresh it
        EffectInstance instance = EffectManager.getEffect(target, key);
        if (instance != null)
        {
            instance.extend(ticks);
            return;
        }

        // If the effect is not registered, make it
        if (EffectManager.getEffect(key) == null)
            makeEffect(key, noPrefix);

        // Play the effect
        EffectManager.runEffect(EffectManager.getEffect(key), target, ticks, level);
    }

    /**
     * Creates and registers an effect
     *
     * @param key      effect key
     * @param noPrefix exclude prefix when grabbing settings
     */
    private void makeEffect(String key, boolean noPrefix)
    {
        String keyMod = noPrefix ? "" : key;

        // Grab the particle type
        ParticleType particleType = ParticleLookup.find(settings.getString(keyMod + P_TYPE, "SPELL"));
        ParticleSettings particle;

        // Block Crack and the related use materials
        if (particleType.usesMat())
        {
            try
            {
                particle = new ParticleSettings(
                    particleType,
                    (float) settings.getDouble(keyMod + DX),
                    (float) settings.getDouble(keyMod + DY),
                    (float) settings.getDouble(keyMod + DZ),
                    (float) settings.getDouble(keyMod + SPEED, 1),
                    settings.getInt(keyMod + AMOUNT, 1),
                    Material.matchMaterial(settings.getString(keyMod + MAT, "DIRT")),
                    settings.getInt(keyMod + DATA)
                );
            }
            catch (Exception ex)
            {
                Logger.invalid("Bad material for particle effect - " + settings.getString(keyMod + MAT));
                return;
            }
        }

        // Others just use basic data
        else
            particle = new ParticleSettings(
                particleType,
                (float) settings.getDouble(keyMod + DX),
                (float) settings.getDouble(keyMod + DY),
                (float) settings.getDouble(keyMod + DZ),
                (float) settings.getDouble(keyMod + SPEED, 1),
                settings.getInt(keyMod + AMOUNT, 1)
            );

        // Make the effect
        ParticleEffect effect = new ParticleEffect(
            key,
            EffectManager.getFormula(settings.getString(keyMod + SHAPE, "single")),
            EffectManager.getFormula(settings.getString(keyMod + ANIMATION, "single")),
            particle,
            Directions.byName(settings.getString(keyMod + SHAPE_DIR, "XZ")),
            Directions.byName(settings.getString(keyMod + ANIM_DIR, "XZ")),
            settings.getString(keyMod + SHAPE_SIZE, "1"),
            settings.getString(keyMod + ANIM_SIZE, "1"),
            settings.getInt(keyMod + INTERVAL, 1),
            settings.getInt(keyMod + VIEW_RANGE, 25)
        );

        // Register the effect
        EffectManager.register(effect);
    }
}
