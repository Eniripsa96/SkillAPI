package com.sucy.skill.api.util;

import com.sucy.skill.api.event.FlagExpireEvent;
import com.sucy.skill.hook.PluginChecker;
import com.sucy.skill.hook.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

/**
 * Represents flags set on an entity
 */
public class FlagData
{
    private final HashMap<String, Long>       flags = new HashMap<String, Long>();
    private final HashMap<String, BukkitTask> tasks = new HashMap<String, BukkitTask>();
    private LivingEntity entity;
    private Plugin       plugin;

    /**
     * Initializes new flag data for the entity
     *
     * @param entity entity to initialize for
     */
    public FlagData(LivingEntity entity)
    {
        this.plugin = Bukkit.getPluginManager().getPlugin("SkillAPI");
        this.entity = entity;
    }

    /**
     * Adds a flag to the entity for the given number of ticks
     *
     * @param flag  flag to set
     * @param ticks number of ticks to set the flag for
     */
    public void addFlag(String flag, int ticks)
    {
        if (flags.containsKey(flag))
        {
            long time = flags.get(flag) - System.currentTimeMillis();
            if (time / 50 > ticks)
            {
                return;
            }
            else
            {
                tasks.remove(flag).cancel();
            }
        }
        else if (flag.startsWith("perm:") && PluginChecker.isVaultActive() && entity instanceof Player)
        {
            VaultHook.add((Player)entity, flag.substring(5));
        }
        flags.put(flag, System.currentTimeMillis() + ticks * 50);
        tasks.put(flag, new FlagTask(flag).runTaskLater(plugin, ticks));
    }

    /**
     * Removes a flag from the entity
     *
     * @param flag flag to remove from the entity
     */
    public void removeFlag(String flag)
    {
        if (flags.containsKey(flag))
        {
            flags.remove(flag);
            tasks.remove(flag).cancel();
            FlagExpireEvent event = new FlagExpireEvent(entity, flag);
            Bukkit.getPluginManager().callEvent(event);
            if (flag.startsWith("perm:") && PluginChecker.isVaultActive() && entity instanceof Player)
            {
                VaultHook.remove((Player)entity, flag.substring(5));
            }
            if (flags.size() == 0)
            {
                FlagManager.clearFlags(entity);
            }
        }
    }

    /**
     * Clears all flags on the entity and stops associated tasks.
     */
    public void clear()
    {
        for (String flag : flags.keySet())
        {
            removeFlag(flag);
        }
        for (BukkitTask task : tasks.values())
        {
            task.cancel();
        }
        tasks.clear();
        FlagManager.clearFlags(entity);
    }

    /**
     * Retrieves the number of seconds left of an active flag on the entity.
     * If the flag is not active, this will instead return 0.
     *
     * @param flag flag to check the time left for
     *
     * @return the seconds left rounded up to the nearest second or 0 if not set
     */
    public int getSecondsLeft(String flag)
    {
        if (!hasFlag(flag))
        {
            return 0;
        }
        long millis = flags.get(flag) - System.currentTimeMillis();
        return (int) Math.max(1, (millis + 999) / 1000);
    }

    /**
     * Retrieves the number of milliseconds left of an active flag on the entity.
     * If the flag is not active, this will instead return 0.
     *
     * @param flag flag to check the time left for
     *
     * @return the number of milliseconds left or 0 if not set
     */
    public int getMillisLeft(String flag)
    {
        if (!hasFlag(flag))
        {
            return 0;
        }
        return (int) (flags.get(flag) - System.currentTimeMillis());
    }

    /**
     * Checks whether or not the entity currently has the flag set
     *
     * @param flag the flag to check if set or not
     *
     * @return true if set, false otherwise
     */
    public boolean hasFlag(String flag)
    {
        return flags.containsKey(flag);
    }

    private class FlagTask extends BukkitRunnable
    {
        private String flag;

        public FlagTask(String flag)
        {
            this.flag = flag;
        }

        @Override
        public void run()
        {
            if (!entity.isValid() || entity.isDead())
            {
                FlagManager.clearFlags(entity);
                return;
            }
            removeFlag(flag);
        }
    }
}
