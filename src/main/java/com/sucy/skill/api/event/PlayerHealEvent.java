package com.sucy.skill.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player is healed through PlayerSkills
 */
public class PlayerHealEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private boolean cancelled;
    private double amount;

    /**
     * Constructor
     *
     * @param player player gaining experience
     * @param amount     amount of experience being gained
     */
    public PlayerHealEvent(Player player, double amount) {
        this.player = player;
        this.amount = amount;
        cancelled = false;
    }

    /**
     * @return data of the player gaining mana experience
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return amount of experience being gained
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount of experience being gained
     *
     * @param amount new amount of experience
     */
    public void setAMount(int amount) {
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
