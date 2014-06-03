package com.sucy.skill.api.event;

import com.sucy.skill.api.PlayerSkills;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player gains class experience
 */
public class PlayerExperienceGainEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private PlayerSkills player;
    private boolean cancelled;
    private boolean commandExp;
    private int amount;

    /**
     * Constructor
     *
     * @param playerData player gaining experience
     * @param amount     amount of experience being gained
     */
    public PlayerExperienceGainEvent(PlayerSkills playerData, int amount) {
        this(playerData, amount, false);
    }

    /**
     * Constructor
     *
     * @param playerData player gaining experience
     * @param amount     amount of experience being gained
     * @param commandExp whether or not the experience came from a command
     */
    public PlayerExperienceGainEvent(PlayerSkills playerData, int amount, boolean commandExp) {
        this.player = playerData;
        this.amount = amount;
        this.commandExp = commandExp;
        cancelled = false;
    }

    /**
     * @return data of the player gaining experience
     */
    public PlayerSkills getPlayerData() {
        return player;
    }

    /**
     * @return amount of experience being gained
     */
    public int getExp() {
        return amount;
    }

    /**
     * @return whether or not the exp came from a command
     */
    public boolean isCommandExp() {
        return commandExp;
    }

    /**
     * Sets the amount of experience being gained
     *
     * @param amount new amount of experience
     * @throws IllegalArgumentException if experience is less than 0
     */
    public void setExp(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Experience cannot be negative");

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
