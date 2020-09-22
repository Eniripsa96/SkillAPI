/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.ParticleEffectMechanic
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
package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.particle.EffectPlayer;
import com.sucy.skill.api.particle.target.EntityTarget;
import org.bukkit.entity.LivingEntity;

import java.util.List;

public class ParticleEffectMechanic extends MechanicComponent
{
    private static final String DURATION = "duration";
    private static final String KEY      = "effect-key";

    @Override
    public String getKey() {
        return "particle effect";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets)
    {
        String key = settings.getString(KEY, skill.getName());
        int duration = (int) (20 * parseValues(caster, DURATION, level, 5));

        EffectPlayer player = new EffectPlayer(settings);
        for (LivingEntity target : targets)
            player.start(new EntityTarget(target), key, duration, level, true);

        return targets.size() > 0;
    }
}
