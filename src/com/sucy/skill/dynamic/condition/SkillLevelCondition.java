/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.SkillLevelCondition
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

import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * A condition for dynamic skills that requires the caster's skill level to be within a range
 */
public class SkillLevelCondition extends EffectComponent
{
    private static final String SKILL     = "skill";
    private static final String MIN_LEVEL = "min-level";
    private static final String MAX_LEVEL = "max-level";

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
        int min = settings.getInt(MIN_LEVEL, 1);
        int max = settings.getInt(MAX_LEVEL, 99);

        String skill = settings.getString(SKILL, "");
        final PlayerSkill triggeredSkill = getSkillData(caster);
        if (triggeredSkill == null) return false;
        PlayerSkill data = triggeredSkill.getPlayerData().getSkill(skill);
        if (data == null) data = triggeredSkill;

        return data.getLevel() >= min
            && data.getLevel() <= max
            && executeChildren(caster, level, targets);
    }
}
