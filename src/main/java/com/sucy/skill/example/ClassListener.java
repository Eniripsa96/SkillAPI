package com.sucy.skill.example;

import com.sucy.skill.SkillAPI;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Listener for general class effects
 */
public class ClassListener implements Listener {

    public static final String
        FLAT_DAMAGE_KEY = "flatCustomDamage",
        DAMAGE_KEY = "customDamage",
        SLOW_KEY = "slow";

    /**
     * <p>Sets up the listener for the default classes/skills.</p>
     *
     * @param plugin SkillAPI reference
     */
    public ClassListener(SkillAPI plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Projectile effects
     *
     * @param event event details
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Projectile) {
            LivingEntity target = (LivingEntity)event.getEntity();
            Projectile p = (Projectile)event.getDamager();
            if (p.hasMetadata(FLAT_DAMAGE_KEY)) {
                event.setDamage(p.getMetadata(DAMAGE_KEY).get(0).asDouble());
            }
            if (p.hasMetadata(DAMAGE_KEY)) {
                event.setDamage(event.getDamage() * p.getMetadata(DAMAGE_KEY).get(0).asDouble() / 100);
            }
            if (p.hasMetadata(SLOW_KEY)) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int)(20 * p.getMetadata(SLOW_KEY).get(0).asDouble()), 1));
            }
        }
    }
}
