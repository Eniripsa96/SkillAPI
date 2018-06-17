/**
 * SkillAPI
 * com.sucy.skill.api.player.PlayerSkill
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
package com.sucy.skill.api.player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.SkillStatus;
import com.sucy.skill.api.skills.Skill;
import org.bukkit.Material;

/**
 * Represents player-specific data for a skill such as the player's
 * current level for the skill, the cooldown, and other related data.
 */
public final class PlayerSkill
{
    private Skill       skill;
    private PlayerData  player;
    private PlayerClass parent;
    private Material    bind;
    private long        cooldown;
    private int         level;
    private int         points;

    /**
     * Constructs a new PlayerSkill. You should not need to use
     * this constructor as it is provided by the API. Get instances
     * through the PlayerData object.
     *
     * @param player owning player data
     * @param skill  skill template
     * @param parent owning player class
     */
    public PlayerSkill(PlayerData player, Skill skill, PlayerClass parent)
    {
        this.player = player;
        this.skill = skill;
        this.parent = parent;
    }

    /**
     * Checks whether or not the skill is currently unlocked
     * for the player. This requires the skill to be at least
     * level 1.
     *
     * @return true if unlocked, false otherwise
     */
    public boolean isUnlocked()
    {
        return level > 0;
    }

    /**
     * Retrieves the template data for this skill.
     *
     * @return skill template data
     */
    public Skill getData()
    {
        return skill;
    }

    /**
     * Retrieves the owning player class.
     *
     * @return owning player class
     */
    public PlayerClass getPlayerClass()
    {
        return parent;
    }

    /**
     * Retrieves the owning player's data.
     *
     * @return owning player's data
     */
    public PlayerData getPlayerData()
    {
        return player;
    }

    /**
     * Retrieves the material this skill is currently bound to.
     *
     * @return the current material bound to or null if not bound
     */
    public Material getBind()
    {
        return bind;
    }

    /**
     * Retrieves the current level the player has the skill at
     *
     * @return current skill level
     */
    public int getLevel()
    {
        return level;
    }

    /**
     * Retrieves the number of points invested in upgrading this skill
     *
     * @return invested points
     */
    public int getPoints()
    {
        return points;
    }

    /**
     * Retrieves the cost to upgrade the skill to the next level
     *
     * @return cost to upgrade the skill to the next level
     */
    public int getCost()
    {
        return skill.getCost(level);
    }

    /**
     * @return mana cost to use the skill
     */
    public double getManaCost()
    {
        return skill.getManaCost(level);
    }

    /**
     * Retrieves the level requirement of the skill to get to the next level
     *
     * @return the level requirement to get to the next level
     */
    public int getLevelReq()
    {
        return skill.getLevelReq(level);
    }

    /**
     * Checks whether or not the skill is currently on cooldown
     *
     * @return true if on cooldown, false otherwise
     */
    public boolean isOnCooldown()
    {
        return cooldown > System.currentTimeMillis();
    }

    /**
     * Checks whether or not the skill is at its maximum level
     *
     * @return true if at max level, false otherwise
     */
    public boolean isMaxed()
    {
        return level >= skill.getMaxLevel();
    }

    /**
     * Gets the current cooldown of the skill in seconds.
     *
     * @return current cooldown in seconds or 0 if not on cooldown
     */
    public int getCooldown()
    {
        if (isOnCooldown())
        {
            return (int) ((cooldown - System.currentTimeMillis() + 999) / 1000);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Retrieves the current ready status of the skill which could
     * be on cooldown, missing mana, or ready.
     *
     * @return the ready status of the skill
     */
    public SkillStatus getStatus()
    {

        // See if it is on cooldown
        if (isOnCooldown())
        {
            return SkillStatus.ON_COOLDOWN;
        }

        // If mana is enabled, check to see if the player has enough
        if (SkillAPI.getSettings().isManaEnabled()
            && player.getMana() < skill.getManaCost(level))
        {

            return SkillStatus.MISSING_MANA;
        }

        // The skill is available when both off cooldown and when there's enough mana
        return SkillStatus.READY;
    }

    /**
     * Sets the level of the skill. This will not update passive
     * effects. To level up/down the skill properly, use the
     * upgrade and downgrade methods in PlayerData.
     *
     * @param level new level of the skill
     */
    public void setLevel(int level)
    {
        this.level = level;
    }

    /**
     * Sets the number of invested points in the skill. This
     * shouldn't be used by other plugins as it is just for
     * keeping track of points for when it is reset.
     *
     * @param points new point amount
     */
    public void setPoints(int points)
    {
        this.points = points;
    }

    /**
     * Adds levels to the skill. This will not update passive
     * effects. To level up/down the skill properly, use the
     * upgrade and downgrade methods in PlayerData.
     *
     * @param amount number of levels to add
     */
    public void addLevels(int amount)
    {
        this.level = Math.min(this.level + amount, skill.getMaxLevel());
    }

    /**
     * Adds to the number of invested points in the skill. This
     * shouldn't be used by other plugins as it is just for
     * keeping track of points for when it is reset.
     *
     * @param amount amount of invested points to add
     */
    public void addPoints(int amount)
    {
        this.points += amount;
    }

    /**
     * Sets the bind material of the skill
     *
     * @param mat new bind material
     */
    public void setBind(Material mat)
    {
        this.bind = mat;
        getPlayerData().bind(mat, this);
    }

    /**
     * Reverts the skill back to level 0, locking it from
     * casting and refunding invested skill points
     */
    public void revert()
    {
        parent.givePoints(points);
        points = 0;
        level = 0;
    }

    /**
     * Starts the cooldown of the skill
     */
    public void startCooldown()
    {
        cooldown = System.currentTimeMillis() + (int) (skill.getCooldown(level) * 1000);
    }

    /**
     * Refreshes the cooldown of the skill, allowing the
     * player to cast the skill again.
     */
    public void refreshCooldown()
    {
        cooldown = 0;
    }

    /**
     * Subtracts from the current cooldown time, shortening
     * the time until it can be cast again.
     *
     * @param seconds number of seconds to subtract from the cooldown
     */
    public void subtractCooldown(double seconds)
    {
        addCooldown(-seconds);
    }

    /**
     * Adds to the current cooldown time, lengthening
     * the time until it can be cast again.
     *
     * @param seconds number of seconds to add to the cooldown
     */
    public void addCooldown(double seconds)
    {
        if (isOnCooldown())
            cooldown += (int) (seconds * 1000);
        else
            cooldown = System.currentTimeMillis() + (int) (seconds * 1000);
    }
}
