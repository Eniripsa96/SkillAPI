package com.sucy.skill.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event for when an entity is healed by
 * another entity with the use of a skill.
 */
public class SkillHealEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    private LivingEntity healer;
    private LivingEntity target;
    private double       damage;
    private boolean      cancelled;

    /**
     * Initializes a new event
     *
     * @param healer entity dealing the damage
     * @param target entity receiving the damage
     * @param damage the amount of damage dealt
     */
    public SkillHealEvent(LivingEntity healer, LivingEntity target, double damage)
    {
        this.healer = healer;
        this.target = target;
        this.damage = damage;
        this.cancelled = false;
    }

    /**
     * Retrieves the entity that dealt the damage
     *
     * @return entity that dealt the damage
     */
    public LivingEntity getHealer()
    {
        return healer;
    }

    /**
     * Retrieves the entity that received the damage
     *
     * @return entity that received the damage
     */
    public LivingEntity getTarget()
    {
        return target;
    }

    /**
     * Retrieves the amount of damage dealt
     *
     * @return amount of damage dealt
     */
    public double getAmount()
    {
        return damage;
    }

    /**
     * Sets the amount of damage dealt
     *
     * @param amount amount of damage dealt
     */
    public void setAmount(double amount)
    {
        damage = amount;
    }

    /**
     * Checks whether or not the event is cancelled
     *
     * @return true if cancelled, false otherwise
     */
    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    /**
     * Sets the cancelled state of the event
     *
     * @param cancelled the cancelled state of the event
     */
    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
