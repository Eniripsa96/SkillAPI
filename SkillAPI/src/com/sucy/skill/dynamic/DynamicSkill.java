package com.sucy.skill.dynamic;

import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DynamicSkill extends Skill implements SkillShot, PassiveSkill
{
    public DynamicSkill(String name)
    {
        super(name, "Dynamic", Material.JACK_O_LANTERN, 1);
    }

    @Override
    public void onUpgrade(Player player, int newLevel)
    {

    }

    @Override
    public void onInitialize(Player player, int level)
    {

    }

    @Override
    public void stopEffects(Player player, int level)
    {

    }

    @Override
    public boolean cast(Player player, int level)
    {
        return false;
    }
}
