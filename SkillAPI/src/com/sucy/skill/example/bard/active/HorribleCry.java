package com.sucy.skill.example.bard.active;

import com.rit.sucy.player.Protection;
import com.sucy.skill.api.Status;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.api.util.effects.DOT;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HorribleCry extends ClassSkill implements SkillShot {

    public static final String NAME = "Horrible Cry";
    private static final String
            DAMAGE = "Damage",
            DURATION = "Stun Duration",
            RADIUS = "Radius";

    public HorribleCry() {
        super(NAME, SkillType.AREA, Material.GHAST_TEAR, 5);

        description.add("Stuns all nearby enemies");
        description.add("while dealing a small");
        description.add("amount of damage over");
        description.add("four seconds.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 14, -1);
        setAttribute(SkillAttribute.MANA, 28, -1);

        setAttribute(DAMAGE, 4, 1);
        setAttribute(DURATION, 1, 0.1);
        setAttribute(RADIUS, 3, 0.5);
    }

    @Override
    public boolean cast(Player player, int level) {
        double radius = getAttribute(RADIUS, level);
        double damage = getAttribute(DAMAGE, level) / 4;
        int duration = (int)(20 * getAttribute(DURATION, level));
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof LivingEntity) {
                LivingEntity target = (LivingEntity)entity;
                if (Protection.canAttack(player, target)) {
                    api.getDOTHelper().getDOTSet(target).addEffect(NAME, new DOT(80, damage, 20, true));
                    if (target instanceof Player) {
                        api.getStatusHolder(target).addStatus(Status.STUN, duration * 50);
                    }
                    else target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 100), true);
                }
            }
        }
        return true;
    }
}
