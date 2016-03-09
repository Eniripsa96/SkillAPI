/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.PotionCondition
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
package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target to have a specified potion effect
 */
public class PotionCondition extends EffectComponent
{
    private static final String TYPE   = "type";
    private static final String POTION = "potion";

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
        boolean active = !settings.getString(TYPE, "active").toLowerCase().equals("not active");
        String potion = settings.getString(POTION, "").toUpperCase().replace(' ', '_');
        PotionEffectType type;
        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        try
        {
            type = PotionEffectType.getByName(potion);
            for (LivingEntity target : targets)
            {
                if (target.hasPotionEffect(type) == active)
                {
                    list.add(target);
                }
            }
        }
        catch (Exception ex)
        {
            for (LivingEntity target : targets)
            {
                boolean has = false;
                for (PotionEffectType check : PotionEffectType.values())
                {
                    if (check != null && target.hasPotionEffect(check))
                    {
                        has = true;
                        break;
                    }
                }
                if (has == active)
                {
                    list.add(target);
                }
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
