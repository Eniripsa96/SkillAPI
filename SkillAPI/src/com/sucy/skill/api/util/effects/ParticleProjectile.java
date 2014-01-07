package com.sucy.skill.api.util.effects;

import com.sucy.skill.BukkitHelper;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.dynamic.EmbedData;
import com.sucy.skill.api.event.ParticleProjectileHitEvent;
import com.sucy.skill.api.event.ParticleProjectileLandEvent;
import com.sucy.skill.api.event.ParticleProjectileLaunchEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * A fake projectile that plays particles along its path
 */
public class ParticleProjectile extends BukkitRunnable {

    public static boolean damaging = false;

    private SkillAPI plugin;
    private LivingEntity shooter;
    private Location loc;
    private ParticleType particle;
    private EmbedData embed;
    private Vector vel;
    private int data;
    private int steps;
    private int delay;
    private int count;
    private double damage;

    /**
     * Constructor
     *
     * @param shooter  entity that shot the projectile
     * @param loc      initial location of the projectile
     * @param vel      velocity of the projectile
     * @param particle particle to play
     * @param data     data value for the particle
     * @param delay    delay in ticks for the effect
     * @param damage   damage to deal upon impact
     */
    public ParticleProjectile(LivingEntity shooter, Location loc, Vector vel, ParticleType particle, int data, int delay, double damage) {
        this.plugin = (SkillAPI)Bukkit.getPluginManager().getPlugin("SkillAPI");
        this.shooter = shooter;
        this.loc = loc;
        this.particle = particle;
        this.vel = vel;
        this.data = data;
        this.delay = delay;
        this.damage = damage;
        steps = (int)Math.ceil(vel.length() * 2);
        vel.multiply(1.0 / steps);
        runTaskTimer(plugin, 1, 1);
        plugin.getServer().getPluginManager().callEvent(new ParticleProjectileLaunchEvent(this));
    }

    /**
     * @return shooter of the projectile
     */
    public LivingEntity getShooter() {
        return shooter;
    }

    /**
     * @return damage of the projectile
     */
    public double getDamage() {
        return damage;
    }

    /**
     * @return velocity of the projectile
     */
    public Vector getVelocity() {
        return vel;
    }

    /**
     * @return type of the particle projectile
     */
    public ParticleType getParticleType() {
        return particle;
    }

    /**
     * @return particle data value for the projectile
     */
    public int getData() {
        return data;
    }

    /**
     * Teleports the projectile to a location
     *
     * @param loc location to teleport to
     */
    public void teleport(Location loc) {
        this.loc = loc;
    }

    /**
     * Sets the velocity of the projectile
     *
     * @param vel new velocity
     */
    public void setVelocity(Vector vel) {
        this.vel = vel;
    }

    /**
     * Embeds data to the projectile
     *
     * @param data data to embed
     */
    public void embed(EmbedData data) {
        this.embed = data;
    }

    /**
     * @return embedded data
     */
    public EmbedData getEmbedData() {
        return embed;
    }

    /**
     * Updates the projectiles position and checks for collisions
     */
    @Override
    public void run() {
        List<LivingEntity> list = loc.getWorld().getLivingEntities();

        // Go through multiple steps to avoid tunneling
        for (int i = 0; i < steps; i++) {
            loc.add(vel);

            // Leaving a loaded chunk
            if (!loc.getChunk().isLoaded()) {
                cancel();
                return;
            }

            // Hitting a solid block
            if (loc.getBlock().getType().isSolid()) {
                cancel();
                ParticleHelper.fillSphere(loc, particle, data, 1, 10);
                if (embed != null) {
                    embed.getSkill().beginUsage();
                }
                plugin.getServer().getPluginManager().callEvent(new ParticleProjectileLandEvent(this));
                if (embed != null) {
                    embed.resolveNonTarget(loc);
                    embed.getSkill().stopUsage();
                }
                return;
            }

            // Hitting an enemy
            for (LivingEntity entity : list) {
                if (entity == shooter) continue;
                if (entity.getLocation().distanceSquared(loc) < 2.25) {
                    cancel();
                    ParticleHelper.fillSphere(entity.getLocation(), particle, data, 1, 10);
                    damaging = true;
                    if (embed != null) {
                        embed.getSkill().beginUsage();
                    }
                    plugin.getServer().getPluginManager().callEvent(new ParticleProjectileHitEvent(this, entity));
                    BukkitHelper.damage(entity, shooter, damage);
                    if (embed != null) {
                        embed.resolveNonTarget(entity.getLocation());
                        embed.resolveTarget(entity);
                        embed.getSkill().stopUsage();
                    }
                    damaging = false;
                    return;
                }
            }
        }

        // Particle along path
        count++;
        if (count >= delay) {
            count = 0;
            ParticleHelper.play(loc, particle, data);
        }
    }

    /**
     * Launches a particle projectile from the source
     *
     * @param source   entity to fire the projectile from
     * @param speed    speed to fire the projectile
     * @param particle particle to play for the effect
     * @param damage   damage to deal upon impact
     */
    public static ParticleProjectile launch(LivingEntity source, double speed, ParticleType particle, double damage) {
        return launch(source, speed, particle, 0, damage);
    }

    /**
     * Launches a particle projectile from the source
     *
     * @param source   entity to fire the projectile from
     * @param speed    speed to fire the projectile
     * @param particle particle to play for the effect
     * @param data     data value for the particle effect
     * @param damage   damage to deal upon impact
     */
    public static ParticleProjectile launch(LivingEntity source, double speed, ParticleType particle, int data, double damage) {
        return new ParticleProjectile(source, source.getLocation().add(0, 1, 0), source.getLocation().getDirection().multiply(speed / source.getLocation().getDirection().length()), particle, data, 2, damage);
    }
}
