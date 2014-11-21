package com.sucy.skill.api.event;

import com.sucy.skill.api.enums.Status;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player leveled up
 */
public class StatusExpireEvent extends Event
{

    private static final HandlerList handlers = new HandlerList();
    private LivingEntity entity;
    private Status       status;

    /**
     * Constructor
     *
     * @param entity the entity the status was on
     * @param status the status that expired
     */
    public StatusExpireEvent(LivingEntity entity, Status status)
    {
    }

    /**
     * @return the entity that the status was on
     */
    public LivingEntity getEntity()
    {
        return entity;
    }

    /**
     * @return the status that expired
     */
    public Status getStatus()
    {
        return status;
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
