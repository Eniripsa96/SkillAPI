package com.sucy.skill.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a flag expired on an entity
 */
public class FlagExpireEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private LivingEntity entity;
    private String       flag;

    /**
     * Constructor
     *
     * @param entity the entity the flag was on
     * @param flag   the flag that expired
     */
    public FlagExpireEvent(LivingEntity entity, String flag)
    {
        this.entity = entity;
        this.flag = flag;
    }

    /**
     * Retrieves the entity that the flag was on
     *
     * @return the entity that the flag was on
     */
    public LivingEntity getEntity()
    {
        return entity;
    }

    /**
     * Retrieves the expired flag
     *
     * @return the flag that expired
     */
    public String getFlag()
    {
        return flag;
    }

    /**
     * @return gets the handlers for the event
     */
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    /**
     * @return gets the handlers for the event
     */
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
