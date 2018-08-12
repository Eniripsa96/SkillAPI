/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.SpeedMechanic
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

import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.listener.AttributeListener;
import com.sucy.skill.listener.MechanicListener;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Applies a flag to each target
 */
public class SpeedMechanic extends MechanicComponent {
    private static final String MULTIPLIER = "multiplier";
    private static final String DURATION   = "duration";

    @Override
    public String getKey() {
        return "speed";
    }

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
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets) {
        float multiplier = (float) parseValues(caster, MULTIPLIER, level, 1.2);
        double seconds = parseValues(caster, DURATION, level, 3.0);
        int ticks = (int) (seconds * 20);
        boolean worked = false;
        for (LivingEntity target : targets) {
            if (!(target instanceof Player)) { continue; }

            AttributeListener.refreshSpeed((Player) target);
            FlagManager.addFlag(target, MechanicListener.SPEED_KEY, ticks);
            ((Player) target).setWalkSpeed(multiplier * ((Player) target).getWalkSpeed());
            worked = true;
        }
        return worked;
    }
}
