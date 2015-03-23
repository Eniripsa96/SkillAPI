package com.sucy.skill.api.event;

import com.sucy.skill.api.player.PlayerData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player refunds an attribute point
 */
public class PlayerRefundAttributeEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private PlayerData player;
    private String     attribute;
    private boolean cancelled = false;

    /**
     * Constructor
     *
     * @param playerData data of the player upgrading the skill
     * @param attribute  the name of the attribute that was raised
     */
    public PlayerRefundAttributeEvent(PlayerData playerData, String attribute)
    {
        this.player = playerData;
        this.attribute = attribute;
    }

    /**
     * @return data of the player refunding the attribute
     */
    public PlayerData getPlayerData()
    {
        return player;
    }

    /**
     * @return name of the refunded attribute
     */
    public String getAttribute()
    {
        return attribute;
    }

    /**
     * @return true if cancelled, false otherwise
     */
    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    /**
     * Sets whether or not the event is cancelled
     *
     * @param value true if cancelled, false otherwise
     */
    @Override
    public void setCancelled(boolean value)
    {
        cancelled = value;
    }

    /**
     * @return gets the handlers for the event
     */
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
