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
import com.sucy.skill.SkillAPI;
import com.sucy.skill.cast.*;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Applies child components to the closest all nearby entities around
 * each of the current targets.
 */
public class ConeTarget extends EffectComponent
{
    private static final String ANGLE  = "angle";
    private static final String RANGE  = "range";
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
        double range = attr(caster, RANGE, level, 3.0, target == caster);
        double angle = attr(caster, ANGLE, level, 90.0, target == caster);
        if (indicatorType != IndicatorType.NONE)
        {
            Location loc = target.getLocation();
            ConeIndicator indicator = new ConeIndicator(angle, range);
            indicator.moveTo(loc.getX(), loc.getY() + 0.1, loc.getZ());
            indicator.setDirection(loc.getYaw());
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
        double range = attr(caster, RANGE, level, 3.0, isSelf);
        double angle = attr(caster, ANGLE, level, 90.0, isSelf);
        boolean both = settings.getString(ALLY, "enemy").toLowerCase().equals("both");
        boolean ally = settings.getString(ALLY, "enemy").toLowerCase().equals("ally");
        boolean throughWall = settings.getString(WALL, "false").toLowerCase().equals("true");
        boolean self = settings.getString(CASTER, "false").toLowerCase().equals("true");
        int max = settings.getInt(MAX, 99);
        Location wallCheckLoc = t.getLocation().add(0, 0.5, 0);

        List<LivingEntity> list = TargetHelper.getConeTargets(t, angle, range);
        if (self)
        {
            list.add(caster);
        }
        for (int i = list.size() - 1; i >= 0; i--)
        {
            LivingEntity target = list.get(i);
            if (i >= max
                || (!throughWall && TargetHelper.isObstructed(wallCheckLoc, target.getLocation().add(0, 0.5, 0)))
                || (!both && ally != SkillAPI.getSettings().isAlly(caster, target)))
            {
                list.remove(i);
            }
        }

        return list;
    }
}
