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

import com.sucy.skill.cast.CircleIndicator;
import com.sucy.skill.cast.IIndicator;
import com.sucy.skill.cast.IndicatorType;
import com.sucy.skill.cast.SphereIndicator;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Applies child components to a location using the caster's faced direction
 */
public class LocationTarget extends EffectComponent
{
    private static final String RANGE  = "range";
    private static final String GROUND = "ground";

    private static final HashSet<Byte> NULL_SET = null;

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
        return executeChildren(caster, level, list);
    }

    private TempEntity getTargetLoc(LivingEntity caster, int level, LivingEntity t)
    {
        boolean isSelf = caster == t;
        double range = attr(caster, RANGE, level, 5.0, isSelf);
        boolean groundOnly = !settings.getString(GROUND, "true").toLowerCase().equals("false");

        Location loc;
        Block b = t.getTargetBlock(NULL_SET, (int) Math.ceil(range));
        if (b == null && !groundOnly)
        {
            loc = t.getLocation().add(t.getLocation().getDirection().multiply(range));
        }
        else if (b != null)
        {
            loc = b.getLocation();
        }
        else
        {
            return null;
        }
        return new TempEntity(loc);
    }
}
