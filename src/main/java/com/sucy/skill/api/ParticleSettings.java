package com.sucy.skill.api;

import com.destroystokyo.paper.ParticleBuilder;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.api.util.ParticleHelper;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class ParticleSettings {

    private final Settings originalSettings;
    private ParticleBuilder particleBuilder;
    private EntityEffect entityEffect;
    private Effect effect;

    public ParticleSettings(LivingEntity caster, Settings settings) {
        this.originalSettings = settings;

        configureParticle(caster);
    }

    public void spawn(Location location) {

        if (particleBuilder != null) {
            particleBuilder.location(location).spawn();
        }

        if (effect != null) {
            location.getWorld().playEffect(location, effect, originalSettings.getInt(ParticleHelper.DATA_KEY, 0));
        }

        if (entityEffect != null) {
            ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
            armorStand.setSilent(true);
            armorStand.setGravity(false);
            armorStand.setMarker(true);
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.playEffect(entityEffect);
            armorStand.remove();
        }

    }

    public Settings getOriginalSettings() {
        return originalSettings;
    }

    private void configureParticle(LivingEntity caster) {
        String particle = originalSettings.getString(ParticleHelper.PARTICLE_KEY, "invalid");
        particle = fixKey(particle);

        final boolean onlyCaster = originalSettings.getBool("onlycaster", true);
        final int rad = originalSettings.getInt(ParticleHelper.VISIBLE_RADIUS_KEY, 25);

        final float dx = (float) originalSettings.getDouble(ParticleHelper.DX_KEY, 0.0);
        final float dy = (float) originalSettings.getDouble(ParticleHelper.DY_KEY, 0.0);
        final float dz = (float) originalSettings.getDouble(ParticleHelper.DZ_KEY, 0.0);

        final int amount = originalSettings.getInt(ParticleHelper.AMOUNT_KEY, 1);
        final float speed = (float) originalSettings.getDouble(ParticleHelper.SPEED_KEY, 1.0);
        final Material mat = Material.matchMaterial(originalSettings.getString(ParticleHelper.MATERIAL_KEY, "DIRT"));

        try {

            if (isValidEnum(Effect.class, particle)) {
                effect = Effect.valueOf(particle);
            } else if (isValidEnum(EntityEffect.class, particle)) {
                entityEffect = EntityEffect.valueOf(particle);
            } else if (VersionManager.isVersionAtLeast(11300)) {

                ParticleBuilder builder = new ParticleBuilder(org.bukkit.Particle.valueOf(particle))
                        .location(caster.getLocation())
                        .offset(dx, dy, dz)
                        .count(amount)
                        .extra(speed);

                if (particle.toLowerCase().startsWith("block")) {
                    builder.data(mat.createBlockData());
                }

                if (particle.toLowerCase().startsWith("icon")) {
                    builder.data(new ItemStack(mat));
                }

                if (particle.equalsIgnoreCase("redstone")) {
                    String hexColor = originalSettings.getString(ParticleHelper.RGB_KEY, null);

                    if (hexColor != null) {

                        hexColor = hexColor.startsWith("#") ? hexColor : "#" + hexColor;

                        java.awt.Color rgbColor = java.awt.Color.decode(hexColor);
                        builder.color(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue());
                    }

                    builder.extra(Math.max(0.001, speed));
                }

                if (onlyCaster) {
                    builder.receivers((Player) caster);
                } else {
                    builder.receivers(rad);
                }

                particleBuilder = builder;
            }

        } catch (Exception ex) {
            System.out.println("ERROR: " + caster.getName());
            originalSettings.dumpToConsole();
        }
    }

    private <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String enumName) {
        if (enumName == null) {
            return false;
        } else {
            try {
                Enum.valueOf(enumClass, enumName);
                return true;
            } catch (IllegalArgumentException var3) {
                return false;
            }
        }
    }

    private String fixKey(String name) {
        String filtered = name;
        if (name.startsWith("minecraft:")) {
            filtered = name.substring("minecraft:".length());
        }

        filtered = filtered.toUpperCase(Locale.ENGLISH);
        return filtered.replaceAll("\\s+", "_").replaceAll("\\W", "");
    }

}
