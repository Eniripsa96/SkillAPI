package com.sucy.skill.language;

/**
 * <p>Language nodes for miscellaneous messages</p>
 * <p>This is primarily for the API retrieving config messages.
 * You shouldn't need to use these values at all.</p>
 */
public class OtherNodes {

    public static final String

    /**
     * Base node for all skills
     */
    BASE = "Other.",

    /**
     * A player leveled up
     */
    LEVEL_UP = BASE + "level-up",

    /**
     * A player's skill is still on cooldown
     */
    ON_COOLDOWN = BASE + "on-cooldown",

    /**
     * Player doesn't have enough mana to use a skill
     */
    NO_MANA = BASE + "no-mana";
}
