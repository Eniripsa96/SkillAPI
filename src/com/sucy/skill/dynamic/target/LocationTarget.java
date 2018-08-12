/**
 * SkillAPI
 * com.sucy.skill.dynamic.target.LocationTarget
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

import com.google.common.collect.ImmutableList;
import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.cast.IIndicator;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Applies child components to a location using the caster's faced direction
 */
public class LocationTarget extends TargetComponent {
    private static final String RANGE = "range";
    private static final String GROUND = "ground";

    /** {@inheritDoc} */
    @Override
    public void makeIndicators(List<IIndicator> list, Player caster, LivingEntity target, int level) {
        final List<LivingEntity> locs = getTargets(caster, level, ImmutableList.of(target));
        if (!locs.isEmpty()) makeCircleIndicator(list, locs.get(0), 0.5);
    }

    /** {@inheritDoc} */
    @Override
    List<LivingEntity> getTargets(
            final LivingEntity caster, final int level, final List<LivingEntity> targets) {
        final double range = parseValues(caster, RANGE, level, 5.0);
        final boolean groundOnly = !settings.getString(GROUND, "true").toLowerCase().equals("false");
        return determineTargets(caster, level, targets, t -> getTargetLoc(caster, t, range, groundOnly));
    }

    private List<LivingEntity> getTargetLoc(
            LivingEntity caster,
            LivingEntity t,
            final double range,
            final boolean groundOnly) {
        Location loc = calcTargetLoc(t, range);
        if (groundOnly && AIR_BLOCKS.contains(loc.getBlock().getType())) {
            return ImmutableList.of();
        }

        if (!groundOnly) {
            final LivingEntity target = TargetHelper.getLivingTarget(t, range, 4);
            final Location casterLoc = caster.getLocation();
            if (target != null && target.getLocation().distanceSquared(casterLoc) < loc.distanceSquared(casterLoc)) {
                loc = target.getLocation();
            }
        }

        return ImmutableList.of(new TempEntity(loc));
    }

    private Location calcTargetLoc(LivingEntity entity, double maxRange) {
        Location start = entity.getEyeLocation();
        Vector dir = entity.getLocation().getDirection();
        if (dir.getX() == 0) { dir.setX(Double.MIN_NORMAL); }
        if (dir.getY() == 0) { dir.setY(Double.MIN_NORMAL); }
        if (dir.getZ() == 0) { dir.setY(Double.MIN_NORMAL); }
        int ox = dir.getX() > 0 ? 1 : 0;
        int oy = dir.getY() > 0 ? 1 : 0;
        int oz = dir.getZ() > 0 ? 1 : 0;

        while (AIR_BLOCKS.contains(start.getBlock().getType()) && maxRange > 0) {
            double dxt = computeWeight(start.getX(), dir.getX(), ox);
            double dyt = computeWeight(start.getY(), dir.getY(), oy);
            double dzt = computeWeight(start.getZ(), dir.getZ(), oz);
            double t = Math.min(dxt, Math.min(dyt, Math.min(dzt, maxRange))) + 1E-5;

            start.add(dir.clone().multiply(t));
            maxRange -= t;
        }

        return start;
    }

    private double computeWeight(double val, double dir, int offset) {
        return (Math.floor(val + offset) - val) / dir;
    }

    private static final Set<Material> AIR_BLOCKS = new HashSet<Material>();

    static {
        for (Material material : Material.values()) {
            if (material.isTransparent()) {
                AIR_BLOCKS.add(material);
            }
        }
    }

    @Override
    public String getKey() {
        return "location";
    }
}
