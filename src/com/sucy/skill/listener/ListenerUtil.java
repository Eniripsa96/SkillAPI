package com.sucy.skill.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Helper class for listeners
 */
public class ListenerUtil
{
    /**
     * Retrieves a damager from an entity damage event which will get the
     * shooter of projectiles if it was a projectile hitting them or
     * converts the Entity damager to a LivingEntity if applicable.
     *
     * @param event event to grab the damager from
     *
     * @return LivingEntity damager of the event or null if not found
     */
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

    /**
     * Gets a simple name of the entity
     *
     * @param entity entity to get the name of
     *
     * @return simple name of the entity
     */
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
