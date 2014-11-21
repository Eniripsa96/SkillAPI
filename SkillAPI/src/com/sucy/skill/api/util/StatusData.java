package com.sucy.skill.api.util;

import com.sucy.skill.api.enums.Status;
import com.sucy.skill.api.event.StatusExpireEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class StatusData
{

    private final HashMap<Status, Long>       statuses = new HashMap<Status, Long>();
    private final HashMap<Status, BukkitTask> tasks    = new HashMap<Status, BukkitTask>();
    private LivingEntity entity;
    private Plugin       plugin;

    public StatusData(LivingEntity entity)
    {
        this.plugin = Bukkit.getPluginManager().getPlugin("SkillAPI");
        this.entity = entity;
    }

    public void addStatus(Status status, int ticks)
    {
        if (statuses.containsKey(status))
        {
            long time = statuses.get(status) - System.currentTimeMillis();
            if (time / 50 > ticks)
            {
                return;
            }
            else
            {
                tasks.remove(status).cancel();
            }
        }
        statuses.put(status, System.currentTimeMillis() + ticks * 50);
        tasks.put(status, new StatusTask(status).runTaskLater(plugin, ticks));
        if (status == Status.STUN || status == Status.ROOT)
        {
            if (!(entity instanceof Player))
            {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, ticks, 100), true);
            }
        }
    }

    public void removeStatus(Status status)
    {
        statuses.remove(status);
    }

    public void clear()
    {
        statuses.clear();
        for (BukkitTask task : tasks.values())
        {
            task.cancel();
        }
        tasks.clear();
    }

    public int getSecondsLeft(Status status)
    {
        if (!hasStatus(status))
        {
            return 0;
        }
        long millis = statuses.get(status) - System.currentTimeMillis();
        return (int) Math.max(1, (millis + 999) / 1000);
    }

    public boolean hasStatus(Status status)
    {
        return statuses.containsKey(status);
    }

    private class StatusTask extends BukkitRunnable
    {
        private Status status;

        public StatusTask(Status status)
        {
            this.status = status;
        }

        @Override
        public void run()
        {
            if (!entity.isValid() || entity.isDead())
            {
                StatusManager.clearStatuses(entity);
                return;
            }
            statuses.remove(status);
            StatusExpireEvent event = new StatusExpireEvent(entity, status);
            Bukkit.getPluginManager().callEvent(event);
            if (statuses.size() == 0)
            {
                StatusManager.clearStatuses(entity);
            }
        }
    }
}
