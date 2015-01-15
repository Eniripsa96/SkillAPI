package com.sucy.skill.api.event;

import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player changes classes
 */
public class PlayerClassChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private PlayerClass playerClass;
    private RPGClass    previousClass;
    private RPGClass    newClass;

    /**
     * Constructor
     *
     * @param playerClass   data of the player changing classes
     * @param previousClass previous class of the player (null if wasn't a profession)
     * @param newClass      new class of the player (null if using the reset command)
     */
    public PlayerClassChangeEvent(PlayerClass playerClass, RPGClass previousClass, RPGClass newClass)
    {
        this.playerClass = playerClass;
        this.previousClass = previousClass;
        this.newClass = newClass;
    }

    /**
     * @return modified player class
     */
    public PlayerClass getPlayerClass()
    {
        return playerClass;
    }

    /**
     * @return Data of the player changing classes
     */
    public PlayerData getPlayerData() {
        return playerClass.getPlayerData();
    }

    /**
     * @return previous class of the player
     */
    public RPGClass getPreviousClass() {
        return previousClass;
    }

    /**
     * @return new class of the player
     */
    public RPGClass getNewClass() {
        return newClass;
    }

    /**
     * @return gets the handlers for the event
     */
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return gets the handlers for the event
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
