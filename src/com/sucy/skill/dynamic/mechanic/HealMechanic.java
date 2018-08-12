/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.HealMechanic
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

import com.rit.sucy.version.VersionManager;
import com.sucy.skill.api.event.SkillHealEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Heals each target
 */
public class HealMechanic extends MechanicComponent
{
    private static final String TYPE  = "type";
    private static final String VALUE = "value";

    @Override
    public String getKey() {
        return "heal";
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
        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        boolean percent = settings.getString(TYPE, "health").toLowerCase().equals("percent");
        double value = parseValues(caster, VALUE, level, 1.0);
        if (value < 0) return false;
        for (LivingEntity target : targets)
        {
            if (target.isDead())
                continue;

            double amount = value;
            if (percent)
            {
                amount = target.getMaxHealth() * value / 100;
            }

            SkillHealEvent event = new SkillHealEvent(caster, target, amount);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled())
            {
                VersionManager.heal(target, event.getAmount());
            }
        }
        return targets.size() > 0;
    }
}
