/**
 * SkillAPI
 * com.sucy.skill.dynamic.target.ConeTarget
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
package com.sucy.skill.dynamic.target;

import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.cast.IIndicator;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Applies child components to the closest all nearby entities around
 * each of the current targets.
 */
public class ConeTarget extends TargetComponent {
    private static final String ANGLE  = "angle";
    private static final String RANGE  = "range";

    /** {@inheritDoc} */
    @Override
    void makeIndicators(List<IIndicator> list, Player caster, LivingEntity target, int level) {
        double range = parseValues(caster, RANGE, level, 3.0);
        double angle = parseValues(caster, ANGLE, level, 90.0);
        makeConeIndicator(list, target, range, angle);
    }

    /** {@inheritDoc} */
    @Override
    List<LivingEntity> getTargets(LivingEntity caster, int level, List<LivingEntity> targets) {
        double range = parseValues(caster, RANGE, level, 3.0);
        double angle = parseValues(caster, ANGLE, level, 90.0);
        return determineTargets(caster, level, targets, t -> TargetHelper.getConeTargets(t, angle, range));
    }

    @Override
    public String getKey() {
        return "cone";
    }
}
