/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.SoundMechanic
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
package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.log.Logger;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Plays a particle effect
 */
public class SoundMechanic extends EffectComponent
{
    private static final String SOUND  = "sound";
    private static final String SOUND2 = "newsound";
    private static final String VOLUME = "volume";
    private static final String PITCH  = "pitch";

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     *
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets)
    {
        if (targets.size() == 0)
        {
            return false;
        }

        String type = settings.getString(SOUND, settings.getString(SOUND2, "")).toUpperCase().replace(" ", "_");
        try
        {
            Sound sound = Sound.valueOf(type);
            float volume = (float) attr(caster, VOLUME, level, 100.0, true) / 100;
            float pitch = (float) attr(caster, PITCH, level, 0.0, true);

            volume = Math.max(0, volume);
            pitch = Math.min(2, Math.max(0.5f, pitch));

            for (LivingEntity target : targets)
            {
                target.getWorld().playSound(target.getLocation(), sound, volume, pitch);
            }
            return targets.size() > 0;
        }
        catch (Exception ex)
        {
            Logger.invalid("Invalid sound type: " + type);
            return false;
        }
    }
}
