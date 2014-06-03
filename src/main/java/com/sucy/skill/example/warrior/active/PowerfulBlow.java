package com.sucy.skill.example.warrior.active;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.api.skill.TargetSkill;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Target skill that pulls in the target and roots them
 */
public class PowerfulBlow extends ClassSkill implements TargetSkill {

    public static final String NAME = "Powerful Blow";
    private static final String
            DAMAGE = "Damage";

    public PowerfulBlow() {
        super(NAME, SkillType.TARGET, Material.DIAMOND_SWORD, 4);

        description.add("Smashes the target,");
        description.add("dealing moderate damage");
        description.add("and knocks them backwards");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 4, 0);
        setAttribute(SkillAttribute.MANA, 15, 0);
        setAttribute(SkillAttribute.RANGE, 2, 0);

        setAttribute(DAMAGE, 6, 2);
    }

    @Override
    public boolean cast(Player player, LivingEntity target, int level, boolean ally) {
        if (!ally) {
            Vector dir = target.getLocation().subtract(player.getLocation()).toVector();
            dir.multiply(2.5 / dir.length());
            dir.setY(dir.getY() / 5 + 0.5);
            target.damage(getAttribute(DAMAGE, level));
            target.setVelocity(dir);
        }
        return !ally;
    }
}
