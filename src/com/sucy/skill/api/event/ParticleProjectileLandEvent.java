package com.sucy.skill.api.event;

import com.sucy.skill.api.projectile.ParticleProjectile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * <p>An event for when a particle projectile projectile hits terrain.</p>
 */
public class ParticleProjectileLandEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private final ParticleProjectile projectile;

    /**
     * <p>Initializes a new event.</p>
     *
     * @param projectile the projectile that hit something
     */
    public ParticleProjectileLandEvent(ParticleProjectile projectile)
    {
        this.projectile = projectile;
    }

    /**
     * <p>Retrieves the projectile</p>
     *
     * @return the projectile that hit something
     */
    public ParticleProjectile getProjectile()
    {
        return projectile;
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
