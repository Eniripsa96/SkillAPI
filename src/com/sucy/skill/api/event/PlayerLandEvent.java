package com.sucy.skill.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event for when a player lands on the ground after falling/jumping
 */
public class PlayerLandEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private Player player;

    /**
     * Constructor
     *
     * @param player the player who landed on the ground
     */
    public PlayerLandEvent(Player player)
    {
        this.player = player;
    }

    /**
     * @return player who landed on the ground
     */
    public Player getPlayer()
    {
        return player;
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
