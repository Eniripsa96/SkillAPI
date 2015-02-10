package com.sucy.skill.api.enums;

/**
 * <p>A collection of possible reasons mana was gained by a player.</p>
 * <p>This is used when giving mana to a player to allow effects
 * to react differently depending on why it is being added.</p>
 */
public enum ManaSource
{
    /**
     * The player regenerated some mana back passively
     */
    REGEN,

    /**
     * A skill effect replenished some of their mana
     */
    SKILL,

    /**
     * A command restored some of their mana
     */
    COMMAND,

    /**
     * The player gained mana for an unspecified reason
     */
    SPECIAL,
}
