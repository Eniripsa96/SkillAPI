package com.sucy.skill.example.hunter.active;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.example.ClassListener;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BlindingDart extends ClassSkill implements SkillShot, Listener {

    public static final String NAME = "Blinding Dart";
    private static final String
            DAMAGE = "Damage",
            DURATION = "Blind Duration";

    public BlindingDart() {
        super(NAME, SkillType.SKILL_SHOT, Material.COAL, 5);

        description.add("Throws a dart that");
        description.add("blinds the first enemy");
        description.add("hit for a short time,");
        description.add("impairing their vision");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 4, -0.5);
        setAttribute(SkillAttribute.MANA, 12, -1);

        setAttribute(DAMAGE, 7, 1);
        setAttribute(DURATION, 2, 0.5);
    }

    @Override
    public boolean cast(Player player, int level) {
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setMetadata(NAME, new FixedMetadataValue(api, level));
        arrow.setMetadata(ClassListener.FLAT_DAMAGE_KEY, new FixedMetadataValue(api, getAttribute(DAMAGE, level)));
        return true;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity && event.getDamager().hasMetadata(NAME)) {
            ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int)(20 * getAttribute(DURATION, event.getDamager().getMetadata(NAME).get(0).asInt())), 1), true);
        }
    }
}
