/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.DelayMechanic
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

import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.api.util.StatusFlag;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Executes child components after a delay, applying "channeling" rules
 */
public class ChannelMechanic extends MechanicComponent
{
    private static final String SECONDS = "time";
    private static final String STILL   = "still";

    @Override
    public String getKey() {
        return "channel";
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
    public boolean execute(final LivingEntity caster, final int level, final List<LivingEntity> targets)
    {
        if (targets.size() == 0)
        {
            return false;
        }
        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        boolean still = settings.getBool(STILL);
        int ticks = (int) (20 * parseValues(caster, SECONDS, level, 2.0));
        if (still)
            FlagManager.addFlag(caster, StatusFlag.CHANNELING, ticks + 2);
        Bukkit.getScheduler().runTaskLater(
            Bukkit.getPluginManager().getPlugin("SkillAPI"), new Runnable()
            {
                @Override
                public void run()
                {
                    if (FlagManager.hasFlag(caster, StatusFlag.CHANNEL))
                    {
                        FlagManager.removeFlag(caster, StatusFlag.CHANNEL);
                        FlagManager.removeFlag(caster, StatusFlag.CHANNELING);
                        executeChildren(caster, level, targets);
                    }
                }
            }, ticks
        );
        FlagManager.addFlag(caster, StatusFlag.CHANNEL, ticks + 2);
        return true;
    }
}
