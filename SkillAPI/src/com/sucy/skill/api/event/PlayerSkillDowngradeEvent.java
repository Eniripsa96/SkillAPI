package com.sucy.skill.api.event;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.skill.ClassSkill;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player upgrades a skill
 */
public class PlayerSkillDowngradeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private PlayerSkills player;
    private ClassSkill downgradedSkill;

    /**
     * Constructor
     *
     * @param playerData      data of the player unlocking the skill
     * @param downgradedSkill the skill that was unlocked
     */
    public PlayerSkillDowngradeEvent(PlayerSkills playerData, ClassSkill downgradedSkill) {
        this.player = playerData;
        this.downgradedSkill = downgradedSkill;
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
    public ClassSkill getDowngradedSkill() {
        return downgradedSkill;
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
