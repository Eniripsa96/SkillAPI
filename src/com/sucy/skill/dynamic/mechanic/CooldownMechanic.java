/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.CooldownMechanic
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
package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Lowers the cooldowns of the caster's skills
 */
public class CooldownMechanic extends MechanicComponent
{
    private static final String SKILL = "skill";
    private static final String TYPE  = "type";
    private static final String VALUE = "value";

    @Override
    public String getKey() {
        return "cooldown";
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
        if (!(caster instanceof Player)) return false;

        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        String skill = settings.getString(SKILL, "");
        String type = settings.getString(TYPE, "all").toLowerCase();
        double value = parseValues(caster, VALUE, level, 0);

        PlayerData playerData = SkillAPI.getPlayerData((Player) caster);

        PlayerSkill skillData = playerData.getSkill(skill);
        if (skillData == null && !skill.equals("all"))
        {
            skillData = playerData.getSkill(this.skill.getName());
        }

        boolean worked = false;
        if (skill.equals("all"))
        {
            for (PlayerSkill data : playerData.getSkills())
            {
                if (type.equals("percent"))
                {
                    data.subtractCooldown(value * data.getCooldown() / 100);
                }
                else
                {
                    data.subtractCooldown(value);
                }
                worked = true;
            }
        }
        else if (skillData != null)
        {
            if (type.equals("percent"))
            {
                skillData.subtractCooldown(value * skillData.getCooldown() / 100);
            }
            else
            {
                skillData.subtractCooldown(value);
            }
            worked = true;
        }
        return worked;
    }
}
