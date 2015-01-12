package com.sucy.skill.api.util;

/**
 * Flag keys used by statuses
 */
public class StatusFlag
{
    public static final String STUN       = "stun";
    public static final String ROOT       = "root";
    public static final String INVINCIBLE = "invincible";
    public static final String ABSORB     = "absorb";
    public static final String DISARM     = "disarm";
    public static final String SILENCE    = "silence";

    public static final String[] ALL = new String[] {
        STUN, ROOT, INVINCIBLE, ABSORB, DISARM, SILENCE
    };
}
