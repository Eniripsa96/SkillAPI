/**
 * SkillAPI
 * com.sucy.skill.api.projectile.ParticleProjectile
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import java.util.ArrayList;

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
    private static final String LIFESPAN = "lifespan";

    /**
     * Settings key for the projectile's frequency of playing particles
     */
    private static final String FREQUENCY = "frequency";

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
        this.life = (int) (settings.getDouble(LIFESPAN, 2) * 20);

        steps = (int) Math.ceil(vel.length() * 2);
        vel.multiply(1.0 / steps);
        Bukkit.getPluginManager().callEvent(new ParticleProjectileLaunchEvent(this));
    }

    /**
     * Retrieves the location of the projectile
     *
     * @return location of the projectile
     */
    @Override
    public Location getLocation()
    {
        return loc;
    }

    /**
     * Handles expiring due to range or leaving loaded chunks
     */
    @Override
    protected Event expire()
    {
        return new ParticleProjectileExpireEvent(this);
    }

    /**
     * Handles landing on terrain
     */
    @Override
    protected Event land()
    {
        return new ParticleProjectileLandEvent(this);
    }

    /**
     * Handles hitting an entity
     *
     * @param entity entity the projectile hit
     */
    @Override
    protected Event hit(LivingEntity entity)
    {
        return new ParticleProjectileHitEvent(this, entity);
    }

    /**
     * @return true if passing through a solid block, false otherwise
     */
    @Override
    protected boolean landed()
    {
        return getLocation().getBlock().getType().isSolid();
    }

    /**
     * @return squared radius for colliding
     */
    @Override
    protected double getCollisionRadius()
    {
        return 1.5;
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
        // Go through multiple steps to avoid tunneling
        for (int i = 0; i < steps; i++)
        {
            loc.add(vel);

            if (!isTraveling())
                return;

            checkCollision();
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
