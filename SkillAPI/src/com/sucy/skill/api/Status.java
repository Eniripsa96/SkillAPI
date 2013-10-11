package com.sucy.skill.api;

/**
 * Statuses able to be applied to a player
 */
public enum Status {

    /**
     * Unable to move, cast skills, or deal auto-attack damage
     */
    STUN,

    /**
     * Unable to move
     */
    ROOT,

    /**
     * Unable to cast skills
     */
    SILENCE,

    /**
     * Unable to deal auto-attack damage
     */
    DISARM,

    /**
     * Healing received damages instead
     */
    CURSE,

    /**
     * Damage taken heals instead
     */
    ABSORB,

    /**
     * Nullify damage taken
     */
    INVINCIBLE
}
