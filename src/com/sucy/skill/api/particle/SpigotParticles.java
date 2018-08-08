package com.sucy.skill.api.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.Map;

/**
 * SkillAPI © 2018
 * com.sucy.skill.api.particle.SpigotParticles
 */
public class SpigotParticles {
    private static boolean error = true;

    public static void play(final Location loc, final String particle, final float dx, final float dy, final float dz, final int count, final float speed, final double distance) {
        play(loc, particle, dx, dy, dz, count, speed, distance, null);
    }

    public static void playItem(final Location loc, final String particle, final float dx, final float dy, final float dz, final int count, final float speed, final double distance, final Material material) {
        play(loc, particle, dx, dy, dz, count, speed, distance, material);
    }

    public static void playBlock(final Location loc, final String particle, final float dx, final float dy, final float dz, final int count, final float speed, final double distance, final Material material) {
        play(loc, particle, dx, dy, dz, count, speed, distance, material);
    }

    private static void play(final Location loc, final String particle, final float dx, final float dy, final float dz, final int count, final float speed, final double distance, final Material material) {
        final String key = particle.toLowerCase().replace('_', ' ');
        final Particle effect = CONVERSION.get(key);
        if (effect == null) return;

        try {
            final Object packet = com.sucy.skill.api.particle.Particle.make(
                    effect.name(), loc.getX(), loc.getY(), loc.getZ(), dx, dy, dz, speed, count, material, 0);
            com.sucy.skill.api.particle.Particle.send(loc, ImmutableList.of(packet), distance);
        } catch (final Exception ex) {
            if (error) {
                ex.printStackTrace();
                error = false;
            }
        }
    }

    private static final Map<String, Particle> CONVERSION = ImmutableMap.<String, Particle>builder()
            .put("angry villager", Particle.VILLAGER_ANGRY)
            .put("barrier", Particle.BARRIER)
            .put("block crack", Particle.BLOCK_CRACK)
            .put("bubble", Particle.WATER_BUBBLE)
            .put("cloud", Particle.CLOUD)
            .put("crit", Particle.CRIT)
            .put("damage indicator", Particle.DAMAGE_INDICATOR)
            .put("death", Particle.SUSPENDED)
            .put("death suspend", Particle.SUSPENDED_DEPTH)
            .put("dragon breath", Particle.DRAGON_BREATH)
            .put("drip lava", Particle.DRIP_LAVA)
            .put("drip water", Particle.DRIP_WATER)
            .put("enchantment table", Particle.ENCHANTMENT_TABLE)
            .put("end rod", Particle.END_ROD)
            .put("ender signal", Particle.PORTAL)
            .put("explode", Particle.EXPLOSION_NORMAL)
            .put("firework spark", Particle.FIREWORKS_SPARK)
            .put("flame", Particle.FLAME)
            .put("footstep", Particle.CLOUD)
            .put("happy villager", Particle.VILLAGER_HAPPY)
            .put("heart", Particle.HEART)
            .put("huge explosion", Particle.EXPLOSION_HUGE)
            .put("hurt", Particle.DAMAGE_INDICATOR)
            .put("icon crack", Particle.ITEM_CRACK)
            .put("instant spell", Particle.SPELL_INSTANT)
            .put("large explode", Particle.EXPLOSION_LARGE)
            .put("large smoke", Particle.SMOKE_LARGE)
            .put("lava", Particle.LAVA)
            .put("magic crit", Particle.CRIT_MAGIC)
            .put("mob spell", Particle.SPELL_MOB)
            .put("mob spell ambient", Particle.SPELL_MOB_AMBIENT)
            .put("mobspawner flames", Particle.FLAME)
            .put("note", Particle.NOTE)
            .put("portal", Particle.PORTAL)
            .put("potion break", Particle.SPELL)
            .put("red dust", Particle.REDSTONE)
            .put("sheep eat", Particle.MOB_APPEARANCE)
            .put("slime", Particle.SLIME)
            .put("smoke", Particle.SMOKE_NORMAL)
            .put("snowball poof", Particle.SNOWBALL)
            .put("snow shovel", Particle.SNOW_SHOVEL)
            .put("spell", Particle.SPELL)
            .put("splash", Particle.WATER_SPLASH)
            .put("sweep attack", Particle.SWEEP_ATTACK)
            .put("suspend", Particle.SUSPENDED)
            .put("town aura", Particle.TOWN_AURA)
            .put("water drop", Particle.WATER_DROP)
            .put("water wake", Particle.WATER_WAKE)
            .put("witch magic", Particle.SPELL_WITCH)
            .put("wolf hearts", Particle.HEART)
            .build();
}
