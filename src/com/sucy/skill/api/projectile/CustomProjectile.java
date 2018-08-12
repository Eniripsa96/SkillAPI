/**
 * SkillAPI
 * com.sucy.skill.api.projectile.CustomProjectile
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

import com.rit.sucy.reflect.Reflection;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.log.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Base class for custom projectiles
 */
public abstract class CustomProjectile extends BukkitRunnable implements Metadatable
{
    private static Constructor<?> aabbConstructor;
    private static Method         getEntities;
    private static Method         getBukkitEntity;
    private static Method         getHandle;

    static
    {
        try
        {
            Class<?> aabbClass = Reflection.getNMSClass("AxisAlignedBB");
            Class<?> entityClass = Reflection.getNMSClass("Entity");
            aabbConstructor = aabbClass.getConstructor(double.class, double.class, double.class, double.class, double.class, double.class);
            getEntities = Reflection.getNMSClass("World").getDeclaredMethod("getEntities", entityClass, aabbClass);
            getBukkitEntity = entityClass.getDeclaredMethod("getBukkitEntity");
            getHandle = Reflection.getCraftClass("CraftWorld").getDeclaredMethod("getHandle");
        }
        catch (Exception ex)
        {
            Logger.log("Unable to use reflection for accurate collision - will resort to simple radius check");
        }
    }

    private final HashMap<String, List<MetadataValue>> metadata = new HashMap<>();

    private final Set<Integer> hit = new HashSet<>();

    private ProjectileCallback callback;
    private LivingEntity       thrower;

    private boolean enemy = true;
    private boolean ally  = false;
    private boolean valid = true;

    /**
     * Constructs a new custom projectile and starts its timer task
     *
     * @param thrower entity firing the projectile
     */
    public CustomProjectile(LivingEntity thrower)
    {
        this.thrower = thrower;
        runTaskTimer(Bukkit.getPluginManager().getPlugin("SkillAPI"), 1, 1);
    }

    /**
     * Retrieves the location of the projectile
     *
     * @return location of the projectile
     */
    public abstract Location getLocation();

    /**
     * Handles expiring due to range or leaving loaded chunks
     */
    protected abstract Event expire();

    /**
     * Handles landing on terrain
     */
    protected abstract Event land();

    /**
     * Handles hitting an entity
     *
     * @param entity entity the projectile hit
     */
    protected abstract Event hit(LivingEntity entity);

    /**
     * @return true if the projectile has landed on terrain, false otherwise
     */
    protected abstract boolean landed();

    /**
     * @return squared radius for colliding
     */
    protected abstract double getCollisionRadius();

    /**
     * Checks whether or not the projectile is still valid.
     * Invalid would mean landing on the ground or leaving the loaded chunks.
     */
    protected boolean isTraveling()
    {
        // Leaving a loaded chunk
        if (!getLocation().getChunk().isLoaded())
        {
            cancel();
            Bukkit.getPluginManager().callEvent(expire());
            return false;
        }

        // Hitting a solid block
        if (landed())
        {
            this.applyLanded();
            return false;
        }

        return true;
    }

    public void applyLanded() {
        if (valid) {
            cancel();
            Bukkit.getPluginManager().callEvent(land());
            if (callback != null)
                callback.callback(this, null);
        }
    }

    /**
     * Checks if the projectile collides with a given list of entities
     */
    protected void checkCollision()
    {
        for (LivingEntity entity : getColliding())
        {
            if (entity == thrower || hit.contains(entity.getEntityId())) {
                continue;
            }
            hit.add(entity.getEntityId());

            boolean ally = SkillAPI.getSettings().isAlly(getShooter(), entity);
            if (ally && !this.ally) continue;
            if (!ally && !this.enemy) continue;

            cancel();
            Bukkit.getPluginManager().callEvent(hit(entity));

            if (callback != null)
                callback.callback(this, entity);

            return;
        }
    }

