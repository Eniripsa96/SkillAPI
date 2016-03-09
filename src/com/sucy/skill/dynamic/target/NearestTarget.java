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
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

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
        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        double radius = attr(caster, RADIUS, level, 3.0, isSelf);
        boolean both = settings.getString(ALLY, "enemy").toLowerCase().equals("both");
        boolean ally = settings.getString(ALLY, "enemy").toLowerCase().equals("ally");
        boolean throughWall = settings.getString(WALL, "false").toLowerCase().equals("true");
        boolean self = settings.getString(CASTER, "false").toLowerCase().equals("true");
        double max = attr(caster, MAX, level, 99, isSelf);
        Location wallCheckLoc = caster.getLocation().add(0, 0.5, 0);
        for (LivingEntity t : targets)
        {
            ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
            List<Entity> entities = t.getNearbyEntities(radius, radius, radius);
            if (self)
            {
                list.add(caster);
            }

            // Grab nearby targets
            for (Entity entity : entities)
            {
                if (entity instanceof LivingEntity)
                {
                    LivingEntity target = (LivingEntity) entity;
                    if (!throughWall && TargetHelper.isObstructed(wallCheckLoc, target.getLocation().add(0, 0.5, 0)))
                    {
                        continue;
                    }
                    if (both || ally == SkillAPI.getSettings().isAlly(caster, target))
                    {
                        list.add(target);
                        if (list.size() >= max)
                        {
                            break;
                        }
                    }
                }
            }

            // Take only the closest
            // TODO optimize getting the nearest ones
            Location casterLoc = caster.getLocation();
            while (targets.size() > max && targets.size() > 0)
            {
                double dSq = -1;
                int index = 0;
                for (int i = 0; i < targets.size(); i++)
                {
                    double d = targets.get(i).getLocation().distanceSquared(casterLoc);
                    if (d > dSq)
                    {
                        dSq = d;
                        index = i;
                    }
                }
                targets.remove(index);
            }

            worked = targets.size() > 0 && executeChildren(caster, level, list) || worked;
        }
        return worked;
    }
}
