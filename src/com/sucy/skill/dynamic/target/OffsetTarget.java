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

import com.sucy.skill.cast.CircleIndicator;
import com.sucy.skill.cast.IIndicator;
import com.sucy.skill.cast.IndicatorType;
import com.sucy.skill.cast.SphereIndicator;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies child effects to a location offset from the current targets
 */
public class OffsetTarget extends EffectComponent
{
    private static Vector up = new Vector(0, 1, 0);

    private static final String FORWARD    = "forward";
    private static final String UPWARD     = "upward";
    private static final String RIGHT      = "right";
    private static final String HORIZONTAL = "horizontal";

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
            list.add(getTargetLoc(caster, level, t));
        return executeChildren(caster, level, list);
    }

    private TempEntity getTargetLoc(LivingEntity caster, int level, LivingEntity t)
    {
        boolean isSelf = t == caster;
        boolean horizontal = settings.getBool(HORIZONTAL, false);
        double forward = attr(caster, FORWARD, level, 0, isSelf);
        double upward = attr(caster, UPWARD, level, 0, isSelf);
        double right = attr(caster, RIGHT, level, 0, isSelf);

        Vector dir = t.getLocation().getDirection().setY(0).normalize();
        if (horizontal)
            dir = dir.setY(0).normalize();
        Vector nor = dir.clone().crossProduct(up);
        dir.multiply(forward);
        dir.add(nor.multiply(right)).setY(upward);

        return new TempEntity(t.getLocation().add(dir));
    }
}
