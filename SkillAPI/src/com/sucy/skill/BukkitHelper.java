package com.sucy.skill;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

public class BukkitHelper {

    public static final int
        MC_1_5_2 = 152,
        MC_1_6_2 = 162,
        MC_1_6_4 = 164,
        MC_1_7_2 = 172;

    private static int version;

    /**
     * Initializes the version data
     */
    public static void initialize() {
        String v = Bukkit.getServer().getVersion();
        v = v.substring(v.indexOf("MC:") + 4);
        v = v.substring(0, v.length() - 1);
        String[] parts = v.split("\\.");
        version = Integer.parseInt(parts[0]) * 100 + Integer.parseInt(parts[1]) * 10 + Integer.parseInt(parts[2]);
    }

    /**
     * Checks if the version is at least the given version
     *
     * @param v version to check
     * @return  true if the actual version is at least the provided one
     */
    public static boolean isVerstionAtLeast(int v) {
        return version >= v;
    }

    /**
     * Checks if the version is at most the given version
     *
     * @param v version to check
     * @return  true if the actual version is at most the provided one
     */
    public static boolean isVersionAtMost(int v) {
        return version <= v;
    }

    /**
     * Damages a target while being compatible with 1.5.2 and earlier
     *
     * @param target  target to damage
     * @param damager entity dealing the damage
     * @param damage  damage to deal
     */
    public static void damage(LivingEntity target, LivingEntity damager, double damage) {

        // 1.5.2 and earlier used integer values
        if (isVersionAtMost(MC_1_5_2)) {
            target.damage((int)damage, damager);
        }

        // 1.6.2 and beyond use double values
        else target.damage(damage, damager);
    }

    /**
     * Damages a target while being compatible with 1.5.2 and earlier
     *
     * @param target  target to damage
     * @param damage  damage to deal
     */
    public static void damage(LivingEntity target, double damage) {

        // Allow damage to occur
        int ticks = target.getNoDamageTicks();
        target.setNoDamageTicks(0);

        // 1.5.2 and earlier used integer values
        if (isVersionAtMost(MC_1_5_2)) {
            target.damage((int)damage);
        }

        // 1.6.2 and beyond use double values
        else target.damage(damage);

        // Reset damage timer to before the damage was applied
        target.setNoDamageTicks(ticks);
    }
    /**
     * Sets the max health of an entity while being compatible with 1.5.2 and earlier
     *
     * @param entity entity to set the health for
     * @param amount amount to set the max health to
     */
    public static void setMaxHealth(LivingEntity entity, double amount) {
        double prevMax = entity.getMaxHealth();
        double prevHealth = entity.getHealth();

        // 1.5.2 and earlier used integer values
        if (isVersionAtMost(MC_1_5_2)) {
            entity.setMaxHealth((int) amount);
            entity.setHealth((int) Math.min(Math.max(1, prevHealth + amount - prevMax), amount));
        }

        // 1.6.2 and beyond use double values
        else {
            entity.setMaxHealth(amount);
            entity.setHealth(Math.min(Math.max(1, prevHealth + amount - prevMax), amount));
        }
    }

    /**
     * Heals the entity while being compatible with 1.5.2 and earlier
     *
     * @param entity entity to heal
     * @param amount amount to heal
     */
    public static void heal(LivingEntity entity, double amount) {

        // Cannot go above the enemy health
        double health = entity.getHealth() + amount;
        health = Math.min(entity.getMaxHealth(), health);

        // 1.5.2 and earlier used integer values
        if (isVersionAtMost(MC_1_5_2)) {
            entity.setHealth((int)health);
        }

        // 1.6.2 and later use double values
        else entity.setHealth(health);
    }

    /**
     * Sets the damage of an event while being compatible with 1.5.2 and earlier
     *
     * @param event  event details
     * @param damage damage to set
     */
    public static void setDamage(EntityDamageEvent event, double damage) {

        // 1.5.2 and earlier used integer values
        if (isVersionAtMost(MC_1_5_2)) {
            event.setDamage((int)damage);
        }

        // 1.6.2 and later used double values
        else event.setDamage(damage);
    }

    /**
     * Retrieves the potion effect types array used by mechanics according to the minecraft version
     *
     * @return compatible potion effect type array
     */
    public static PotionEffectType[] getPotionEffectTypes() {

        // 1.5.2 and earlier didn't have Absorption, Health Boost, or Saturation
        if (isVersionAtMost(MC_1_5_2)) {
            return new PotionEffectType[] {
                PotionEffectType.BLINDNESS, PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION,
                PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.FAST_DIGGING, PotionEffectType.FIRE_RESISTANCE,
                PotionEffectType.BLINDNESS, PotionEffectType.HUNGER, PotionEffectType.INCREASE_DAMAGE,
                PotionEffectType.INVISIBILITY, PotionEffectType.JUMP, PotionEffectType.NIGHT_VISION,
                PotionEffectType.POISON, PotionEffectType.REGENERATION, PotionEffectType.BLINDNESS,
                PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.SPEED,
                PotionEffectType.WATER_BREATHING, PotionEffectType.WEAKNESS, PotionEffectType.WITHER };
        }

        // Potions 1.6.2 and beyond
        else {
            return new PotionEffectType[] {
                    PotionEffectType.ABSORPTION, PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION,
                    PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.FAST_DIGGING, PotionEffectType.FIRE_RESISTANCE,
                    PotionEffectType.HEALTH_BOOST, PotionEffectType.HUNGER, PotionEffectType.INCREASE_DAMAGE,
                    PotionEffectType.INVISIBILITY, PotionEffectType.JUMP, PotionEffectType.NIGHT_VISION,
                    PotionEffectType.POISON, PotionEffectType.REGENERATION, PotionEffectType.SATURATION,
                    PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.SPEED,
                    PotionEffectType.WATER_BREATHING, PotionEffectType.WEAKNESS, PotionEffectType.WITHER };
        }
    }
}
