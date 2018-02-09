/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.ToolCondition
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

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target to have a specified potion effect
 */
public class ToolCondition extends EffectComponent
{
    private static final String MATERIAL = "material";
    private static final String TOOL     = "tool";

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
        String material = settings.getString(MATERIAL, "").toUpperCase();
        String tool = "_" + settings.getString(TOOL, "").toUpperCase().replace("SHOVEL", "SPADE");
        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            if (target.getEquipment() == null || target.getEquipment().getItemInHand() == null)
            {
                continue;
            }
            String hand = target.getEquipment().getItemInHand().getType().name();
            if ((material.equals("ANY") || hand.contains(material)) && (tool.equals("_ANY") || hand.contains(tool)))
            {
                list.add(target);
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
