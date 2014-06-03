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

/**
 * Target skill that pulls in the target and roots them
 */
public class HeadSmash extends ClassSkill implements TargetSkill {

    public static final String NAME = "Head Smash";
    private static final String
            DURATION = "Stun Duration";

    public HeadSmash() {
        super(NAME, SkillType.TARGET, Material.IRON_HELMET, 4);

        description.add("Bashes the target with");
        description.add("your head, leaving them");
        description.add("stunned for a short time.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 12, -1);
        setAttribute(SkillAttribute.MANA, 20, 0);
        setAttribute(SkillAttribute.RANGE, 2, 0);

        setAttribute(DURATION, 1, 0.2);
    }

    @Override
    public boolean cast(Player player, LivingEntity target, int level, boolean ally) {
        if (!ally) {
            if (target instanceof Player) {
                api.getStatusHolder(target).addStatus(Status.STUN, (long)(1000 * getAttribute(DURATION, level)));
            }
            else target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int)(20 * getAttribute(DURATION, level)), 100), true);
        }
        return !ally;
    }
}
