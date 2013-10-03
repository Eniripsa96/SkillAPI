package com.sucy.skill.api.event;

import com.sucy.skill.api.CustomClass;
import com.sucy.skill.skills.PlayerSkills;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player changes classes
 */
public class PlayerClassChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private PlayerSkills player;
    private CustomClass previousClass;
    private CustomClass newClass;

    /**
     * Constructor
     *
     * @param playerData    data of the player changing classes
     * @param previousClass previous class of the player (null if wasn't a profession)
     * @param newClass     new class of the player (null if using the reset command)
     */
    public PlayerClassChangeEvent(PlayerSkills playerData, CustomClass previousClass, CustomClass newClass) {
        this.player = playerData;
        this.previousClass = previousClass;
        this.newClass = newClass;
    }

    /**
     * @return Data of the player changing classes
     */
    public PlayerSkills getPlayerData() {
        return player;
    }

    /**
     * @return previous class of the player
     */
    public CustomClass getPreviousClass() {
        return previousClass;
    }

    /**
     * @return new class of the player
     */
    public CustomClass getNewClass() {
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
