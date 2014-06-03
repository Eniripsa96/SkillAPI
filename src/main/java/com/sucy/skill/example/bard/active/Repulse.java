package com.sucy.skill.example.bard.active;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Repulse extends ClassSkill implements SkillShot {

    public static final String NAME = "Repulse";
    private static final String
            SPEED = "Speed",
            RADIUS = "Radius";

    public Repulse() {
        super(NAME, SkillType.AREA, Material.PISTON_BASE, 5);

        description.add("Pushes away all nearby");
        description.add("players and mobs.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 12, -1);
        setAttribute(SkillAttribute.MANA, 28, -1);

        setAttribute(SPEED, 1, 0.5);
        setAttribute(RADIUS, 2, 0.5);
    }

    @Override
    public boolean cast(Player player, int level) {
        double radius = getAttribute(RADIUS, level);
        double speed = getAttribute(SPEED, level);
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof LivingEntity) {
                Vector vel = entity.getLocation().subtract(player.getLocation()).toVector();
                vel.multiply(speed / vel.length());
                vel.setY(vel.getY() / 5 + 0.5);
                entity.setVelocity(vel);
            }
        }
        return true;
    }
}
