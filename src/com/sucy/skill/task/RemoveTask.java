package com.sucy.skill.task;

import com.sucy.skill.SkillAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A simple task for removing an entity after a duration
 */
public class RemoveTask extends BukkitRunnable
{
    private LivingEntity entity;

    /**
     * Initializes a new task to remove the entity after the
     * given number of ticks.
     *
     * @param entity entity to remove
     * @param ticks  ticks to wait before removing the entity
     */
    public RemoveTask(LivingEntity entity, int ticks)
    {
        this.entity = entity;
        SkillAPI.schedule(this, ticks);
    }

    /**
     * Removes the entity once the time is up
     */
    @Override
    public void run()
    {
        if (entity.isValid() && !entity.isDead())
        {
            entity.remove();
        }
    }
}
