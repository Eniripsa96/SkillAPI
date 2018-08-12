/**
 * SkillAPI
 * com.sucy.skill.dynamic.target.OffsetTarget
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
package com.sucy.skill.dynamic.target;

import com.google.common.collect.ImmutableList;
import com.sucy.skill.cast.IIndicator;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Applies child effects to a location offset from the current targets
 */
public class OffsetTarget extends TargetComponent {
    private static final Vector UP = new Vector(0, 1, 0);

    private static final String FORWARD    = "forward";
    private static final String UPWARD     = "upward";
    private static final String RIGHT      = "right";
    private static final String HORIZONTAL = "horizontal";

    /** {@inheritDoc} */
    @Override
    public void makeIndicators(List<IIndicator> list, Player caster, LivingEntity target, int level) {
        makeCircleIndicator(list, getTargetLoc(caster, level, target), 0.5);
    }

    /** {@inheritDoc} */
    @Override
    List<LivingEntity> getTargets(
            final LivingEntity caster, final int level, final List<LivingEntity> targets) {
        return determineTargets(caster, level, targets, t -> ImmutableList.of(getTargetLoc(caster, level, t)));
    }

    private TempEntity getTargetLoc(LivingEntity caster, int level, LivingEntity t) {
        final boolean horizontal = settings.getBool(HORIZONTAL, false);
        final double forward = parseValues(caster, FORWARD, level, 0);
        final double upward = parseValues(caster, UPWARD, level, 0);
        final double right = parseValues(caster, RIGHT, level, 0);

        final Vector dir = t.getLocation().getDirection().setY(0).normalize();
        if (horizontal) { dir.setY(0).normalize(); }

        final Vector nor = dir.clone().crossProduct(UP);
        dir.multiply(forward);
        dir.add(nor.multiply(right)).setY(upward);

        return new TempEntity(t.getLocation().add(dir));
    }

    @Override
    public String getKey() {
        return "offset";
    }
}
