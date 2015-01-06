package com.sucy.skill.api.player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.SkillStatus;
import com.sucy.skill.api.skills.Skill;
import org.bukkit.Material;

public final class PlayerSkill
{

    private Skill       skill;
    private PlayerData  player;
    private PlayerClass parent;
    private Material    bind;
    private long        cooldown;
    private int         level;
    private int         points;

    public PlayerSkill(PlayerData player, Skill skill, PlayerClass parent)
    {
        this.player = player;
        this.skill = skill;
        this.parent = parent;
    }

    public boolean isUnlocked()
    {
        return level > 0;
    }

    public Skill getData()
    {
        return skill;
    }

    public PlayerClass getPlayerClass()
    {
        return parent;
    }

    public PlayerData getPlayerData()
    {
        return player;
    }

    public Material getBind()
    {
        return bind;
    }

    public int getLevel()
    {
        return level;
    }

    public int getPoints()
    {
        return points;
    }

    public boolean isOnCooldown()
    {
        return cooldown > System.currentTimeMillis();
    }

    public boolean isMaxed()
    {
        return level >= skill.getMaxLevel();
    }

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

    public void setLevel(int level)
    {
        this.level = level;
    }

    public void setPoints(int points)
    {
        this.points = points;
    }

    public void addLevels(int amount)
    {
        this.level += amount;
    }

    public void addPoints(int amount)
    {
        this.points += amount;
    }

    public void setBind(Material mat)
    {
        this.bind = mat;
        getPlayerData().bind(mat, this);
    }

    public void revert()
    {
        parent.givePoints(points);
        points = 0;
        level = 0;
    }

    public void startCooldown()
    {
        cooldown = System.currentTimeMillis() + (int) (skill.getCooldown(level) * 1000);
    }

    public void refreshCooldown()
    {
        cooldown = 0;
    }

    public void subtractCooldown(double seconds)
    {
        cooldown -= seconds * 1000;
    }

    public void addCooldown(double seconds)
    {
        if (isOnCooldown())
        {
            cooldown += (int) (seconds * 1000);
        }
        else
        {
            cooldown = System.currentTimeMillis() + (int) (seconds * 1000);
        }
    }
}
