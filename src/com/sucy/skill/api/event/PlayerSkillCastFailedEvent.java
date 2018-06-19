package com.sucy.skill.api.event;

import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * SkillAPI © 2018
 * com.sucy.skill.api.event.PlayerSkillCastFailedEvent
 */
public class PlayerSkillCastFailedEvent extends Event {

    public enum Cause {
        CANCELED,
        CASTER_DEAD,
        EFFECT_FAILED,
        NO_MANA,
        NO_TARGET,
        NOT_UNLOCKED,
        ON_COOLDOWN
    }

    private static final HandlerList handlers = new HandlerList();
    private PlayerSkill skill;
    private Cause       cause;

    private PlayerSkillCastFailedEvent(final PlayerSkill skill, final Cause cause) {
        this.skill = skill;
        this.cause = cause;
    }

    public static boolean invoke(final PlayerSkill skill, final Cause cause) {
        Bukkit.getPluginManager().callEvent(new PlayerSkillCastFailedEvent(skill, cause));
        return false;
    }

    /**
     * @return player trying to cast the skill
     */
    public PlayerData getPlayerData() {
        return skill.getPlayerData();
    }

    /**
     * @return skill that was attempted to be cast
     */
    public PlayerSkill getSkill() {
        return skill;
    }

    /**
     * @return reason the skill cast failed
     */
    public Cause getCause() {
        return cause;
    }

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
