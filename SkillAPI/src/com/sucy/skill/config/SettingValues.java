package com.sucy.skill.config;

/**
 * Configuration nodes for setting values
 */
public class SettingValues {

    public static final String

    // -------------------------- Class Nodes -------------------------- //

    CLASS_ROOT = "Classes.",
    CLASS_DEFAULT = CLASS_ROOT + "default",
    CLASS_HP = CLASS_ROOT + "classless-hp",
    CLASS_STARTING_POINTS = CLASS_ROOT + "starting-points",
    CLASS_POINTS_PER_LEVEL = CLASS_ROOT + "points-per-level",
    CLASS_RESET = CLASS_DEFAULT + "profess-reset",

    // -------------------------- Mana Nodes -------------------------- //

    MANA_ROOT = "Mana.",
    MANA_ENABLED = MANA_ROOT + "enabled",
    MANA_GAIN_FREQ = MANA_ROOT + "gain-freq",
    MANA_GAIN_AMOUNT = MANA_ROOT + "gain-amount",

    // -------------------------- Skill Nodes -------------------------- //

    SKILL_ROOT = "Skills.",
    SKILL_ALLOW_DOWNGRADE = SKILL_ROOT + "allow-downgrade",
    SKILL_TREE_TYPE = SKILL_ROOT + "tree-type",
    SKILL_MESSAGE_RADIUS = SKILL_ROOT + "message-radius",

    // -------------------------- Item Nodes -------------------------- //

    ITEM_ROOT = "Items.",
    ITEM_LORE_REQUIREMENTS = ITEM_ROOT + "lore-requirements",
    ITEM_PLAYERS_PER_CHECK = ITEM_ROOT + "players-per-check",
    ITEM_DEFAULT_ONE_DAMAGE = ITEM_ROOT + "default-one-damage",

    // -------------------------- GUI Nodes -------------------------- //

    GUI_ROOT = "GUI.",
    GUI_OLD_HEALTH = GUI_ROOT + "old-health-bar",
    GUI_LEVEL_BAR = GUI_ROOT + "use-level-bar",
    GUI_SCOREBOARD = GUI_ROOT + "scoreboard-enabled",
    GUI_CLASS_NAME = GUI_ROOT + "show-class-name",
    GUI_CLASS_LEVEL = GUI_ROOT + "show-class-level",

    // -------------------------- Casting Nodes  -------------------------- //

    CAST_ROOT = "Casting.",
    CAST_SKILL_BARS = CAST_ROOT + "use-skill-bars",
    CAST_CLICK_COMBOS = CAST_ROOT + "use-click-combos",

    // -------------------------- Experience Nodes -------------------------- //

    EXP_ROOT = "Experience.",
    EXP_USE_ORBS = EXP_ROOT + "use-exp-orbs",
    EXP_LOST_ON_DEATH = EXP_ROOT + "lost-on-death",
    EXP_BLOCK_SPAWNER = EXP_ROOT + "block-mob-spawner",
    EXP_BLOCK_EGG = EXP_ROOT + "block-mob-egg",
    EXP_BLOCK_CREATIVE = EXP_ROOT + "block-creative",
    EXP_MESSAGE_ENABLED = EXP_ROOT + "messages-enabled",
    EXP_FORMULA = EXP_ROOT + "formula",
    EXP_YIELDS = EXP_ROOT + "yields",

    // -------------------------- Skill Bar Nodes -------------------------- //

    SKILL_BAR = "Skill Bar",

    // -------------------------- Logging Nodes -------------------------- //

    LOG_ROOT = "Logging.",
    LOG_LOAD = LOG_ROOT + "load";
}
