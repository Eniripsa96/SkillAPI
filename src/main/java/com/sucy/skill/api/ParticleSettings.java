package com.sucy.skill.api;

import com.rit.sucy.version.VersionManager;
import com.sucy.skill.api.util.ParticleHelper;
import net.minecraft.world.entity.Entity;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.logging.Level;

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
            CraftWorld cw = (CraftWorld) location.getWorld();
            Entity fake = cw.createEntity(location, Wolf.class);
            cw.getHandle().broadcastEntityEffect(fake, entityEffect.getData());
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

            boolean isParticle = isValidEnum(Particle.class, particle);

            if (isValidEnum(Effect.class, particle) && !isParticle) {
                effect = Effect.valueOf(particle);
            } else if (isValidEnum(EntityEffect.class, particle) && !isParticle) {
                entityEffect = EntityEffect.valueOf(particle);
            } else if (VersionManager.isVersionAtLeast(11300)) {

                ParticleBuilder builder = new ParticleBuilder(Particle.valueOf(particle))
                        .location(caster.getLocation())
                        .offset(dx, dy, dz)
                        .count(amount)
                        .extra(speed);

                if (particle.toLowerCase().startsWith("block")) {
                    builder.data(mat.createBlockData());
                }

                else if (particle.toLowerCase().startsWith("icon")) {
                    builder.data(new ItemStack(mat));
                }

                else if (particle.equalsIgnoreCase("redstone")) {
                    String hexColor = originalSettings.getString(ParticleHelper.RGB_KEY, null);

                    builder.data(Color.RED);

                    if (hexColor != null) {
                        hexColor = hexColor.startsWith("#") ? hexColor : "#" + hexColor;
                        builder.data(Color.fromRGB(Integer.decode(hexColor)));
                    }

                    builder.extra(speed == 0 ? 0.001F : speed);
                }

                if (onlyCaster) {
                    builder.receivers((Player) caster);
                } else {
                    builder.receivers(rad);
                }

                particleBuilder = builder;
            }

        } catch (Exception ex) {
        	Bukkit.getLogger().log(Level.INFO, "SkillAPI Error: " + caster.getName());
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
