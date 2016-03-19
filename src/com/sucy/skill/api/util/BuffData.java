/**
 * SkillAPI
 * com.sucy.skill.api.util.BuffData
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
package com.sucy.skill.api.util;

import com.sucy.skill.log.LogType;
import com.sucy.skill.log.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashMap;

/**
 * Represents buffs set on an entity
 */
public class BuffData
{
    private static final int MAX_ID = 9999999;

    private final HashMap<Integer, Buff>       damageBuffs  = new HashMap<Integer, Buff>();
    private final HashMap<Integer, Buff>       defenseBuffs = new HashMap<Integer, Buff>();
    private final HashMap<Integer, BukkitTask> tasks        = new HashMap<Integer, BukkitTask>();

    private LivingEntity entity;
    private Plugin       plugin;
    private int nextId = 0;

    /**
     * Initializes new buff data for the entity
     *
     * @param entity entity to initialize for
     */
    public BuffData(LivingEntity entity)
    {
        this.plugin = Bukkit.getPluginManager().getPlugin("SkillAPI");
        this.entity = entity;
    }

    /**
     * Adds an offensive buff to the entity
     *
     * @param buff  buff to add
     * @param ticks ticks to apply the buff for
     */
    public void addDamageBuff(Buff buff, int ticks)
    {
        int id = check(buff, damageBuffs);
        if (id == -1)
        {
            damageBuffs.put(nextId, buff);
            tasks.put(nextId, new BuffTask(nextId).runTaskLater(plugin, ticks));
            nextId = (nextId + 1) % MAX_ID;
        }
        else
        {
            tasks.remove(id).cancel();
            tasks.put(id, new BuffTask(id).runTaskLater(plugin, ticks));
        }
    }

    /**
     * Adds a defensive buff to the entity
     *
     * @param buff  buff to add
     * @param ticks ticks to apply the buff for
     */
    public void addDefenseBuff(Buff buff, int ticks)
    {
        int id = check(buff, defenseBuffs);
        if (id == -1)
        {
            defenseBuffs.put(nextId, buff);
            tasks.put(nextId, new BuffTask(nextId).runTaskLater(plugin, ticks));
            nextId = (nextId + 1) % MAX_ID;
        }
        else
        {
            tasks.remove(id).cancel();
            tasks.put(id, new BuffTask(id).runTaskLater(plugin, ticks));
        }
    }

    /**
     * Checks for buffs with overlapping keys
     *
     * @param buff new buff to check against
     * @param map  map to look through
     *
     * @return ID of overlapping buff or -1 if no conflict
     */
    private int check(Buff buff, HashMap<Integer, Buff> map)
    {
        for (Buff active : map.values())
        {
            if (active.getKey().equals(buff.getKey()))
            {
                return active.getId();
            }
        }
        return -1;
    }

    /**
     * Modifies the amount of dealt damage using damage buff
     * multipliers and bonuses.
     *
     * @param damage base damage amount to modify
     *
     * @return modified damage amount
     */
    public double modifyDealtDamage(double damage)
    {
        return modify(damageBuffs.values(), damage);
    }

    /**
     * Modifies the amount of taken damage using defense buff
     * multipliers and bonuses.
     *
     * @param damage base damage amount to modify
     *
     * @return modified damage amount
     */
    public double modifyTakenDamage(double damage)
    {
        return modify(defenseBuffs.values(), damage);
    }

    private double modify(Collection<Buff> buffs, double value)
    {
        if (value <= 0)
        {
            return 0;
        }
        double multiplier = 1;
        double bonus = 0;
        Logger.log(LogType.BUFF, 1, "Buffs:");
        for (Buff buff : buffs)
        {
            if (buff.isPercent())
            {
                Logger.log(LogType.BUFF, 1, "  - x" + buff.getValue());
                multiplier *= buff.getValue();
            }
            else
            {
                Logger.log(LogType.BUFF, 1, "  - +" + buff.getValue());
                bonus += buff.getValue();
            }
        }
        Logger.log(LogType.BUFF, 1, "Result: x" + multiplier + ", +" + bonus + ", " + value + " -> " + Math.max(0, value * multiplier + bonus));
        if (multiplier <= 0)
        {
            return 0;
        }
        else
        {
            return Math.max(0, value * multiplier + bonus);
        }
    }

    /**
     * Clears all buffs on the entity and stops associated tasks.
     */
    public void clear()
    {
        damageBuffs.clear();
        defenseBuffs.clear();
        for (BukkitTask task : tasks.values())
        {
            task.cancel();
        }
        tasks.clear();
        BuffManager.clearData(entity);
    }

    private class BuffTask extends BukkitRunnable
    {
        private int id;

        public BuffTask(int id)
        {
            this.id = id;
        }

        @Override
        public void run()
        {
            if (!entity.isValid() || entity.isDead())
            {
                BuffManager.clearData(entity);
                return;
            }
            if (damageBuffs.containsKey(id))
            {
                damageBuffs.remove(id);
            }
            else
            {
                defenseBuffs.remove(id);
            }
            tasks.remove(id);
            if (damageBuffs.size() + defenseBuffs.size() == 0)
            {
                BuffManager.clearData(entity);
            }
        }
    }
}
