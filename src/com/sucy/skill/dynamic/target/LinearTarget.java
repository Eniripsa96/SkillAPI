/**
 * SkillAPI
 * com.sucy.skill.dynamic.target.LinearTarget
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
 * Applies child components to the entities in a line in front of each of the
 * provided targets.
 */
public class LinearTarget extends TargetComponent {
    private static final String RANGE     = "range";
    private static final String TOLERANCE = "tolerance";

    /** {@inheritDoc} */
    @Override
    List<LivingEntity> getTargets(
            final LivingEntity caster, final int level, final List<LivingEntity> targets) {
        final double tolerance = parseValues(caster, TOLERANCE, level, 4.0);
        final double range = parseValues(caster, RANGE, level, 5.0);
        return determineTargets(caster, level, targets, t -> TargetHelper.getLivingTargets(t, range, tolerance));
    }

    /** {@inheritDoc} */
    @Override
    void makeIndicators(
            final List<IIndicator> list, final Player caster, final LivingEntity target, final int level) {
        // TODO - add indicators for linear targeting
    }

    @Override
    public String getKey() {
        return "linear";
    }
}
