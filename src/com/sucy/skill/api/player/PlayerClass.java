package com.sucy.skill.api.player;

import com.rit.sucy.config.Filter;
import com.rit.sucy.config.FilterType;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.enums.PointSource;
import com.sucy.skill.api.event.PlayerExperienceGainEvent;
import com.sucy.skill.api.event.PlayerGainSkillPointsEvent;
import com.sucy.skill.api.event.PlayerLevelUpEvent;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.language.OtherNodes;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.Bukkit;

/**
 * <p>Represents a player's class progress.</p>
 * <p/>
 * <p>This class if for handling individual players.</p>
 * <p/>
 * <p>This does not include information about the class specifically,
 * rather what the player has within the class. For more general information
 * about the class, you should use the RPGClass class.</p>
 */
public final class PlayerClass
{

    private PlayerData player;
    private RPGClass   classData;
    private int        level;
    private int        points;
    private double     exp;
    private double     totalExp;

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   Constructors                    //
    //                                                   //
    ///////////////////////////////////////////////////////

    public PlayerClass(PlayerData player, RPGClass classData)
    {
        this.player = player;
        this.classData = classData;
        this.level = 1;
        this.points = SkillAPI.getSettings().getGroupSettings(classData.getGroup()).getStartingPoints();
        this.exp = 0;
        this.totalExp = 0;

        for (Skill skill : classData.getSkills())
        {
            player.giveSkill(skill, this);
        }
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                 Accessor Methods                  //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * <p>Retrieves the data of the player owning this class.</p>
     *
     * @return data of owning player
     */
    public PlayerData getPlayerData()
    {
        return player;
    }

    /**
     * <p>Retrieves the generic data for the class.</p>
     *
     * @return generic data for the class
     */
    public RPGClass getData()
    {
        return classData;
    }

    /**
     * <p>Retrieves the experience of the class towards the next level.</p>
     * <p>This should not ever be higher than the required experience.</p>
     *
     * @return the current experience of the class towards the next level
     */
    public double getExp()
    {
        return exp;
    }

    /**
     * <p>Retrieves the required experience to level up to the next level.</p>
     *
     * @return the current required experience
     */
    public double getRequiredExp()
    {
        return classData.getRequiredExp(level);
    }

    /**
     * <p>Retrieves the total amount of experience the player has accumulated
     * for this class since professing as it.</p>
     *
     * @return total accumulated experience for the class
     */
    public double getTotalExp()
    {
        return totalExp;
    }

    /**
     * <p>Retrieves the current level of the class.</p>
     * <p>This should never be less than 1 or greater than the maximum level.</p>
     *
     * @return current level of the class
     */
    public int getLevel()
    {
        return level;
    }

    /**
     * <p>Retrieves the number of skill points the class has currently available.</p>
     * <p>This should never be a negative number.</p>
     *
     * @return number of available skill points
     */
    public int getPoints()
    {
        return points;
    }

    /**
     * <p>Checks whether or not the class has reached the max level.</p>
     *
     * @return true if max level, false otherwise
     */
    public boolean isLevelMaxed()
    {
        return level == classData.getMaxLevel();
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                Functional Methods                 //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * <p>Gives skill points to be used for the class.</p>
     * <p>The number of points cannot be negative.</p>
     * <p>This calls an event that can be cancelled or have the number
     * of points modified.</p>
     * <p>This treats the points as coming from the source "SPECIAL".</p>
     *
     * @param amount amount of points to give
     *
     * @throws java.lang.IllegalArgumentException if the points are less than 1
     */
    public void givePoints(int amount)
    {
        givePoints(amount, PointSource.SPECIAL);
    }

    /**
     * <p>Gives skill points to be used for the class.</p>
     * <p>The number of points cannot be negative.</p>
     * <p>This calls an event that can be cancelled or have the number
     * of points modified.</p>
     *
     * @param amount amount of points to give
     * @param source source of the points
     *
     * @throws java.lang.IllegalArgumentException if the points are less than 1
     */
    public void givePoints(int amount, PointSource source)
    {
        // Cannot give a non-positive amount of points
        if (amount < 1)
        {
            throw new IllegalArgumentException("Invalid point amount - cannot be less than 1");
        }

        // Call the event
        PlayerGainSkillPointsEvent event = new PlayerGainSkillPointsEvent(this, amount, source);
        Bukkit.getPluginManager().callEvent(event);

        // Add the points if not cancelled
        if (!event.isCancelled())
        {
            points += event.getAmount();
        }
    }

    /**
     * Uses points from the player for skill upgrades.
     *
     * @param amount amount of points to use
     */
    public void usePoints(int amount)
    {
        // Cannot use too few points
        if (amount < 0)
        {
            throw new IllegalArgumentException("Invalid points amount - cannot be less than 1");
        }

        // Cannot use more points than obtained
        if (amount > points)
        {
            throw new IllegalArgumentException("Invalid points amount - more than current total");
        }

        // Use the points
        points -= amount;
    }

    /**
     * <p>Sets the amount of points the player's class has without
     * launching an event.</p>
     * <p>This cannot be less than 0.</p>
     * <p>This is used primarily for initialization. You should generally
     * use givePoints(int, PointSource) instead.</p>
     *
     * @param amount number of points to set it to
     */
    public void setPoints(int amount)
    {
        // Cannot have a negative amount of points
        if (amount < 0)
        {
            throw new IllegalArgumentException("Invalid point amount - cannot be less than 1");
        }

        // Set the points
        points = amount;
    }

    /**
     * <p>Gives experience to the class under the context of the experience source.</p>
     * <p>This will also check for leveling up after the experience is added.</p>
     * <p>If the class does not normally receive experience from the source,
     * it will still launch an experience event, just it will start off as
     * cancelled in case it should still be given in select circumstances.</p>
     *
     * @param amount amount of experience to give
     * @param source type of the source of the experience
     */
    public void giveExp(double amount, ExpSource source)
    {
        // Cannot give a non-positive amount of exp
        if (amount <= 0)
        {
            return;
        }

        // Call an event for the experience gained
        PlayerExperienceGainEvent event = new PlayerExperienceGainEvent(this, amount, source);
        event.setCancelled(classData.receivesExp(source));
        Bukkit.getPluginManager().callEvent(event);

        // Add experience if not cancelled
        if (!event.isCancelled() && event.getExp() > 0)
        {
            exp += amount;
            totalExp += amount;
            checkLevelUp();
        }
    }

    /**
     * <p>Sets the total experience the player has gained.</p>
     * <p>This should only be used when loading player data.</p>
     *
     * @param total total amount of experience the player has earned
     */
    public void setTotalExp(double total)
    {
        totalExp = total;
        exp = (classData.getRequiredExp(level) + classData.getRequiredExp(1)) * level / 2;
        checkLevelUp();
    }

    /**
     * <p>Checks whether or not the player has leveled up based on
     * their current experience.</p>
     */
    private void checkLevelUp()
    {
        // Count the number of levels gained, if any
        int levels = 0;
        int required;
        while (exp >= (required = classData.getRequiredExp(level)))
        {
            exp -= required;
            levels++;
        }

        // Give the levels if applicable
        if (levels > 0)
        {
            giveLevels(levels);
        }
    }

    /**
     * <p>Gives levels to the player's class, leveling it up.</p>
     * <p>The amount of levels must be a positive number.</p>
     * <p>This will launch a level event for the gained levels.</p>
     *
     * @param amount amount of levels to give
     *
     * @throws java.lang.IllegalArgumentException when the level amount is less than 1
     */
    public void giveLevels(int amount)
    {
        // Cannot give non-positive amount of levels
        if (amount < 1)
        {
            throw new IllegalArgumentException("Invalid level amount - cannot be less than 1");
        }

        // Level up
        amount = Math.max(amount, classData.getMaxLevel() - level);
        level += amount;
        points += classData.getGroupSettings().getPointsPerLevel() * amount;
        SkillAPI.getLanguage().sendMessage(
                OtherNodes.LEVEL_UP,
                player.getPlayer(),
                FilterType.COLOR,
                RPGFilter.LEVEL.setReplacement(level + ""),
                RPGFilter.CLASS.setReplacement(classData.getName()),
                RPGFilter.POINTS.setReplacement(points + ""),
                Filter.AMOUNT.setReplacement(amount + "")
        );

        // Max Level
        if (isLevelMaxed())
        {
            SkillAPI.getLanguage().sendMessage(
                    OtherNodes.MAX_LEVEL,
                    player.getPlayer(),
                    FilterType.COLOR,
                    RPGFilter.LEVEL.setReplacement(level + ""),
                    RPGFilter.CLASS.setReplacement(classData.getName())
            );
        }

        // Call the event
        PlayerLevelUpEvent event = new PlayerLevelUpEvent(this, amount);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Sets the class data this player class is based off of, optionally
     * resetting the class progress.
     *
     * @param classData class data to switch to
     * @param reset     whether or not to reset class data
     */
    public void setClassData(RPGClass classData, boolean reset)
    {
        this.classData = classData;
        if (reset)
        {
            this.level = 1;
            this.points = SkillAPI.getSettings().getGroupSettings(classData.getGroup()).getStartingPoints();
            this.exp = 0;
        }
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                  File IO Methods                  //
    //                                                   //
    ///////////////////////////////////////////////////////
}
