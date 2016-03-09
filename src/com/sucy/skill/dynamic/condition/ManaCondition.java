/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.ManaCondition
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

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target's mana to fit the requirement
 */
public class ManaCondition extends EffectComponent
{
    private static final String TYPE = "type";
    private static final String MIN  = "min-value";
    private static final String MAX  = "max-value";

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
        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        String type = settings.getString(TYPE).toLowerCase();
        double min = attr(caster, MIN, level, 0, isSelf);
        double max = attr(caster, MAX, level, 99, isSelf);

        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            if (!(target instanceof Player))
            {
                continue;
            }
            double value;
            PlayerData data = SkillAPI.getPlayerData((Player) target);
            PlayerSkill skill = getSkillData(caster);
            double mana = data.getMana();
            if (type.equals("difference percent"))
            {
                value = (mana - skill.getPlayerData().getMana()) * 100 / skill.getPlayerData().getMana();
            }
            else if (type.equals("difference"))
            {
                value = mana - skill.getPlayerData().getMana();
            }
            else if (type.equals("percent"))
            {
                value = mana * 100 / data.getMaxMana();
            }
            else // type.equals("health")
            {
                value = mana;
            }
            if (value >= min && value <= max)
            {
                list.add(target);
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
