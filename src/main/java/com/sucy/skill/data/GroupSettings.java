/**
 * SkillAPI
 * com.sucy.skill.data.GroupSettings
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.data;

import com.rit.sucy.config.parse.DataSection;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;

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
    private static final String STARTING_ATTRIBS = "starting-attribs";
    private static final String ATTRIB_PER_LEVEL = "attribs-per-level";
    private static final String SCOREBOARD       = "show-scoreboard";

    private String  defaultClass;
    private String  permission;
    private boolean professReset;
    private boolean showScoreboard;
    private boolean canReset;
    private double  deathPenalty;
    private int     startingPoints;
    private double  pointsPerLevel;
    private double  attribsPerLevel;
    private int     startingAttribs;

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
        showScoreboard = config.getBoolean(SCOREBOARD, showScoreboard);
        canReset = config.getBoolean(CAN_RESET, canReset);
        deathPenalty = config.getDouble(EXP_LOST, deathPenalty);
        startingPoints = config.getInt(STARTING_POINTS, startingPoints);
        pointsPerLevel = config.getDouble(POINTS_PER_LEVEL, pointsPerLevel);
        attribsPerLevel = config.getDouble(ATTRIB_PER_LEVEL, attribsPerLevel);
        startingAttribs = config.getInt(STARTING_ATTRIBS, startingAttribs);

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
        showScoreboard = true;
        canReset = true;
        deathPenalty = 0;
        startingPoints = 1;
        pointsPerLevel = 1;
        attribsPerLevel = 1;
        startingAttribs = 0;
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
     * @return true if the group is allowed to reset, false otherwise
     */
    public boolean canReset()
    {
        return canReset;
    }

    /**
     * @return true if should show the scoreboard, false otherwise
     */
    public boolean isShowScoreboard()
    {
        return showScoreboard;
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
    public double getPointsPerLevel()
    {
        return pointsPerLevel;
    }

    /**
     * Gets the number of skill points to gain for a specified level
     *
     * @param newLevel level to check for
     * @param oldLevel level coming from
     *
     * @return gained points
     */
    public int getPointsForLevels(int newLevel, int oldLevel)
    {
        return (int) (newLevel * pointsPerLevel) - (int) (oldLevel * pointsPerLevel);
    }

    /**
     * Retrieves the number of attribute points gained each level
     *
     * @return attribute points gained each level
     */
    public double getAttribsPerLevel()
    {
        return attribsPerLevel;
    }

    /**
     * Gets the number of attribute points to gain for a specified level
     *
     * @param newLevel level to check for
     * @param oldLevel level coming from
     *
     * @return gained points
     */
    public int getAttribsForLevels(int newLevel, int oldLevel)
    {
        return (int) (newLevel * attribsPerLevel) - (int) (oldLevel * attribsPerLevel);
    }

    /**
     * @return attribute points classes in the group start with
     */
    public int getStartingAttribs()
    {
        return startingAttribs;
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
        config.set(CAN_RESET, canReset);
        config.set(SCOREBOARD, showScoreboard);
        config.set(EXP_LOST, deathPenalty);
        config.set(STARTING_POINTS, startingPoints);
        config.set(POINTS_PER_LEVEL, pointsPerLevel);
        config.set(STARTING_ATTRIBS, startingAttribs);
        config.set(ATTRIB_PER_LEVEL, attribsPerLevel);
    }
}
