package com.sucy.skill.dynamic;

/**
 * Possible triggers for dynamic skill effects
 */
public enum Trigger
{
    /**
     * Trigger effects when a player casts the skill
     */
    CAST,

    /**
     * Trigger effects when the player crouches
     */
    CROUCH,

    /**
     * Trigger effects when the player inflicts non-skill damage
     */
    MELEE_DAMAGE,

    /**
     * Trigger effects when the player inflicts skill damage
     */
    SKILL_DAMAGE,

    /**
     * Trigger effects when the player dies
     */
    DEATH,

    /**
     * Trigger effects when the player falls to a certain health percentage
     */
    HEALTH,

    /**
     * Trigger effects when the skill is available
     */
    INITIALIZE,

    /**
     * Trigger effects when taking non-skill damage
     */
    TOOK_MELEE_DAMAGE,

    /**
     * Trigger effects when taking skill damage
     */
    TOOK_SKILL_DAMAGE
}
