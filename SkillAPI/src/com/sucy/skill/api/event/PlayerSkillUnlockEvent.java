package com.sucy.skill.api.event;

import com.sucy.skill.skills.PlayerSkills;
import com.sucy.skill.skills.Skill;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player initially unlocks a skill
 */
public class PlayerSkillUnlockEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private PlayerSkills player;
    private Skill unlockedSkill;

    /**
     * Constructor
     *
     * @param playerData    data of the player unlocking the skill
     * @param unlockedSkill the skill that was unlocked
     */
    public PlayerSkillUnlockEvent(PlayerSkills playerData, Skill unlockedSkill) {
        this.player = player;
        this.unlockedSkill = unlockedSkill;
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
    public Skill getUnlockedSkill() {
        return unlockedSkill;
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
