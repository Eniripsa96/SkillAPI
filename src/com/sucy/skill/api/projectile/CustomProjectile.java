package com.sucy.skill.api.projectile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

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
}
