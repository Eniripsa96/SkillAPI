package com.sucy.skill.language;

/**
 * <p>Language nodes for scoreboard stats</p>
 * <p>This is primarily for the API retrieving config messages.
 * You shouldn't need to use these values at all.</p>
 */
public class StatNodes
{

    public static final String

            /**
             * Base node for all stats
             */
            BASE = "Stats.",

    /**
     * Experience title
     */
    EXP             = BASE + "exp",
            EXP_KEY = "exp",

    /**
     * Health title
     */
    HEALTH             = BASE + "health",
            HEALTH_KEY = "health",

    /**
     * Level title
     */
    LEVEL             = BASE + "level",
            LEVEL_KEY = "level",

    /**
     * Mana title
     */
    MANA             = BASE + "mana",
            MANA_KEY = "mana",

    /**
     * Skill points title
     */
    POINTS             = BASE + "points",
            POINTS_KEY = "points";
}
