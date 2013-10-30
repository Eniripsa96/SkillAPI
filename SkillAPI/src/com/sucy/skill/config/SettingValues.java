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
     * Number of players to check for item restrictions each tick
     */
    PLAYERS_PER_CHECK,

    /**
     * Whether or not the API uses old health bar mechanics
     */
    OLD_HEALTH_BAR,

    /**
     * Experience yield for kills
     */
    KILLS,

    /**
     * Formula for experience requirements
     */
    EXP_FORMULA,

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
