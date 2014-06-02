package com.sucy.skill.example.wizard.active;

import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.api.util.effects.Direction;
import com.sucy.skill.api.util.effects.ParticleHelper;
import com.sucy.skill.api.util.effects.ParticleType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class Funnel extends ClassSkill implements SkillShot {

    public static final String NAME = "Funnel";
    private static final String
        ANGLE = "Angle",
        DAMAGE = "Damage";

    public Funnel() {
        super(NAME, SkillType.CONE, Material.FEATHER, 5);

        description.add("Blasts enemies in a cone");
        description.add("with a burst of wind that");
        description.add("deals moderate damage and");
        description.add("knocks them back slightly.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 10, -1);
        setAttribute(SkillAttribute.MANA, 15, 0);
        setAttribute(SkillAttribute.RANGE, 4, 0);

        setAttribute(ANGLE, 90, 0);
        setAttribute(DAMAGE, 5, 1);
    }

    @Override
    public boolean cast(Player player, int level) {
        double range = getAttribute(SkillAttribute.RANGE, level);
        Location center = player.getLocation();
        Vector dir = center.getDirection();
        dir.setY(0);
        dir.multiply(range / (2 * dir.length()));
        center = center.add(dir);
        ParticleHelper.fillCircle(center, ParticleType.OTHER, 14, (int) (range / 2), (int) (range * range / 4), Direction.XZ);
        List<LivingEntity> targets = TargetHelper.getConeTargets(player, getAttribute(ANGLE, level), range);
        double damage = getAttribute(DAMAGE, level);
        for (LivingEntity target : targets) {
            target.damage(damage, player);
            Vector vel = target.getLocation().subtract(player.getLocation()).toVector();
            vel.multiply(2 / vel.length());
            vel.setY(vel.getY() / 5 + 0.5);
            target.setVelocity(vel);
        }
        return true;
    }
}
