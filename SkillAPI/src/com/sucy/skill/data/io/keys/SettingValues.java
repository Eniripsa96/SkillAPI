package com.sucy.skill.data.io.keys;

/**
 * Configuration nodes for setting values
 */
public class SettingValues
{

    public static final String

            // -------------------------- Class Nodes -------------------------- //

            CLASS_ROOT           = "Classes.",
            CLASS_DEFAULT        = CLASS_ROOT + "default",
            CLASS_DEFAULT_HP     = CLASS_ROOT + "classless-hp",
            CLASS_EXAMPLES       = CLASS_ROOT + "use-examples",
            CLASS_LIMITED        = CLASS_ROOT + "limited-accounts",
            CLASS_SKILL_EXAMPLES = CLASS_ROOT + "use-example-skills",

    // -------------------------- Mana Nodes -------------------------- //

    MANA_ROOT                = "Mana.",
            MANA_ENABLED     = MANA_ROOT + "enabled",
            MANA_GAIN_FREQ   = MANA_ROOT + "gain-freq",
            MANA_GAIN_AMOUNT = MANA_ROOT + "gain-amount",

    // -------------------------- Skill Nodes -------------------------- //

    SKILL_ROOT                    = "Skills.",
            SKILL_ALLOW_DOWNGRADE = SKILL_ROOT + "allow-downgrade",
            SKILL_TREE_TYPE       = SKILL_ROOT + "tree-type",
            SKILL_SHOW_MESSAGE    = SKILL_ROOT + "show-message",
            SKILL_MESSAGE_RADIUS  = SKILL_ROOT + "message-radius",

    // -------------------------- Item Nodes -------------------------- //

    ITEM_ROOT                       = "Items.",
            ITEM_LORE_REQUIREMENTS  = ITEM_ROOT + "lore-requirements",
            ITEM_PLAYERS_PER_CHECK  = ITEM_ROOT + "players-per-check",
            ITEM_DEFAULT_ONE_DAMAGE = ITEM_ROOT + "default-one-damage",

    // -------------------------- GUI Nodes -------------------------- //

    GUI_ROOT                = "GUI.",
            GUI_OLD_HEALTH  = GUI_ROOT + "old-health-bar",
            GUI_LEVEL_BAR   = GUI_ROOT + "use-level-bar",
            GUI_SCOREBOARD  = GUI_ROOT + "scoreboard-enabled",
            GUI_CLASS_NAME  = GUI_ROOT + "show-class-name",
            GUI_CLASS_LEVEL = GUI_ROOT + "show-class-level",

    // -------------------------- Casting Nodes  -------------------------- //

    CAST_ROOT                        = "Casting.",
            CAST_SKILL_BARS          = CAST_ROOT + "use-skill-bars",
            CAST_SKILL_BAR_COOLDOWNS = CAST_ROOT + "use-skill-bar-cooldowns",
            CAST_CLICK_COMBOS        = CAST_ROOT + "use-click-combos",
            CAST_CLICK_ROOT          = CAST_ROOT + "clicks.",
            CAST_CLICK_LEFT          = CAST_CLICK_ROOT + "left",
            CAST_CLICK_RIGHT         = CAST_CLICK_ROOT + "right",
            CAST_CLICK_SHIFT         = CAST_CLICK_ROOT + "shift",

    // -------------------------- Experience Nodes -------------------------- //

    EXP_ROOT                        = "Experience.",
            EXP_USE_ORBS            = EXP_ROOT + "use-exp-orbs",
            EXP_BLOCK_SPAWNER       = EXP_ROOT + "block-mob-spawner",
            EXP_BLOCK_EGG           = EXP_ROOT + "block-mob-egg",
            EXP_BLOCK_CREATIVE      = EXP_ROOT + "block-creative",
            EXP_MESSAGE_ENABLED     = EXP_ROOT + "messages-enabled",
            EXP_FORMULA             = EXP_ROOT + "formula",
            EXP_YIELDS              = EXP_ROOT + "yields",
            EXP_LVL_MESSAGE_ENABLED = EXP_ROOT + "level-message-enabled",

    // -------------------------- Skill Bar Nodes -------------------------- //

    SKILL_BAR = "Skill Bar",

    // -------------------------- Logging Nodes -------------------------- //

    LOG_ROOT         = "Logging.",
            LOG_LOAD = LOG_ROOT + "load";
}