    /**
     * @return list of entities colliding with the projectile
     */
    private List<LivingEntity> getColliding()
    {
        // Reflection for nms collision
        List<LivingEntity> result = new ArrayList<>(1);
        try
        {
            Object nmsWorld = getHandle.invoke(getLocation().getWorld());
            Object list = getEntities.invoke(nmsWorld, null, getBoundingBox());
            for (Object item : (List) list)
            {
                Object bukkit = getBukkitEntity.invoke(item);
                if (bukkit instanceof LivingEntity) {
                    result.add((LivingEntity) bukkit);
                }
            }
        }
        // Fallback when reflection fails
        catch (Exception ex)
        {
            double radiusSq = getCollisionRadius();
            radiusSq *= radiusSq;
            for (LivingEntity entity : getNearbyEntities())
            {
                if (entity == thrower)
                    continue;

                if (getLocation().distanceSquared(entity.getLocation()) < radiusSq)
                    result.add(entity);
            }
        }
        return result;
    }

    /**
     * @return NMS bounding box of the projectile
     */
    private Object getBoundingBox() throws Exception
    {
        Location loc = getLocation();
        double rad = getCollisionRadius();
        return aabbConstructor.newInstance(
                loc.getX() - rad, loc.getY() - rad, loc.getZ() - rad,
                loc.getX() + rad, loc.getY() + rad, loc.getZ() + rad
        );
    }

    /**
     * @return list of nearby living entities
     */
    private List<LivingEntity> getNearbyEntities()
    {
        List<LivingEntity> list = new ArrayList<>();
        Location loc = getLocation();
        double radius = getCollisionRadius();
        int minX = (int) (loc.getX() - radius) >> 4;
        int maxX = (int) (loc.getX() + radius) >> 4;
        int minZ = (int) (loc.getZ() - radius) >> 4;
        int maxZ = (int) (loc.getZ() + radius) >> 4;
        for (int i = minX; i <= maxX; i++)
            for (int j = minZ; j < maxZ; j++)
                for (Entity entity : loc.getWorld().getChunkAt(i, j).getEntities())
                    if (entity instanceof LivingEntity)
                        list.add((LivingEntity) entity);
        return list;
    }

    /**
     * Sets whether or not the projectile can hit allies or enemies
     *
     * @param ally  whether or not allies can be hit
     * @param enemy whether or not enemies can be hit
     */
    public void setAllyEnemy(boolean ally, boolean enemy)
    {
        this.ally = ally;
        this.enemy = enemy;
    }

    /**
     * Retrieves the entity that shot the projectile
     *
     * @return the entity that shot the projectile
     */
    public LivingEntity getShooter()
    {
        return thrower;
    }

    /**
     * Marks the projectile as invalid when the associated task is cancelled
     */
    @Override
    public void cancel()
    {
        super.cancel();
        valid = false;
    }

    /**
     * <p>Sets a bit of metadata onto the projectile.</p>
     *
     * @param key  the key for the metadata
     * @param meta the metadata to set
     */
    @Override
    public void setMetadata(String key, MetadataValue meta)
    {
        boolean hasMeta = hasMetadata(key);
        List<MetadataValue> list = hasMeta ? getMetadata(key) : new ArrayList<>();
        list.add(meta);
        if (!hasMeta)
        {
            metadata.put(key, list);
        }
    }

    /**
     * <p>Retrieves a metadata value from the projectile.</p>
     * <p>If no metadata was set with the key, this will instead return null</p>
     *
     * @param key the key for the metadata
     *
     * @return the metadata value
     */
    @Override
    public List<MetadataValue> getMetadata(String key)
    {
        return metadata.get(key);
    }

    /**
     * <p>Checks whether or not this has a metadata set for the key.</p>
     *
     * @param key the key for the metadata
     *
     * @return whether or not there is metadata set for the key
     */
    @Override
    public boolean hasMetadata(String key)
    {
        return metadata.containsKey(key);
    }

