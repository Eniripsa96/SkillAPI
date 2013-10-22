package com.sucy.skill.api.event;

import com.sucy.skill.api.PlayerSkills;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player leveled up
 */
public class PlayerLevelUpEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private PlayerSkills player;
    private int level;

    /**
     * Constructor
     *
     * @param playerData data of the player leveling up
     */
    public PlayerLevelUpEvent(PlayerSkills playerData) {
        this.player = playerData;
        this.level = playerData.getLevel();
    }

    /**
     * @return data of the player who leveled up
     */
    public PlayerSkills getPlayerData() {
        return player;
    }

    /**
     * @return new level of the player
     */
    public int getLevel() {
        return level;
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
