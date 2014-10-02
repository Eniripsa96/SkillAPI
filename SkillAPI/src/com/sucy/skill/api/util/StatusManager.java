package com.sucy.skill.api.util;

import com.sucy.skill.api.enums.Status;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public class StatusManager
{

    private static final HashMap<LivingEntity, StatusData> data = new HashMap<LivingEntity, StatusData>();

    public static void addStatus(LivingEntity entity, Status status, int ticks)
    {
        if (entity == null)
        {
            return;
        }
        if (!data.containsKey(entity))
        {
            data.put(entity, new StatusData(entity));
        }
        getStatusData(entity).addStatus(status, ticks);
    }

    public static void removeStatus(LivingEntity entity, Status status)
    {
        if (entity == null)
        {
            return;
        }
        if (!data.containsKey(entity))
        {
            return;
        }
        getStatusData(entity).removeStatus(status);
    }

    public static StatusData getStatusData(LivingEntity entity)
    {
        if (entity == null)
        {
            return null;
        }
        return data.get(entity);
    }

    public static boolean hasStatus(LivingEntity entity, Status status)
    {
        if (entity == null)
        {
            return false;
        }
        return data.containsKey(entity) && getStatusData(entity).hasStatus(status);
    }

    public static int getTimeLeft(LivingEntity entity, Status status)
    {
        if (entity == null)
        {
            return 0;
        }
        return data.containsKey(entity) ? getStatusData(entity).getSecondsLeft(status) : 0;
    }

    public static void clearStatuses(LivingEntity entity)
    {
        if (entity == null)
        {
            return;
        }
        StatusData result = data.remove(entity);
        if (result != null)
        {
            result.clear();
        }
    }
}
