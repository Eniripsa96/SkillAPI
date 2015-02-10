package com.sucy.skill.api.enums;

/**
 * <p>A collection of possible reasons mana was deducted from a player.</p>
 * <p>This is used when deducting mana from a player to allow effects
 * to react differently depending on why it is being deducted.</p>
 */
public enum ManaCost
{
    /**
     * The player cast a skill that used some mana
     */
    SKILL_CAST,

    /**
     * The player was affected by a skill effect that reduced their mana
     */
    SKILL_EFFECT,

    /**
     * The player lost mana for some unspecified reason
     */
    SPECIAL,
}
