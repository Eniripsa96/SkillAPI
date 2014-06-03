package com.sucy.skill.example.hunter.active;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.api.skill.TargetSkill;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Grapple extends ClassSkill implements TargetSkill {

    public static final String NAME = "Grapple";

    public Grapple() {
        super(NAME, SkillType.TARGET, Material.STRING, 5);

        description.add("Drags in a targeted enemy,");
        description.add("bringing them closer to");
        description.add("you depending on how far");
        description.add("apart you are.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 12, -1);
        setAttribute(SkillAttribute.MANA, 18, -1);
        setAttribute(SkillAttribute.RANGE, 4, 2);
    }

    @Override
    public boolean cast(Player player, LivingEntity target, int level, boolean ally) {
        Vector vel = player.getLocation().subtract(target.getLocation()).toVector().multiply(0.3);
        vel.setY(vel.getY() / 5 + 0.5);
        target.setVelocity(vel);
        return true;
    }
}
