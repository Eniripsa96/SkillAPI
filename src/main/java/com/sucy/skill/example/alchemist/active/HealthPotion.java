package com.sucy.skill.example.alchemist.active;

import com.rit.sucy.player.Protection;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class HealthPotion extends ClassSkill implements SkillShot, Listener {

    public static final String NAME = "Health Potion";
    private static final String
            HEAL_AMOUNT = "Heal Amount",
            BONUS_DURATION = "Bonus Duration";

    public HealthPotion() {
        super(NAME, SkillType.SKILL_SHOT, Material.GOLDEN_APPLE, 5);

        description.add("Launches a potion that");
        description.add("heals allies over time");
        description.add("and grants bonus health");
        description.add("for a short time.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 12, -1);
        setAttribute(SkillAttribute.MANA, 18, -1);

        setAttribute(HEAL_AMOUNT, 6, 2);
        setAttribute(BONUS_DURATION, 6, 2);
    }

    @Override
    public boolean cast(Player player, int level) {

        Potion potion = new Potion(PotionType.REGEN, 1);
        potion.setSplash(true);

        ItemStack item = new ItemStack(Material.POTION);
        potion.apply(item);

        ThrownPotion thrownPotion = player.launchProjectile(ThrownPotion.class);
        thrownPotion.setItem(item);
        thrownPotion.setMetadata(NAME, new FixedMetadataValue(api, level));

        return true;
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if (event.getEntity().hasMetadata(NAME)) {
            int level = event.getEntity().getMetadata(NAME).get(0).asInt();
            int regenDuration = (int)(12 * getAttribute(HEAL_AMOUNT, level));
            int healthDuration = (int)(20 * getAttribute(BONUS_DURATION, level));

            PotionEffect healEffect = new PotionEffect(PotionEffectType.REGENERATION, regenDuration, 2);
            PotionEffect healthEffect = new PotionEffect(PotionEffectType.HEALTH_BOOST, healthDuration, 0);
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (Protection.isAlly((Player) event.getEntity().getShooter(), entity)) {
                    entity.addPotionEffect(healEffect);
                    entity.addPotionEffect(healthEffect);
                }
            }

            event.getAffectedEntities().clear();
        }
    }
}
