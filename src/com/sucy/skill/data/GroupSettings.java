package com.sucy.skill.data;

import com.rit.sucy.config.parse.DataSection;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Settings for class groups
 */
public class GroupSettings
{
    private static final String PROFESS_RESET    = "profess-reset";
    private static final String CAN_RESET        = "can-reset";
    private static final String EXP_LOST         = "exp-lost-on-death";
    private static final String STARTING_POINTS  = "starting-points";
    private static final String POINTS_PER_LEVEL = "points-per-level";
    private static final String PERMISSION       = "permission";
    private static final String DEFAULT          = "default";
    private static final String ATTRIB_PER_LEVEL = "attribs-per-level";

    private String  defaultClass;
    private String  permission;
    private boolean professReset;
    private double  deathPenalty;
    private int     startingPoints;
    private int     pointsPerLevel;
    private int     attribsPerLevel;

    /**
     * Initializes a new set of settings for a class group by
     * loading settings from the config
     *
     * @param config config to load from
     */
    public GroupSettings(DataSection config)
    {
        this();

        defaultClass = config.getString(DEFAULT, defaultClass);
        permission = config.getString(PERMISSION, permission);
        professReset = config.getBoolean(PROFESS_RESET, professReset);
        deathPenalty = config.getDouble(EXP_LOST, deathPenalty);
        startingPoints = config.getInt(STARTING_POINTS, startingPoints);
        pointsPerLevel = config.getInt(POINTS_PER_LEVEL, pointsPerLevel);
        attribsPerLevel = config.getInt(ATTRIB_PER_LEVEL, attribsPerLevel);

        save(config);
    }

    /**
     * Initializes a default collection of group settings
     */
    public GroupSettings()
    {
        defaultClass = "none";
        permission = "none";
        professReset = false;
        deathPenalty = 0;
        startingPoints = 1;
        pointsPerLevel = 1;
        attribsPerLevel = 1;
    }

    /**
     * Retrieves the default class of the group
     *
     * @return default class of the group or null/"none" if none
     */
    public RPGClass getDefault()
    {
        return SkillAPI.getClass(defaultClass);
    }

    /**
     * Retrieves whether or not the group requires a permission to use at all
     *
     * @return true if requires a permission, false otherwise
     */
    public boolean requiresPermission()
    {
        return !permission.equals("none");
    }

    /**
     * Gets the permission required by the group
     *
     * @return required permission or null if none
     */
    public String getPermission()
    {
        return requiresPermission() ? permission : null;
    }

    /**
     * Checks whether or not classes reset upon profession in the group
     *
     * @return true if resets upon profession, false otherwise
     */
    public boolean isProfessReset()
    {
        return professReset;
    }

    /**
     * Retrieves the death penalty for classes in this group
     *
     * @return death penalty
     */
    public double getDeathPenalty()
    {
        return deathPenalty;
    }

    /**
     * Retrieves the number of skill points classes in this group start with
     *
     * @return starting skill points
     */
    public int getStartingPoints()
    {
        return startingPoints;
    }

    /**
     * Retrieves the number of skill points gained in the group per level
     *
     * @return skill points per level
     */
    public int getPointsPerLevel()
    {
        return pointsPerLevel;
    }

    /**
     * Retrieves the number of attribute points gained each level
     *
     * @return attribute points gained each level
     */
    public int getAttribsPerLevel()
    {
        return attribsPerLevel;
    }

    /**
     * Saves the group settings to a config
     *
     * @param config config to save to
     */
    public void save(DataSection config)
    {
        config.set(DEFAULT, defaultClass);
        config.set(PERMISSION, permission);
        config.set(PROFESS_RESET, professReset);
        config.set(EXP_LOST, deathPenalty);
        config.set(STARTING_POINTS, startingPoints);
        config.set(POINTS_PER_LEVEL, pointsPerLevel);
        config.set(ATTRIB_PER_LEVEL, attribsPerLevel);
    }
}
