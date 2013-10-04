package com.sucy.skill.api.event;

import com.sucy.skill.skills.PlayerSkills;
import com.sucy.skill.skills.Skill;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player upgrades a skill
 */
public class PlayerSkillUpgradeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private PlayerSkills player;
    private Skill upgradedSkill;

    /**
     * Constructor
     *
     * @param playerData    data of the player unlocking the skill
     * @param unlockedSkill the skill that was unlocked
     */
    public PlayerSkillUpgradeEvent(PlayerSkills playerData, Skill unlockedSkill) {
        this.player = playerData;
        this.upgradedSkill = unlockedSkill;
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
    public Skill getUpgradedSkill() {
        return upgradedSkill;
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
