package com.sucy.skill.api.event;

import com.sucy.skill.api.projectile.ItemProjectile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * <p>An event for when an item projectile lands on the ground</p>
 */
public class ItemProjectileLandEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private final ItemProjectile projectile;

    /**
     * <p>Initializes a new event.</p>
     *
     * @param projectile the projectile that hit something
     */
    public ItemProjectileLandEvent(ItemProjectile projectile)
    {
        this.projectile = projectile;
    }

    /**
     * <p>Retrieves the projectile</p>
     *
     * @return the projectile that hit something
     */
    public ItemProjectile getProjectile()
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
