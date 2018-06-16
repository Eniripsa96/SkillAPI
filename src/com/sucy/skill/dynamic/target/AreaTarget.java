/**
 * SkillAPI
 * com.sucy.skill.dynamic.target.AreaTarget
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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies child components to the closest all nearby entities around
 * each of the current targets.
 */
public class AreaTarget extends EffectComponent
{
    private static final String RADIUS = "radius";
    private static final String ALLY   = "group";
    private static final String MAX    = "max";
    private static final String WALL   = "wall";
    private static final String CASTER = "caster";
    private static final String BOX    = "box";

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
        for (LivingEntity target : targets)
        {
            List<LivingEntity> list = getTargets(caster, level, target);
            worked = (list.size() > 0 && executeChildren(caster, level, list)) || worked;
        }
        return worked;
    }

    private List<LivingEntity> getTargets(LivingEntity caster, int level, LivingEntity target)
    {
        boolean isSelf = target == caster;
        double radius = attr(caster, RADIUS, level, 3.0, isSelf);
        String group = settings.getString(ALLY, "enemy").toLowerCase();
        boolean both = group.equals("both");
        boolean ally = group.equals("ally");
        boolean throughWall = settings.getString(WALL, "false").equalsIgnoreCase("true");
        boolean self = settings.getString(CASTER, "false").equalsIgnoreCase("true");
        double max = attr(caster, MAX, level, 99, isSelf);
        Location wallCheckLoc = target.getEyeLocation();

        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();

        List<LivingEntity> entities = Nearby.getLivingNearby(target.getLocation(), radius);

        for (int i = 0; i < entities.size() && list.size() < max; i++)
        {
            LivingEntity t = entities.get(i);
            if (t == caster && !self)
                continue;
            if (!throughWall && TargetHelper.isObstructed(wallCheckLoc, t.getEyeLocation()))
                continue;

            if (both || ally == SkillAPI.getSettings().isAlly(caster, t))
            {
                list.add(t);
                if (list.size() >= max)
                    return list;
            }
        }

        return list;
    }
}
