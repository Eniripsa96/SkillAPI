package com.sucy.skill.api.skill;

/**
 * Statuses for a skill in relation to a player
 */
public enum SkillStatus {

    /**
     * The skill is currently on cooldown
     */
    ON_COOLDOWN,

    /**
     * The player requires more mana before using the skill
     */
    MISSING_MANA,

    /**
     * The skill is ready to be used
     */
    READY
}
