package com.sucy.skill.api.event;

import com.sucy.skill.api.projectile.ParticleProjectile;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * <p>An event for when a particle projectile hits something.</p>
 */
public class ParticleProjectileHitEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private final ParticleProjectile projectile;
    private final LivingEntity       target;

    /**
     * <p>Initializes a new event.</p>
     *
     * @param projectile the projectile that hit something
     * @param target     the thing that the projectile hit
     */
    public ParticleProjectileHitEvent(ParticleProjectile projectile, LivingEntity target)
    {
        this.projectile = projectile;
        this.target = target;
    }

    /**
     * <p>Retrieves the projectile that hit something.</p>
     *
     * @return the projectile that hit something
     */
    public ParticleProjectile getProjectile()
    {
        return projectile;
    }

    /**
     * <p>Retrieves the thing that was hit by the projectile.</p>
     *
     * @return the thing hit by the projectile
     */
    public LivingEntity getTarget()
    {
        return target;
    }

    /**
     * <p>Bukkit method for taking care of the event handlers.</p>
     *
     * @return list of event handlers
     */
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    /**
     * <p>Bukkit method for taking care of the event handlers.</p>
     *
     * @return list of event handlers
     */
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
