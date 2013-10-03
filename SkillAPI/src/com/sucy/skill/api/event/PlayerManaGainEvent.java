package com.sucy.skill.api.event;

import com.sucy.skill.skills.PlayerSkills;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player gains mana via regeneration
 */
public class PlayerManaGainEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private PlayerSkills player;
    private boolean cancelled;
    private int amount;

    /**
     * Constructor
     *
     * @param playerData player gaining experience
     * @param amount     amount of experience being gained
     */
    public PlayerManaGainEvent(PlayerSkills playerData, int amount) {
        this.player = playerData;
        this.amount = amount;
        cancelled = false;
    }

    /**
     * @return data of the player gaining mana experience
     */
    public PlayerSkills getPlayerData() {
        return player;
    }

    /**
     * @return amount of experience being gained
     */
    public int getMana() {
        return amount;
    }

    /**
     * Sets the amount of experience being gained
     *
     * @param amount new amount of experience
     */
    public void setMana(int amount) {
        this.amount = amount;
    }

    /**
     * @return whether or not the gain in experience is cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets whether or not the gain in experience is cancelled
     *
     * @param cancelled true/false
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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
