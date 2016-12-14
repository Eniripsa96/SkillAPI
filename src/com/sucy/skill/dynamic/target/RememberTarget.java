/**
 * SkillAPI
 * com.sucy.skill.dynamic.target.RememberTarget
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
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Applies a flag to each target
 */
public class RememberTarget extends EffectComponent
{
    private static final String KEY = "key";

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
        List<LivingEntity> targets = getTargets(caster);
        if (targets == null)
            return;

        for (LivingEntity t : targets)
        {
            if (indicatorType == IndicatorType.DIM_3)
            {
                Location loc = t.getLocation();
                IIndicator indicator = new SphereIndicator(0.5);
                indicator.moveTo(loc.getX(), loc.getY() + t.getEyeHeight() / 2, loc.getZ());
                list.add(indicator);
            }
            else if (indicatorType == IndicatorType.DIM_2)
            {
                Location loc = t.getLocation();
                IIndicator indicator = new CircleIndicator(0.5);
                indicator.moveTo(loc.getX(), loc.getY() + t.getEyeHeight() / 2, loc.getZ());
                list.add(indicator);
            }

            for (EffectComponent component : children)
                component.makeIndicators(list, caster, t, level);
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
        targets = getTargets(caster);
        return targets != null && executeChildren(caster, level, targets);
    }

    @SuppressWarnings("unchecked")
    private List<LivingEntity> getTargets(LivingEntity caster)
    {
        String key = settings.getString(KEY);
        try
        {
            Object data = DynamicSkill.getCastData(caster).get(key);
            List<LivingEntity> remembered = (List<LivingEntity>) data;
            for (int i = 0; i < remembered.size(); i++)
            {
                if (remembered.get(i).isDead() || !remembered.get(i).isValid())
                {
                    remembered.remove(i);
                    i--;
                }
            }
            return remembered;
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}
