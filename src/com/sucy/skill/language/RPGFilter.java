package com.sucy.skill.language;

import com.rit.sucy.config.CustomFilter;

/**
 * Various text filters used by SkillAPI
 */
public class RPGFilter
{
    public static final CustomFilter NAME       = new CustomFilter("{name}", "");
    public static final CustomFilter MAX        = new CustomFilter("{max}", "");
    public static final CustomFilter TYPE       = new CustomFilter("{type}", "");
    public static final CustomFilter LEVEL      = new CustomFilter("{level}", "");
    public static final CustomFilter GROUP      = new CustomFilter("{group}", "");
    public static final CustomFilter CLASS      = new CustomFilter("{class}", "");
    public static final CustomFilter POINTS     = new CustomFilter("{points}", "");
    public static final CustomFilter COOLDOWN   = new CustomFilter("{cooldown}", "");
    public static final CustomFilter SKILL      = new CustomFilter("{skill}", "");
    public static final CustomFilter MANA       = new CustomFilter("{mana}", "");
    public static final CustomFilter COST       = new CustomFilter("{cost}", "");
    public static final CustomFilter MISSING    = new CustomFilter("{missing}", "");
    public static final CustomFilter DURATION   = new CustomFilter("{duration}", "");
    public static final CustomFilter PROFESSION = new CustomFilter("{profession}", "");
    public static final CustomFilter EXP        = new CustomFilter("{exp}", "");
    public static final CustomFilter ITEM       = new CustomFilter("{item}", "");
    public static final CustomFilter ACCOUNT    = new CustomFilter("{account}", "");
}
