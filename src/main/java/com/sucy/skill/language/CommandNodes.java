package com.sucy.skill.language;

/**
 * <p>Configuration nodes for messages pertaining to commands</p>
 * <p>This is primarily for the API retrieving config messages.
 * You shouldn't need to use these values at all.</p>
 */
public class CommandNodes {

    public static final String

    /**
     * Base node for all command messages
     */
    BASE = "Commands.",

    /**
     * Root command for the sub commands
     */
    ROOT = BASE + "root",

    // --------------------- Sections ---------------------------- //

    /**
     * Arguments section root
     */
    ARGUMENTS = BASE + "arguments.",

    /**
     * Complete section root
     */
    COMPLETE = BASE + "complete.",

    /**
     * Description section root
     */
    DESCRIPTION = BASE + "description.",

    /**
     * Name section root
     */
    NAME = BASE + "name.",

    // ------------------- Special Cases ------------------------- //

    /**
     * When a player has a class for the info command
     */
    HAS_CLASS = ".has-class",

    /**
     * When a player doesn't have a class for the info command
     */
    NO_CLASS = ".no-class",

    /**
     * When a player has profession options for the options command
     */
    HAS_OPTIONS = ".has-options",

    /**
     * When a player doesn't have profession options for the options command
     */
    NO_OPTIONS = ".no-options",

    /**
     * When a player confirmed the reset command
     */
    CONFIRMED = ".confirmed",

    /**
     * When a player hasn't confirmed the reset command
     */
    NOT_CONFIRMED = ".not-confirmed",

    /**
     * Toggling something on
     */
    ON = ".toggle-on",

    /**
     * Toggling something off
     */
    OFF = ".toggle-off",

    // --------------------- Commands ---------------------------- //

    /**
     * Admin command for professing players
     */
    ADMIN_PROFESS = "admin-profess",

    /**
     * Admin command for resetting players
     */
    ADMIN_RESET = "admin-reset",

    /**
     * Bind command key
     */
    BIND = "bind",

    /**
     * Cast command key
     */
    CAST = "cast",

    /**
     * Experience command by the player
     */
    EXP = "exp-player",

    /**
     * Info player command key
     */
    INFO = "info-player",

    /**
     * Level player command key
     */
    LEVEL = "level-player",

    /**
     * Info console command key
     */
    INFO_CONSOLE = "info-console",

    /**
     * Level console command key
     */
    LEVEL_CONSOLE = "level-console",

    /**
     * Mana command key
     */
    MANA = "mana",

    /**
     * Options command key
     */
    OPTIONS = "options",

    /**
     * Points command by the player
     */
    POINTS_PLAYER = "points-player",

    /**
     * Points command by the console
     */
    POINTS_CONSOLE = "points-console",

    /**
     * Profess command key
     */
    PROFESS = "profess",

    /**
     * Reset command key
     */
    RESET = "reset",

    /**
     * Skills command key
     */
    SKILLS = "skills",

    /**
     * Reloads the plugin data
     */
    RELOAD = "reload",

    /**
     * Toggles the skill bar
     */
    TOGGLE_BAR = "bar",

    /**
     * Unbinding a skill
     */
    UNBIND = "unbind",

    // ------------------- Error Messages ------------------------ //

    /**
     * Base node for all error messages
     */
    ERROR_BASE = BASE + "error.",

    /**
     * A skill cannot be cast
     */
    CANNOT_BE_CAST = ERROR_BASE + "cannot-be-cast",

    /**
     * Error message for being unable to receive points
     */
    CANNOT_GET_POINTS = ERROR_BASE + "cannot-get-points",

    /**
     * When a player doesn't have a class to level up with
     */
    CANNOT_LEVEL = ERROR_BASE + "cannot-level",

    /**
     * A player cannot profess into a new class
     */
    CANNOT_PROFESS = ERROR_BASE + "cannot-profess",

    /**
     * Cannot view the stats of other players
     */
    CANNOT_SEE_STATS = ERROR_BASE + "cannot-see-stats",

    /**
     * Player already is the maximum level
     */
    MAX_LEVEL = ERROR_BASE + "max-level",

    /**
     * There is no skill to unbind
     */
    NO_BOUND_SKILL = ERROR_BASE + "no-bound-skill",

    /**
     * A player doesn't have a class
     */
    NO_CHOSEN_CLASS = ERROR_BASE + "no-class",

    /**
     * A player cannot be in creative mode
     */
    NO_CREATIVE = ERROR_BASE + "no-creative",

    /**
     * A player doesn't have a held item
     */
    NO_HELD_ITEM = ERROR_BASE + "no-held-item",

    /**
     * A player doesn't have enough space in there inventory
     */
    NO_SPACE = ERROR_BASE + "no-space",

    /**
     * No target for a targeted skill
     */
    NO_TARGET = "no-target",

    /**
     * The argument isn't the name of a valid class
     */
    NOT_A_CLASS = ERROR_BASE + "not-a-class",

    /**
     * The argument isn't the name of a valid player
     */
    NOT_A_PLAYER = ERROR_BASE + "not-a-player",

    /**
     * The argument isn't the name of a valid skill
     */
    NOT_A_SKILL = ERROR_BASE + "not-a-skill",

    /**
     * A value is not a positive number when it should be
     */
    NOT_POSITIVE = ERROR_BASE + "not-positive",

    /**
     * A player doesn't own the skill
     */
    SKILL_NOT_OWNED = ERROR_BASE + "skill-not-owned";
}
