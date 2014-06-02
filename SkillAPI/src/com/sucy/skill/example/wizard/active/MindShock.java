package com.sucy.skill.example.wizard.active;

import com.rit.sucy.player.Protection;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.api.skill.TargetSkill;
import com.sucy.skill.api.util.effects.DOT;
import com.sucy.skill.api.util.effects.Direction;
import com.sucy.skill.api.util.effects.ParticleHelper;
import com.sucy.skill.api.util.effects.ParticleType;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MindShock extends ClassSkill implements TargetSkill {

    public static final String NAME = "Mind Shock";
    private static final String
            DAMAGE = "DOT Damage",
            RADIUS = "Radius";

    public MindShock() {
        super(NAME, SkillType.TARGET_AOE, Material.BOOK, 5);

        description.add("Shocks the area around");
        description.add("the target, applying a");
        description.add("damage over time to all");
        description.add("enemies affected.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 12, -1);
        setAttribute(SkillAttribute.MANA, 20, -1);
        setAttribute(SkillAttribute.RANGE, 4, 1);

        setAttribute(DAMAGE, 5, 1);
        setAttribute(RADIUS, 2, 0.25);
    }

    @Override
    public boolean cast(Player player, LivingEntity target, int level, boolean ally) {
        if (!ally) {
            double radius = getAttribute(RADIUS, level);
            ParticleHelper.fillCircle(target.getLocation(), ParticleType.OTHER, 21, (int) radius, (int) (4 * radius * radius), Direction.XZ);
            double damage = getAttribute(DAMAGE, level) / 5;
            api.getDOTHelper().getDOTSet(target).addEffect(NAME, new DOT(100, damage, 20, true));
            for (Entity entity : target.getNearbyEntities(radius, radius, radius)) {
                if (entity instanceof LivingEntity) {
                    LivingEntity t = (LivingEntity)entity;
                    if (Protection.canAttack(player, t)) {
                        api.getDOTHelper().getDOTSet(t).addEffect(NAME, new DOT(100, damage, 20, true));
                    }
                }
            }
        }
        return !ally;
    }
}