    /**
     * <p>Removes a metadata value from the object.</p>
     * <p>If no metadata is set for the key, this will do nothing.</p>
     *
     * @param key    the key for the metadata
     * @param plugin plugin to remove the metadata for
     */
    @Override
    public void removeMetadata(String key, Plugin plugin)
    {
        metadata.remove(key);
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

    private static final Vector X_VEC = new Vector(1, 0, 0);
    private static final double DEGREE_TO_RAD = Math.PI / 180;
    private static final Vector vel = new Vector();

    /**
     * Calculates the directions for projectiles spread from
     * the centered direction using the given angle and
     * number of projectiles to be fired.
     *
     * @param dir    center direction of the spread
     * @param angle  angle which to spread at
     * @param amount amount of directions to calculate
     *
     * @return the list of calculated directions
     */
    public static ArrayList<Vector> calcSpread(Vector dir, double angle, int amount)
    {
        // Special cases
        if (amount <= 0)
        {
            return new ArrayList<>();
        }

        ArrayList<Vector> list = new ArrayList<>();

        // One goes straight if odd amount
        if (amount % 2 == 1)
        {
            list.add(dir);
            amount--;
        }

        if (amount <= 0)
        {
            return list;
        }

        // Get the base velocity
        Vector base = dir.clone();
        base.setY(0);
        base.normalize();
        vel.setX(1);
        vel.setY(0);
        vel.setZ(0);

        // Get the vertical angle
        double vBaseAngle = Math.acos(Math.max(-1, Math.min(base.dot(dir), 1)));
        if (dir.getY() < 0)
        {
            vBaseAngle = -vBaseAngle;
        }
        double hAngle = Math.acos(Math.max(-1, Math.min(1, base.dot(X_VEC)))) / DEGREE_TO_RAD;
        if (dir.getZ() < 0)
        {
            hAngle = -hAngle;
        }

        // Calculate directions
        double angleIncrement = angle / (amount - 1);
        for (int i = 0; i < amount / 2; i++)
        {
            for (int direction = -1; direction <= 1; direction += 2)
            {
                // Initial calculations
                double bonusAngle = angle / 2 * direction - angleIncrement * i * direction;
                double totalAngle = hAngle + bonusAngle;
                double vAngle = vBaseAngle * Math.cos(bonusAngle * DEGREE_TO_RAD);
                double x = Math.cos(vAngle);

                // Get the velocity
                vel.setX(x * Math.cos(totalAngle * DEGREE_TO_RAD));
                vel.setY(Math.sin(vAngle));
                vel.setZ(x * Math.sin(totalAngle * DEGREE_TO_RAD));

                // Launch the projectile
                list.add(vel.clone());
            }
        }

        return list;
    }

    /**
     * Calculates the locations to spawn projectiles to rain them down
     * over a given location.
     *
     * @param loc    the center location to rain on
     * @param radius radius of the circle
     * @param height height above the target to use
     * @param amount amount of locations to calculate
     *
     * @return list of locations to spawn projectiles
     */
    public static ArrayList<Location> calcRain(Location loc, double radius, double height, int amount)
    {
        ArrayList<Location> list = new ArrayList<>();
        if (amount <= 0)
        {
            return list;
        }
        loc.add(0, height, 0);

        // One would be in the center
        list.add(loc);
        amount--;

        // Calculate locations
        int tiers = (amount + 7) / 8;
        for (int i = 0; i < tiers; i++)
        {
            double rad = radius * (tiers - i) / tiers;
            int tierNum = Math.min(amount, 8);
            double increment = 360 / tierNum;
            double angle = (i % 2) * 22.5;
            for (int j = 0; j < tierNum; j++)
            {
                double dx = Math.cos(angle) * rad;
                double dz = Math.sin(angle) * rad;
                Location l = loc.clone();
                l.add(dx, 0, dz);
                list.add(l);
                angle += increment;
            }
            amount -= tierNum;
        }

        return list;
    }
}
