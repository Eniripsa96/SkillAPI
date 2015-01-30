package com.sucy.skill.api.util;

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
        damageBuffs.put(nextId, buff);
        tasks.put(nextId, new BuffTask(nextId).runTaskLater(plugin, ticks));
        nextId = (nextId + 1) % MAX_ID;
    }

    /**
     * Adds a defensive buff to the entity
     *
     * @param buff  buff to add
     * @param ticks ticks to apply the buff for
     */
    public void addDefenseBuff(Buff buff, int ticks)
    {
        damageBuffs.put(nextId, buff);
        tasks.put(nextId, new BuffTask(nextId).runTaskLater(plugin, ticks));
        nextId = (nextId + 1) % MAX_ID;
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
    public double modifyTakenDefense(double damage)
    {
        return modify(defenseBuffs.values(), damage);
    }

    private double modify(Collection<Buff> buffs, double value)
    {
        double multiplier = 1;
        double bonus = 0;
        for (Buff buff : buffs)
        {
            if (buff.isPercent())
            {
                multiplier *= buff.getValue();
            }
            else
            {
                bonus += buff.getValue();
            }
        }
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
