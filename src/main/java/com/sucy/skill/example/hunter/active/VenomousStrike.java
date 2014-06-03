package com.sucy.skill.example.hunter.active;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.api.skill.TargetSkill;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VenomousStrike extends ClassSkill implements TargetSkill {

    public static final String NAME = "Venomous Strike";
    private static final String
        DAMAGE = "Damage",
        DURATION = "Duration";

    public VenomousStrike() {
        super(NAME, SkillType.TARGET, Material.SPIDER_EYE, 5);

        description.add("Strikes a nearby target,");
        description.add("dealing damage and");
        description.add("inflicting them with");
        description.add("a harsh poison.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 10, -1);
        setAttribute(SkillAttribute.MANA, 16, -1);
        setAttribute(SkillAttribute.RANGE, 2, 0.25);

        setAttribute(DAMAGE, 3, 1);
        setAttribute(DURATION, 5, 0);
    }

    @Override
    public boolean cast(Player player, LivingEntity target, int level, boolean ally) {
        if (!ally) {
            target.damage(getAttribute(DAMAGE, level), player);
            target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, (int)(20 * getAttribute(DURATION, level)), 1), true);
        }
        return !ally;
    }
}
