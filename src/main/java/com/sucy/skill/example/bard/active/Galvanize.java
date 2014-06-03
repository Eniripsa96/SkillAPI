package com.sucy.skill.example.bard.active;

import com.rit.sucy.player.Protection;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.api.util.effects.Direction;
import com.sucy.skill.api.util.effects.ParticleHelper;
import com.sucy.skill.api.util.effects.ParticleType;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Galvanize extends ClassSkill implements SkillShot {

    public static final String NAME = "Galvanize";
    private static final String
            DURATION = "Duration",
            RADIUS = "Radius";

    private static final PotionEffectType[] POTION_EFFECTS = new PotionEffectType[] {
            PotionEffectType.BLINDNESS, PotionEffectType.HUNGER, PotionEffectType.WITHER,
            PotionEffectType.WEAKNESS, PotionEffectType.SLOW_DIGGING, PotionEffectType.SLOW,
            PotionEffectType.POISON, PotionEffectType.CONFUSION
    };

    public Galvanize() {
        super(NAME, SkillType.AREA, Material.GOLD_BOOTS, 5);

        description.add("Rallies nearby allies,");
        description.add("speeding them up and");
        description.add("cleansing negative");
        description.add("effects on them.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 12, -1);
        setAttribute(SkillAttribute.MANA, 30, -2);

        setAttribute(DURATION, 1, 0.5);
        setAttribute(RADIUS, 2, 0.25);
    }

    @Override
    public boolean cast(Player player, int level) {
        double duration = getAttribute(DURATION, level);
        apply(player, duration);

        double radius = getAttribute(RADIUS, level);
        ParticleHelper.fillCircle(player.getLocation(), ParticleType.OTHER, 9, (int) radius, (int) (8 * radius * radius), Direction.XZ);
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof LivingEntity) {
                LivingEntity target = (LivingEntity)entity;
                if (Protection.isAlly(player, target)) {
                    apply(target, duration);
                }
            }
        }
        return true;
    }

    private void apply(LivingEntity target, double duration) {
        for (PotionEffectType type : POTION_EFFECTS) {
            target.removePotionEffect(type);
        }
        target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int)(20 * duration), 1), true);
    }
}
