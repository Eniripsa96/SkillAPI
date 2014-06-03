package com.sucy.skill.example.warrior.active;

import com.rit.sucy.player.Protection;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Target skill that pulls in the target and roots them
 */
public class GroundPound extends ClassSkill implements SkillShot {

    public static final String NAME = "Ground Pound";
    private static final String
            SPEED = "Speed",
            DAMAGE = "Damage",
            RADIUS = "Radius";

    public GroundPound() {
        super(NAME, SkillType.TARGET, Material.DIRT, 3);

        description.add("Deals minor damage to");
        description.add("all enemies in an area");
        description.add("around you, dealing");
        description.add("minor damage");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 10, -1);
        setAttribute(SkillAttribute.MANA, 35, -3);

        setAttribute(SPEED, 1, 0);
        setAttribute(DAMAGE, 3, 1);
        setAttribute(RADIUS, 2, 0.5);
    }

    @Override
    public boolean cast(Player player, int level) {
        boolean worked = false;

        double damage = getAttribute(DAMAGE, level);
        double speed = getAttribute(SPEED, level);
        double radius = getAttribute(RADIUS, level);
        Vector vel = new Vector(0, speed, 0);
        for (Entity entity : player.getNearbyEntities(radius, radius,radius)) {
            if (entity instanceof LivingEntity) {
                LivingEntity target = (LivingEntity)entity;
                if (Protection.canAttack(player, target)) {
                    target.damage(damage, player);
                    target.setVelocity(vel);
                    worked = true;
                }
            }
        }

        return worked;
    }
}
