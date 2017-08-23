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

import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.cast.CircleIndicator;
import com.sucy.skill.cast.IIndicator;
import com.sucy.skill.cast.IndicatorType;
import com.sucy.skill.cast.SphereIndicator;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Applies child components to a location using the caster's faced direction
 */
public class LocationTarget extends EffectComponent
{
    private static final String RANGE  = "range";
    private static final String GROUND = "ground";

    private static final Set<Material> NULL_SET = null;

    /**
     * Creates the list of indicators for the skill
     *
     * @param list   list to store indicators in
     * @param caster caster reference
     * @param target location to base location on
     * @param level  the level of the skill to create for
     */
    @Override
    public void makeIndicators(List<IIndicator> list, Player caster, LivingEntity target, int level)
    {
        target = getTargetLoc(caster, level, target);
        if (target == null)
            return;

        if (indicatorType == IndicatorType.DIM_3)
        {
            Location loc = target.getLocation();
            IIndicator indicator = new SphereIndicator(0.5);
            indicator.moveTo(loc.getX(), loc.getY() + 0.1, loc.getZ());
            list.add(indicator);
        }
        else if (indicatorType == IndicatorType.DIM_2)
        {
            Location loc = target.getLocation();
            IIndicator indicator = new CircleIndicator(0.5);
            indicator.moveTo(loc.getX(), loc.getY() + 0.1, loc.getZ());
            list.add(indicator);
        }

        for (EffectComponent component : children)
            component.makeIndicators(list, caster, target, level);
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
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets)
    {
        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity t : targets)
        {
            TempEntity temp = getTargetLoc(caster, level, t);
            if (temp == null)
                continue;

            list.add(temp);
        }
        return list.size() > 0 && executeChildren(caster, level, list);
    }

    private TempEntity getTargetLoc(LivingEntity caster, int level, LivingEntity t)
    {
        boolean isSelf = caster == t;
        double range = attr(caster, RANGE, level, 5.0, isSelf);
        boolean groundOnly = !settings.getString(GROUND, "true").toLowerCase().equals("false");

        Location loc = calcTargetLoc(t, range);
        if (groundOnly && AIR_BLOCKS.contains(loc.getBlock().getType())) {
            return null;
        }

        if (!groundOnly) {
            LivingEntity target = TargetHelper.getLivingTarget(t, range, 4);
            Location casterLoc = caster.getLocation();
            if (target != null && target.getLocation().distanceSquared(casterLoc) < loc.distanceSquared(casterLoc)) {
                loc = target.getLocation();
            }
        }

        return new TempEntity(loc);
    }

    private Location calcTargetLoc(LivingEntity entity, double maxRange) {
        Location start = entity.getEyeLocation();
        Vector dir = entity.getLocation().getDirection();
        if (dir.getX() == 0) dir.setX(Double.MIN_NORMAL);
        if (dir.getY() == 0) dir.setY(Double.MIN_NORMAL);
        if (dir.getZ() == 0) dir.setY(Double.MIN_NORMAL);
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
}
