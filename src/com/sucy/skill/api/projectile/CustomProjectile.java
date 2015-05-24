package com.sucy.skill.api.projectile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Base class for custom projectiles
 */
public abstract class CustomProjectile extends BukkitRunnable implements Metadatable
{
    private final HashMap<String, List<MetadataValue>> metadata = new HashMap<String, List<MetadataValue>>();

    protected ProjectileCallback callback;
    protected LivingEntity       thrower;
    protected boolean  enemy = true;
    protected boolean  ally = false;

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
     * Retrieves the location of the projectile
     *
     * @return location of the projectile
     */
    public abstract Location getLocation();

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
        List<MetadataValue> list = hasMeta ? getMetadata(key) : new ArrayList<MetadataValue>();
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

    private static final Vector X_VEC         = new Vector(1, 0, 0);
    private static final double DEGREE_TO_RAD = Math.PI / 180;
    private static final Vector vel           = new Vector();

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
            return new ArrayList<Vector>();
        }

        ArrayList<Vector> list = new ArrayList<Vector>();

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
        vel.setX(1);
        vel.setY(0);
        vel.setZ(0);

        // Get the vertical angle
        double vBaseAngle = base.angle(dir);
        if (dir.getY() < 0)
        {
            vBaseAngle = -vBaseAngle;
        }
        double hAngle = base.angle(X_VEC) / DEGREE_TO_RAD;
        if (base.getZ() < 0)
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
        ArrayList<Location> list = new ArrayList<Location>();
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
