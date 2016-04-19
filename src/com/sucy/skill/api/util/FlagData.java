/**
 * SkillAPI
 * com.sucy.skill.api.util.FlagData
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

import com.sucy.skill.api.event.FlagApplyEvent;
import com.sucy.skill.api.event.FlagExpireEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
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
        FlagApplyEvent event = new FlagApplyEvent(entity, flag, ticks);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        // Permanent flag
        if (ticks < 0)
        {
            BukkitTask task = tasks.remove(flag);
            if (task != null)
                task.cancel();
            flags.put(flag, Long.MAX_VALUE);
            return;
        }

        if (flags.containsKey(flag))
        {
            long time = flags.get(flag);
            if (time > ticks * 50 + System.currentTimeMillis())
                return;
            else
            {
                BukkitTask task = tasks.remove(flag);
                if (task != null)
                    task.cancel();
            }
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
        removeFlag(flag, FlagExpireEvent.ExpireReason.REMOVED);
    }

    /**
     * Removes a flag from the entity, using the given reason
     *
     * @param flag   flag to remove
     * @param reason reason for removal
     */
    private void removeFlag(String flag, FlagExpireEvent.ExpireReason reason)
    {
        if (flags.containsKey(flag))
        {
            flags.remove(flag);
            BukkitTask task = tasks.remove(flag);
            if (task != null)
                task.cancel();
            Bukkit.getPluginManager().callEvent(new FlagExpireEvent(entity, flag, reason));
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
        ArrayList<String> flags = new ArrayList<String>(this.flags.keySet());
        for (String flag : flags)
        {
            removeFlag(flag);
        }
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
            removeFlag(flag, FlagExpireEvent.ExpireReason.TIME);
        }
    }
}
