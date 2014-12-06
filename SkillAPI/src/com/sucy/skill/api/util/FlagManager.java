package com.sucy.skill.api.util;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.UUID;

/**
 * The manager for temporary entity flag data
 */
public class FlagManager
{
    private static final HashMap<UUID, FlagData> data = new HashMap<UUID, FlagData>();

    /**
     * Retrieves the flag data for an entity. This returns null if
     * no existing data is found.
     *
     * @param entity entity to retrieve the data for
     *
     * @return the flag data for the entity
     */
    public static FlagData getFlagData(LivingEntity entity)
    {
        return getFlagData(entity, false);
    }

    /**
     * Retrieves the flag data for an entity, optionally creating new data
     * if none currently exists. If set to false, this will return null
     * if no data currently exists.
     *
     * @param entity entity to get the flag data for
     * @param create whether or not to create new data if it doesn't exist
     *
     * @return the flag data for an enemy
     */
    public static FlagData getFlagData(LivingEntity entity, boolean create)
    {
        if (entity == null)
        {
            return null;
        }
        if (!data.containsKey(entity.getUniqueId()))
        {
            data.put(entity.getUniqueId(), new FlagData(entity));
        }
        return data.get(entity.getUniqueId());
    }

    /**
     * Adds a flag to an entity
     *
     * @param entity entity to add the flag to
     * @param flag   the flag to add
     * @param ticks  the duration to add the flag for
     */
    public static void addFlag(LivingEntity entity, String flag, int ticks)
    {
        FlagData data = getFlagData(entity, true);
        if (data != null)
        {
            data.addFlag(flag, ticks);
        }
    }

    /**
     * Removes the flag from an entity
     *
     * @param entity entity to remove the flag from
     * @param flag   flag to remove
     */
    public static void removeFlag(LivingEntity entity, String flag)
    {
        FlagData data = getFlagData(entity, false);
        if (data != null)
        {
            data.removeFlag(flag);
        }
    }

    /**
     * Checks whether or not the entity has the given flag
     *
     * @param entity the entity to check for
     * @param flag   the flag to check for
     *
     * @return true if the flag is active on the entity, false otherwise
     */
    public static boolean hasFlag(LivingEntity entity, String flag)
    {
        return entity != null && data.containsKey(entity.getUniqueId()) && getFlagData(entity, false).hasFlag(flag);
    }

    /**
     * Retrieves the time left on a flag for an entity
     *
     * @param entity entity to get the time for
     * @param flag   flag to get the time for
     *
     * @return time left on the flag in seconds for the entity
     */
    public static int getTimeLeft(LivingEntity entity, String flag)
    {
        if (entity == null)
        {
            return 0;
        }
        return data.containsKey(entity.getUniqueId()) ? getFlagData(entity).getSecondsLeft(flag) : 0;
    }

    /**
     * Clears the flags for an entity
     *
     * @param entity entity to clear the flags for
     */
    public static void clearFlags(LivingEntity entity)
    {
        if (entity == null)
        {
            return;
        }
        FlagData result = data.remove(entity.getUniqueId());
        if (result != null)
        {
            result.clear();
        }
    }
}
