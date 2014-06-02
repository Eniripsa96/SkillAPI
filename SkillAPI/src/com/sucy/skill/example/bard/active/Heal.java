package com.sucy.skill.example.bard.active;

import com.rit.sucy.player.Protection;
import com.rit.sucy.player.TargetHelper;
import com.rit.sucy.version.VersionManager;
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

public class Heal extends ClassSkill implements SkillShot {

    public static final String NAME = "Heal";
    private static final String
        HEALTH = "Health",
        ANGLE = "Angle";

    public Heal() {
        super(NAME, SkillType.CONE, Material.GOLDEN_APPLE, 5);

        description.add("Heals all allies within");
        description.add("a cone in front of you");
        description.add("for a small amount");
        description.add("including yourself.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 10, -1);
        setAttribute(SkillAttribute.MANA, 25, 0);
        setAttribute(SkillAttribute.RANGE, 3, 0.25);

        setAttribute(HEALTH, 6, 1);
        setAttribute(ANGLE, 90, 0);
    }

    @Override
    public boolean cast(Player player, int level) {
        double health = getAttribute(HEALTH, level);
        VersionManager.heal(player, health);
        double range = getAttribute(SkillAttribute.RANGE, level);
        Location center = player.getLocation();
        Vector dir = center.getDirection();
        dir.setY(0);
        dir.multiply(range / (2 * dir.length()));
        center = center.add(dir);
        ParticleHelper.fillCircle(center, ParticleType.OTHER, 13, (int) (range / 2), (int) (range * range), Direction.XZ);
        List<LivingEntity> targets = TargetHelper.getConeTargets(player, getAttribute(ANGLE, level), range);
        for (LivingEntity target : targets) {
            if (Protection.isAlly(player, target)) {
                VersionManager.heal(target, health);
            }
        }
        return true;
    }
}
