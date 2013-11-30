package com.sucy.skill.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Hashtable;

/**
 * Event for when a player hits an enemy
 */
public class PlayerSkillHealEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Hashtable<String, Object> flags = new Hashtable<String, Object>();
    private Player player;
    private Player healer;
    private String skill;
    private double amount;

    /**
     * Constructor - you shouldn't need to use this
     *
     * @param player player dealing damage
     * @param healer mob or player taking damage
     * @param skill  name of the skill that dealt damage
     * @param amount amount of healing done
     */
    public PlayerSkillHealEvent(Player player, Player healer, String skill, double amount) {
        this.player = player;
        this.healer = healer;
        this.skill = skill;
        this.amount = amount;
    }

    /**
     * @return player that dealt damage
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the entity that was damaged by the player
     */
    public Player getHealer() {
        return healer;
    }

    /**
     * @return name of the skill that dealt damage
     */
    public String getSkillName() {
        return skill;
    }

    /**
     * @return the amount of health restored
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount of health restored
     *
     * @param amount new amount of healing to do
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * @param flag checks if a flag has been set
     * @return     true if set
     */
    public boolean hasFlag(String flag) {
        return flags.containsKey(flag.toLowerCase());
    }

    /**
     * Gets the value set for a flag
     *
     * @param flag flag name
     * @return     flag value
     */
    public Object getFlag(String flag) {
        return flags.get(flag.toLowerCase());
    }

    /**
     * Sets a flag for the event
     *
     * @param flag  flag name
     * @param value flag value
     * @return      previous value or null if there wasn't one
     */
    public Object setFlag(String flag, Object value) {
        return flags.put(flag, value);
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
