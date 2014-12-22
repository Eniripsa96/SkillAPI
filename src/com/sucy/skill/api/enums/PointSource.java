package com.sucy.skill.api.enums;

/**
 * <p>A collection of possible reasons skill points were gained by a player.</p>
 * <p>This is used when giving skill points to a player to allow effects
 * to react differently depending on why it is being added.</p>
 */
public enum PointSource
{
    /**
     * The player leveled up to get more skill points
     */
    LEVEL,

    /**
     * The player downgraded a skill and was refunded the skill points
     */
    REFUND,

    /**
     * A command gave the player additional skill points
     */
    COMMAND,

    /**
     * The player was given skill points for an unspecified reason
     */
    SPECIAL,

    /**
     * The skill points are from the player's data being initialized on startup
     */
    INITIALIZATION,
}
