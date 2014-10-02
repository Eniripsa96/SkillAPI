package com.sucy.skill.language;

public class StatusNodes
{

    public static final String

            /**
             * Base node for status messages
             */
            BASE = "Status.",

    /**
     * When a player is stunned and tries to move/attack/cast a skill
     */
    STUNNED = BASE + "stunned",

    /**
     * When a player is rooted and tries to move
     */
    ROOTED = BASE + "rooted",

    /**
     * When a player is silenced and tries to cast a skill
     */
    SILENCED = BASE + "silenced",

    /**
     * When a player tries to attack while disarmed
     */
    DISARMED = BASE + "disarmed",

    /**
     * When a player attacks an invincible target
     */
    INVINCIBLE = BASE + "invincible";
}
