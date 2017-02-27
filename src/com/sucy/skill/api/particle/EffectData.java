/**
 * SkillAPI
 * com.sucy.skill.api.particle.EffectData
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

import com.sucy.skill.api.particle.target.EffectTarget;

import java.util.HashMap;
import java.util.Iterator;

/**
 * A collection of effects played on a target
 */
public class EffectData
{
    private static HashMap<String, EffectInstance> effects = new HashMap<String, EffectInstance>();

    private EffectTarget target;

    /**
     * @param target target of each effect
     */
    public EffectData(EffectTarget target)
    {
        this.target = target;
    }

    /**
     * Checks whether or not an effect is still running
     *
     * @param key effect key
     *
     * @return true if running
     */
    public static boolean isEffectActive(String key)
    {
        return effects.containsKey(key);
    }

    /**
     * Fetches an active effect by key
     *
     * @param key effect key
     *
     * @return active effect or null if not found
     */
    public EffectInstance getEffect(String key)
    {
        return effects.get(key);
    }

    /**
     * @return true if should keep the data, false otherwise
     */
    public boolean isValid()
    {
        return effects.size() > 0 && target.isValid();
    }

    /**
     * Starts running an effect for the target. If the effect is already
     * running for the target, the running effect will be stopped before
     * the new one is started.
     *
     * @param effect effect to run
     * @param ticks  ticks to run for
     * @param level  effect level
     */
    public void runEffect(ParticleEffect effect, int ticks, int level)
    {
        EffectInstance instance = new EffectInstance(effect, target, level);
        instance.extend(ticks);
        effects.put(effect.getName(), instance);
    }

    /**
     * Cancels an effect via its associated key
     *
     * @param key key of the effect to cancel
     */
    public void cancel(String key) {
        effects.remove(key);
    }

    /**
     * Ticks each effect for the target
     */
    public void tick()
    {
        Iterator<EffectInstance> iterator = effects.values().iterator();
        while (iterator.hasNext())
        {
            EffectInstance effect = iterator.next();
            if (effect.isValid())
                effect.tick();
            else
                iterator.remove();
        }
    }
}
