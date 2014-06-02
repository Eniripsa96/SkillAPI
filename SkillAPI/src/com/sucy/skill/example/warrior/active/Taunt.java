package com.sucy.skill.example.warrior.active;

import com.sucy.skill.api.Status;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.api.skill.TargetSkill;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Target skill that pulls in the target and roots them
 */
public class Taunt extends ClassSkill implements TargetSkill {

    public static final String NAME = "Taunt";
    private static final String
        DURATION = "Root Duration";

    public Taunt() {
        super(NAME, SkillType.TARGET, Material.RAW_CHICKEN, 4);

        description.add("Pulls the target in");
        description.add("towards you and roots");
        description.add("them in place.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 8, 0);
        setAttribute(SkillAttribute.MANA, 20, 0);
        setAttribute(SkillAttribute.RANGE, 5, 1);

        setAttribute(DURATION, 1.4, 0.4);
    }

    @Override
    public boolean cast(Player player, LivingEntity target, int level, boolean ally) {
        if (!ally) {
            Vector dir = player.getLocation().subtract(target.getLocation()).toVector();
            dir.multiply(0.2);
            dir.setY(dir.getY() / 5 + 0.5);
            target.setVelocity(dir);
            if (target instanceof Player) {
                api.getStatusHolder(target).addStatus(Status.ROOT, (long)(1000 * getAttribute(DURATION, level)));
            }
            else target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int)(20 * getAttribute(DURATION, level)), 100), true);
        }
        return !ally;
    }
}
