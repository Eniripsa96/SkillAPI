package com.sucy.skill.api.projectile;

import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.ParticleProjectileExpireEvent;
import com.sucy.skill.api.event.ParticleProjectileHitEvent;
import com.sucy.skill.api.event.ParticleProjectileLandEvent;
import com.sucy.skill.api.event.ParticleProjectileLaunchEvent;
import com.sucy.skill.api.util.ParticleHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * A fake projectile that plays particles along its path
 */
public class ParticleProjectile extends CustomProjectile
{
    public static final String SPEED     = "velocity";
    public static final String LIFESPAN  = "lifespan";
    public static final String FREQUENCY = "frequency";

    private Location loc;
    private Settings settings;
    private Vector   vel;
    private int      steps;
    private int      count;
    private int      freq;
    private int      life;

    /**
     * Constructor
     *
     * @param thrower  entity that shot the projectile
     * @param loc      initial location of the projectile
     * @param settings settings for the projectile
     */
    public ParticleProjectile(LivingEntity thrower, Location loc, Settings settings)
    {
        super(thrower);

        this.loc = loc;
        this.settings = settings;
        this.vel = loc.getDirection().multiply(settings.get(SPEED, 3.0));
        this.freq = (int) (20 * settings.get(FREQUENCY, 0.5) * 20);
        this.life = (int) (settings.get(LIFESPAN, 10.0) * 20);

        steps = (int) Math.ceil(vel.length() * 2);
        vel.multiply(1.0 / steps);
        Bukkit.getPluginManager().callEvent(new ParticleProjectileLaunchEvent(this));
    }

    /**
     * Retrieves the location of the projectile
     *
     * @return location of the projectile
     */
    public Location getLocation()
    {
        return loc;
    }

    /**
     * @return velocity of the projectile
     */
    public Vector getVelocity()
    {
        return vel;
    }

    /**
     * Teleports the projectile to a location
     *
     * @param loc location to teleport to
     */
    public void teleport(Location loc)
    {
        this.loc = loc;
    }

    /**
     * Sets the velocity of the projectile
     *
     * @param vel new velocity
     */
    public void setVelocity(Vector vel)
    {
        this.vel = vel;
    }

    /**
     * Updates the projectiles position and checks for collisions
     */
    @Override
    public void run()
    {
        List<LivingEntity> list = loc.getWorld().getLivingEntities();

        // Go through multiple steps to avoid tunneling
        for (int i = 0; i < steps; i++)
        {
            loc.add(vel);

            // Leaving a loaded chunk
            if (!loc.getChunk().isLoaded())
            {
                cancel();
                Bukkit.getPluginManager().callEvent(new ParticleProjectileExpireEvent(this));
                return;
            }

            // Hitting a solid block
            if (loc.getBlock().getType().isSolid())
            {
                cancel();
                Bukkit.getPluginManager().callEvent(new ParticleProjectileLandEvent(this));
                if (callback != null)
                {
                    callback.callback(this, null);
                }
                return;
            }

            // Hitting an enemy
            for (LivingEntity entity : list)
            {
                if (entity == thrower)
                {
                    continue;
                }
                if (entity.getLocation().distanceSquared(loc) < 2.25)
                {
                    cancel();
                    Bukkit.getPluginManager().callEvent(new ParticleProjectileHitEvent(this, entity));
                    if (callback != null)
                    {
                        callback.callback(this, entity);
                    }
                    return;
                }
            }
        }

        // Particle along path
        count++;
        if (count >= freq)
        {
            count = 0;
            ParticleHelper.play(loc, settings);
        }

        // Lifespan
        life--;
        if (life <= 0)
        {
            cancel();
            Bukkit.getPluginManager().callEvent(new ParticleProjectileExpireEvent(this));
        }
    }

    /**
     * Sets the callback handler for the projectile
     *
     * @param callback callback handler
     */
    public void setCallback(ProjectileCallback callback)
    {
        this.callback = callback;
    }
}
