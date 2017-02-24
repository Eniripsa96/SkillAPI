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

import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.util.Nearby;
import com.sucy.skill.cast.CircleIndicator;
import com.sucy.skill.cast.IIndicator;
import com.sucy.skill.cast.IndicatorType;
import com.sucy.skill.cast.SphereIndicator;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies child components to the closest entities to the caster within a given range
 */
public class NearestTarget extends EffectComponent
{
    private static final String RADIUS = "radius";
    private static final String ALLY   = "group";
    private static final String MAX    = "max";
    private static final String WALL   = "wall";
    private static final String CASTER = "caster";

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
        if (indicatorType == IndicatorType.DIM_3)
        {
            Location loc = target.getLocation();
            IIndicator indicator = new SphereIndicator(attr(caster, RADIUS, level, 3.0, true));
            indicator.moveTo(loc.getX(), loc.getY() + 0.1, loc.getZ());
            list.add(indicator);
        }
        else if (indicatorType == IndicatorType.DIM_2)
        {
            Location loc = target.getLocation();
            IIndicator indicator = new CircleIndicator(attr(caster, RADIUS, level, 3.0, true));
            indicator.moveTo(loc.getX(), loc.getY() + 0.1, loc.getZ());
            list.add(indicator);
        }

        List<LivingEntity> targets = null;
        for (EffectComponent component : children)
        {
            if (component.hasEffect)
            {
                if (targets == null)
                    targets = getTargets(caster, level, target);

                for (LivingEntity t : targets)
                    component.makeIndicators(list, caster, t, level);
            }
        }
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
        boolean worked = false;
        for (LivingEntity t : targets)
        {
            List<LivingEntity> list = getTargets(caster, level, t);
            worked = (list.size() > 0 && executeChildren(caster, level, list)) || worked;
        }
        return worked;
    }

    private List<LivingEntity> getTargets(LivingEntity caster, int level, LivingEntity t)
    {
        boolean isSelf = t == caster;
        double radius = attr(caster, RADIUS, level, 3.0, isSelf);
        boolean both = settings.getString(ALLY, "enemy").toLowerCase().equals("both");
        boolean ally = settings.getString(ALLY, "enemy").toLowerCase().equals("ally");
        boolean throughWall = settings.getString(WALL, "false").toLowerCase().equals("true");
        boolean self = settings.getString(CASTER, "false").toLowerCase().equals("true");
        int max = (int) attr(caster, MAX, level, 1, isSelf);
        Location wallCheckLoc = t.getLocation().add(0, 0.5, 0);
        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        double[] dists = new double[max];
        double maxDist = Double.MIN_VALUE;
        int next = 0;

        List<LivingEntity> entities = Nearby.getLivingNearbyBox(t.getLocation(), radius);

        if (self)
        {
            list.add(caster);
        }

        // Grab nearby targets
        for (Entity entity : entities)
        {
            if (!(entity instanceof LivingEntity) || entity == caster)
                continue;

            LivingEntity target = (LivingEntity) entity;
            Location targetLoc = target.getLocation().add(0, 0.5, 0);
            if ((!throughWall && TargetHelper.isObstructed(wallCheckLoc, targetLoc))
                || (!both && ally != SkillAPI.getSettings().isAlly(caster, target)))
                continue;

            double dist = targetLoc.distanceSquared(wallCheckLoc);
            if (list.size() >= max)
            {
                if (dist > maxDist)
                    continue;

                double newMax = Double.MIN_VALUE;
                for (int i = 0; i < max; i++)
                {
                    if (dists[i] == maxDist)
                    {
                        list.set(i, target);
                        dists[i] = dist;
                    }
                    else newMax = Math.max(newMax, dists[i]);
                }
                maxDist = newMax;
            }
            else
            {
                maxDist = Math.max(maxDist, dist);
                dists[next++] = dist;
                list.add(target);
            }
        }

        return list;
    }
}
