package com.sucy.skill.api.event;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.skill.ClassSkill;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player initially unlocks a skill
 */
public class PlayerCastSkillEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private PlayerSkills player;
    private ClassSkill castSkill;
    private boolean cancelled = false;

    /**
     * Constructor
     *
     * @param playerData data of the player unlocking the skill
     * @param castSkill  the skill that was unlocked
     */
    public PlayerCastSkillEvent(PlayerSkills playerData, ClassSkill castSkill) {
        this.player = playerData;
        this.castSkill = castSkill;
    }

    /**
     * @return data of the player unlocking the skill
     */
    public PlayerSkills getPlayerData() {
        return player;
    }

    /**
     * @return skill that was unlocked
     */
    public ClassSkill getCastSkill() {
        return castSkill;
    }

    /**
     * @return whether or not the event was cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets whether or not the event is cancelled
     *
     * @param value whether or not the event is cancelled
     */
    @Override
    public void setCancelled(boolean value) {
        cancelled = value;
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
