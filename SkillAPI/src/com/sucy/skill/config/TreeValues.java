package com.sucy.skill.config;

/**
 * <p>Config keys for skill tree data values</p>
 * <p>This is primarily for the API saving data. You shouldn't need
 * to use these values at all.</p>
 */
public class TreeValues {

    public static final String

    /**
     * Root path for skill trees
     */
    ROOT = "classes",

    /**
     * Inherited skill trees
     */
    INHERIT = "inherit",

    /**
     * Included skills
     */
    SKILLS = "skills",

    /**
     * Prefix gained for the tree
     */
    PREFIX = "prefix",

    /**
     * Tree succeeding from
     */
    PARENT = "parent",

    /**
     * Required level to move to the next skill tree
     */
    LEVEL = "profess-level",

    /**
     * Base health at level 1
     */
    HEALTH_BASE = "health-base",

    /**
     * Bonus health per level
     */
    HEALTH_BONUS = "health-scale",

    /**
     * Base mana at level 1
     */
    MANA_BASE = "mana-base",

    /**
     * Bonus mana per level
     */
    MANA_BONUS = "mana-scale";
}
