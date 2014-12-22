package com.sucy.skill.data;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import org.bukkit.configuration.ConfigurationSection;

public class GroupSettings
{
    private static final String PROFESS_RESET    = "profess-reset";
    private static final String CAN_RESET        = "can-reset";
    private static final String EXP_LOST         = "exp-lost-on-death";
    private static final String STARTING_POINTS  = "starting-points";
    private static final String POINTS_PER_LEVEL = "points-per-level";
    private static final String PERMISSION       = "permission";
    private static final String DEFAULT          = "default";

    private String  defaultClass;
    private String  permission;
    private boolean professReset;
    private boolean resetable;
    private double  deathPenalty;
    private int     startingPoints;
    private int     pointsPerLevel;

    public GroupSettings(ConfigurationSection config)
    {
        this();

        defaultClass = config.getString(DEFAULT, defaultClass);
        permission = config.getString(PERMISSION, permission);
        professReset = config.getBoolean(PROFESS_RESET, professReset);
        resetable = config.getBoolean(CAN_RESET, resetable);
        deathPenalty = config.getDouble(EXP_LOST, deathPenalty);
        startingPoints = config.getInt(STARTING_POINTS, startingPoints);
        pointsPerLevel = config.getInt(POINTS_PER_LEVEL, pointsPerLevel);

        save(config);
    }

    public GroupSettings()
    {
        defaultClass = "none";
        permission = "none";
        professReset = false;
        resetable = true;
        deathPenalty = 0;
        startingPoints = 1;
        pointsPerLevel = 1;
    }

    public RPGClass getDefault()
    {
        return SkillAPI.getClass(defaultClass);
    }

    public boolean requiresPermission()
    {
        return !permission.equals("none");
    }

    public String getPermission()
    {
        return requiresPermission() ? permission : null;
    }

    public boolean isProfessReset()
    {
        return professReset;
    }

    public boolean isResetable()
    {
        return resetable;
    }

    public double getDeathPenalty()
    {
        return deathPenalty;
    }

    public int getStartingPoints()
    {
        return startingPoints;
    }

    public int getPointsPerLevel()
    {
        return pointsPerLevel;
    }

    public void setResetable(boolean resetable)
    {
        this.resetable = resetable;
    }

    public void setDeathPenalty(double penalty)
    {
        this.deathPenalty = penalty;
    }

    public void setStartingPoints(int points)
    {
        this.startingPoints = points;
    }

    public void setPointsPerLevel(int points)
    {
        this.pointsPerLevel = points;
    }

    public void save(ConfigurationSection config)
    {
        config.set(DEFAULT, defaultClass);
        config.set(PERMISSION, permission);
        config.set(PROFESS_RESET, professReset);
        config.set(CAN_RESET, resetable);
        config.set(EXP_LOST, deathPenalty);
        config.set(STARTING_POINTS, startingPoints);
        config.set(POINTS_PER_LEVEL, pointsPerLevel);
    }
}
