/**
 * SkillAPI
 * com.sucy.skill.dynamic.target.NearestTarget
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

import com.sucy.skill.api.util.Nearby;
import com.sucy.skill.cast.IIndicator;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;

/**
 * Applies child components to the closest all nearby entities around
 * each of the current targets.
 */
public class NearestTarget extends TargetComponent {
    private static final String RADIUS = "radius";

    /** {@inheritDoc} */
    @Override
    List<LivingEntity> getTargets(
            final LivingEntity caster, final int level, final List<LivingEntity> targets) {

        final double radius = parseValues(caster, RADIUS, level, 3.0);
        return determineTargets(caster, level, targets,
                t -> sort(Nearby.getLivingNearby(t.getLocation(), radius), t.getLocation()));
    }

    /** {@inheritDoc} */
    @Override
    void makeIndicators(final List<IIndicator> list, final Player caster, final LivingEntity target, final int level) {
        makeCircleIndicator(list, target, parseValues(caster, RADIUS, level, 3.0));
    }

    @Override
    public String getKey() {
        return "nearest";
    }

    private List<LivingEntity> sort(final List<LivingEntity> list, final Location loc) {
        list.sort(Comparator.comparing(e -> e.getLocation().distanceSquared(loc)));
        return list;
    }
}
