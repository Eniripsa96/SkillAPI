/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.CommandMechanic
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

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Executes a command for each target
 */
public class CommandMechanic extends MechanicComponent
{
    private static final String COMMAND = "command";
    private static final String TYPE    = "type";

    @Override
    public String getKey() {
        return "command";
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
        if (targets.size() == 0 || !settings.has(COMMAND))
        {
            return false;
        }

        String command = settings.getString(COMMAND);
        String type = settings.getString(TYPE).toLowerCase();
        boolean worked = false;
        for (LivingEntity t : targets)
        {
            if (t instanceof Player)
            {
                Player p = (Player) t;
                worked = true;

                command = filter(caster, p, command);
                if (type.equals("op"))
                {
                    boolean op = p.isOp();
                    p.setOp(true);
                    Bukkit.getServer().dispatchCommand(p, command);
                    p.setOp(op);
                }
                else
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }

        return worked;

    }
}
