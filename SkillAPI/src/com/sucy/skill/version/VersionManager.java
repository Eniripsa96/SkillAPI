package com.sucy.skill.version;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class VersionManager {

    public static int
        MC_1_5_2_MIN = 2788,
        MC_1_6_2_MIN = 2789,
        MC_1_6_4_MIN = 2880,
        MC_1_7_2_MIN = 2922,
        MC_1_7_5_MIN = 3026,
        MC_1_7_8_MIN = 3043,
        MC_1_7_9_MIN = 3057,
        MC_1_5_2_MAX = 2788,
        MC_1_6_2_MAX = 2879,
        MC_1_6_4_MAX = 2919,
        MC_1_7_2_MAX = 3024,
        MC_1_7_5_MAX = 3042,
        MC_1_7_8_MAX = 3055;

    private static int version;

    /**
     * Initializes the version data
     */
    public static void initialize() {
        String v = Bukkit.getServer().getVersion();

        // Load the bukkit version if applicable
        if (v.contains("jnks")) {
            v = v.substring(v.indexOf("-b") + 2);
            v = v.substring(0, v.indexOf("jnks"));
            Bukkit.getLogger().info("Detected CraftBukkit build " + v);
        }

        // Spigot modification in case someone is using it
        else {
            MC_1_5_2_MIN = 832;
            MC_1_6_2_MIN = 1016;
            MC_1_6_4_MIN = 1108;
            MC_1_7_2_MIN = 1141;
            MC_1_7_5_MIN = 1342;
            MC_1_7_8_MIN = 1388;
            MC_1_7_9_MIN = 1434;
            MC_1_5_2_MAX = 964;
            MC_1_6_2_MAX = 1107;
            MC_1_6_4_MAX = 1138;
            MC_1_7_2_MAX = 1339;
            MC_1_7_5_MAX = 1387;
            MC_1_7_8_MAX = 1433;
            v = v.substring(v.lastIndexOf("-") + 1);
            v = v.substring(0, v.indexOf(" "));
            Bukkit.getLogger().info("Detected Spigot build " + v);
        }

        // Get the actual build number
        version = Integer.parseInt(v);
    }

    /**
     * Checks if the version is at least the given version
     *
     * @param v version to check
     * @return  true if the actual version is at least the provided one
     */
    public static boolean isVersionAtMost(int v) {
        return version <= v;
    }

    /**
     * Checks if the version is at most the given version
     *
     * @param v version to check
     * @return  true if the actual version is at most the provided one
     */
    public static boolean isVersionAtLeast(int v) {
        return version >= v;
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
        if (isVersionAtMost(MC_1_5_2_MAX)) {
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
        if (isVersionAtMost(MC_1_5_2_MAX)) {
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
        if (isVersionAtMost(MC_1_5_2_MAX)) {
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
        if (isVersionAtMost(MC_1_5_2_MAX)) {
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
        if (isVersionAtMost(MC_1_5_2_MAX)) {
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
        if (isVersionAtMost(MC_1_5_2_MAX)) {
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
