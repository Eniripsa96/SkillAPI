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

import java.util.ArrayList;
import java.util.List;

/**
 * A fake projectile that plays particles along its path
 */
public class ParticleProjectile extends CustomProjectile
{
    /**
     * Settings key for the projectile speed
     */
    public static final String SPEED = "velocity";

    /**
     * Settings key for the projectile lifespan
     */
    public static final String LIFESPAN = "lifespan";

    /**
     * Settings key for the projectile's frequency of playing particles
     */
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
     * @param shooter  entity that shot the projectile
     * @param level    level to use for scaling the speed
     * @param loc      initial location of the projectile
     * @param settings settings for the projectile
     */
    public ParticleProjectile(LivingEntity shooter, int level, Location loc, Settings settings)
    {
        super(shooter);

        this.loc = loc;
        this.settings = settings;
        this.vel = loc.getDirection().multiply(settings.getAttr(SPEED, level, 1.0));
        this.freq = (int) (20 * settings.getDouble(FREQUENCY, 0.5));
        this.life = (int) (settings.getDouble(LIFESPAN, 10.0) * 20);

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

    /**
     * Fires a spread of projectiles from the location.
     *
     * @param shooter  entity shooting the projectiles
     * @param level    level to use for scaling the speed
     * @param center   the center direction of the spread
     * @param loc      location to shoot from
     * @param settings settings to use when firing
     * @param angle    angle of the spread
     * @param amount   number of projectiles to fire
     * @param callback optional callback for when projectiles hit
     *
     * @return list of fired projectiles
     */
    public static ArrayList<ParticleProjectile> spread(LivingEntity shooter, int level, Vector center, Location loc, Settings settings, double angle, int amount, ProjectileCallback callback)
    {
        ArrayList<Vector> dirs = calcSpread(center, angle, amount);
        ArrayList<ParticleProjectile> list = new ArrayList<ParticleProjectile>();
        for (Vector dir : dirs)
        {
            Location l = loc.clone();
            l.setDirection(dir);
            ParticleProjectile p = new ParticleProjectile(shooter, level, l, settings);
            p.setCallback(callback);
            list.add(p);
        }
        return list;
    }

    /**
     * Fires a spread of projectiles from the location.
     *
     * @param shooter  entity shooting the projectiles
     * @param level    level to use for scaling the speed
     * @param center   the center location to rain on
     * @param settings settings to use when firing
     * @param radius   radius of the circle
     * @param height   height above the center location
     * @param amount   number of projectiles to fire
     * @param callback optional callback for when projectiles hit
     *
     * @return list of fired projectiles
     */
    public static ArrayList<ParticleProjectile> rain(LivingEntity shooter, int level, Location center, Settings settings, double radius, double height, int amount, ProjectileCallback callback)
    {
        Vector vel = new Vector(0, 1, 0);
        ArrayList<Location> locs = calcRain(center, radius, height, amount);
        ArrayList<ParticleProjectile> list = new ArrayList<ParticleProjectile>();
        for (Location l : locs)
        {
            l.setDirection(vel);
            ParticleProjectile p = new ParticleProjectile(shooter, level, l, settings);
            p.setCallback(callback);
            list.add(p);
        }
        return list;
    }
}
