package com.sucy.skill.config;

/**
 * <p>Config keys for skill tree data values</p>
 * <p>This is primarily for the API saving data. You shouldn't need
 * to use these values at all.</p>
 */
public class ClassValues {

    /**
     * Root path for skill trees
     */
    public static final String ROOT = "classes";

    /**
     * Inherited skill trees
     */
    public static final String INHERIT = "inherit";

    /**
     * Included skills
     */
    public static final String SKILLS = "skills";

    /**
     * Prefix gained for the tree
     */
    public static final String PREFIX = "prefix";

    /**
     * Tree succeeding from
     */
    public static final String PARENT = "parent";

    /**
     * Required level to move to the next skill tree
     */
    public static final String LEVEL = "profess-level";

    /**
     * Base health at level 1
     */
    public static final String HEALTH_BASE = "health-base";

    /**
     * Bonus health per level
     */
    public static final String HEALTH_BONUS = "health-scale";

    /**
     * Base mana at level 1
     */
    public static final String MANA_BASE = "mana-base";

    /**
     * Bonus mana per level
     */
    public static final String MANA_BONUS = "mana-scale";

    /**
     * Custom name for mana
     */
    public static final String MANA_NAME = "mana-name";

    /**
     * Whether or not the class gains mana passively
     */
    public static final String PASSIVE_MANA_GAIN = "passive-mana-gain";

    /**
     * Maximum level of the class
     */
    public static final String MAX_LEVEL = "max-level";

    /**
     * Offset for click combos
     */
    public static final String OFFSET = "offset";

    /**
     * Interval for click combos
     */
    public static final String INTERVAL = "interval";

    /**
     * Whether or not the class needs permission to be used
     */
    public static final String NEEDS_PERMISSION = "needs-permission";

    /**
     * Permissions granted by the class
     */
    public static final String PERMISSIONS = "permissions";
}
