package com.sucy.skill.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ListenerUtil
{
    public static LivingEntity getDamager(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof LivingEntity)
        {
            return (LivingEntity) event.getDamager();
        }
        else if (event.getDamager() instanceof Projectile)
        {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof LivingEntity)
            {
                return (LivingEntity) projectile.getShooter();
            }
        }
        return null;
    }

    public static String getName(Entity entity)
    {
        String name = entity.getClass().getSimpleName().toLowerCase().replace("craft", "");
        if (entity instanceof Skeleton)
        {
            if (((Skeleton) entity).getSkeletonType() == Skeleton.SkeletonType.WITHER)
            {
                name = "wither" + name;
            }
        }
        return name;
    }
}
