package com.sucy.skill.config;

/**
 * Configuration nodes for setting values
 */
public enum SettingValues {

    /**
     * Whether or not players reset their profession on profession
     */
    PROFESS_RESET,

    /**
     * Type of the skill tree to use
     */
    TREE_TYPE,

    /**
     * Whether or not the mana system is enabled
     */
    MANA_ENABLED,

    /**
     * Whether or not the scoreboard is enabled
     */
    SCOREBOARD_ENABLED,

    /**
     * Time between players regaining mana
     */
    MANA_GAIN_FREQ,

    /**
     * Amount of mana to gain each interval
     */
    MANA_GAIN_AMOUNT,

    /**
     * Number of points to start with at level 1
     */
    STARTING_POINTS,

    /**
     * Number of points to gain each time a player levels up
     */
    POINTS_PER_LEVEL,

    /**
     * Whether or not lore requirements are enabled
     */
    LORE_REQUIREMENTS,

    /**
     * Number of players to check for item restrictions each tick
     */
    PLAYERS_PER_CHECK,

    /**
     * Whether or not the API uses old health bar mechanics
     */
    OLD_HEALTH_BAR,

    /**
     * Whether or not the API uses the level bar to display class level
     */
    USE_LEVEL_BAR,

    /**
     * Whether or not to use click combos for skill shots
     */
    USE_CLICK_COMBOS,

    /**
     * Whether or not to use experience orbs instead of the settings values
     */
    USE_EXP_ORBS,

    /**
     * Whether or not to block experience gain from mob spawners
     */
    BLOCK_MOB_SPAWNER_EXP,

    /**
     * Whether or not to block experience gain from mob eggs
     */
    BLOCK_MOB_EGG_EXP,

    /**
     * Blocks experience while in creative mode
     */
    BLOCK_CREATIVE_EXP,

    /**
     * Experience yield for kills
     */
    KILLS,

    /**
     * Formula for experience requirements
     */
    EXP_FORMULA,

    /**
     * How large of an area to say that a player used a skill
     */
    SKILL_MESSAGE_RADIUS,

    ;

    /**
     * Path to the setting value
     *
     * @return path
     */
    public String path() {
        return name().toLowerCase().replace("_", "-");
    }
}
