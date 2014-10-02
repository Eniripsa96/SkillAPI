package com.sucy.skill.api.event;

import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player leveled up
 */
public class PlayerLevelUpEvent extends Event
{

    private static final HandlerList handlers = new HandlerList();
    private PlayerClass playerClass;
    private int         level;
    private int         amount;

    /**
     * Constructor
     *
     * @param playerClass data of the player leveling up
     */
    public PlayerLevelUpEvent(PlayerClass playerClass, int amount)
    {
        this.playerClass = playerClass;
        this.level = playerClass.getLevel();
        this.amount = amount;
    }

    /**
     * @return data of the player whose class leveled up
     */
    public PlayerData getPlayerData()
    {
        return playerClass.getPlayerData();
    }

    /**
     * @return the player's class that is leveling up
     */
    public PlayerClass getPlayerClass()
    {
        return playerClass;
    }

    /**
     * @return new level of the player's class
     */
    public int getLevel()
    {
        return level;
    }

    /**
     * @return how many levels the player's class gained
     */
    public int getAmount()
    {
        return amount;
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
