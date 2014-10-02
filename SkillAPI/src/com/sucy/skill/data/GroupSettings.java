package com.sucy.skill.data;

import com.sucy.skill.data.io.keys.GroupValues;
import org.bukkit.configuration.ConfigurationSection;

public class GroupSettings
{

    private boolean professReset;
    private boolean resetable;
    private double  deathPenalty;
    private int     startingPoints;
    private int     pointsPerLevel;

    public GroupSettings(ConfigurationSection config)
    {
        this.professReset = config.getBoolean(GroupValues.PROFESS_RESET, false);
        this.resetable = config.getBoolean(GroupValues.CAN_RESET, true);
        this.deathPenalty = config.getDouble(GroupValues.EXP_LOST_ON_DEATH, 0.0);
        this.startingPoints = config.getInt(GroupValues.STARTING_POINTS, 1);
        this.pointsPerLevel = config.getInt(GroupValues.POINTS_PER_LEVEL, 1);
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
        config.set(GroupValues.PROFESS_RESET, professReset);
        config.set(GroupValues.CAN_RESET, resetable);
        config.set(GroupValues.EXP_LOST_ON_DEATH, deathPenalty);
        config.set(GroupValues.STARTING_POINTS, startingPoints);
        config.set(GroupValues.POINTS_PER_LEVEL, pointsPerLevel);
    }
}
