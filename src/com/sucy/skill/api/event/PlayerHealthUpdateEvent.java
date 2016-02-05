package com.sucy.skill.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.sucy.skill.api.player.PlayerData;

/**
 * Event called when a player got his max health updated
 */
public class PlayerHealthUpdateEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private PlayerData player;
    
    private double     health;

    /**
     * Constructor
     *
     * @param player class of the player being updated
     * @param amount new player max health
     */
    public PlayerHealthUpdateEvent(PlayerData player, double health)
    {
        this.player = player;
        this.health = health;
    }

    /**
     * @return data of the player being updated
     */
    public PlayerData getPlayerData()
    {
        return player;
    }

    /**
     * @return player max health
     */
    public double getHealth()
    {
        return health;
    }
    
    /**
     * Sets the player max health
     *
     * @param health player max health
     *
     * @throws IllegalArgumentException if max health is equal or less than 0
     */
    public void setHealth(double health)
    {
        if (health <= 0)
        {
            throw new IllegalArgumentException("Max health cannot be negative or equal to zero");
        }

        this.health = health;
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