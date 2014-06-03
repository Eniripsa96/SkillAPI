package com.sucy.skill.api.util.effects;

import com.rit.sucy.player.Protection;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.ItemProjectileHitEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Represents a projectile that uses an item as the actual projectile.</p>
 */
public class ItemProjectile extends BukkitRunnable implements Metadatable {

    private HashMap<String, List<MetadataValue>> metadata = new HashMap<String, List<MetadataValue>>();
    private SkillAPI api;
    private LivingEntity thrower;
    private Item item;
    private double damage;

    /**
     * <p>Constructs a new item projectile.</p>
     *
     * @param thrower the entity throwing the projectile
     * @param item    the item to represent the projectile
     * @param vel     the velocity of the projectile
     * @param damage  the damage for the projectile to deal upon impact
     */
    public ItemProjectile(LivingEntity thrower, ItemStack item, Vector vel, double damage) {
        this.item = thrower.getWorld().dropItem(thrower.getLocation().add(0, 1, 0), item);
        this.item.setVelocity(vel);
        this.item.setPickupDelay(999999);
        this.thrower = thrower;
        this.damage = damage;

        api = (SkillAPI)Bukkit.getPluginManager().getPlugin("SkillAPI");
        runTaskTimer(api, 0, 1);
    }

    /**
     * <p>Updates the projectile's position.</p>
     * <p>This is for the repeating task and if you call it yourself, it
     * will move faster than it should.</p>
     */
    @Override
    public void run() {
        if (item.getLocation().add(0, -0.1, 0).getBlock().getType().isSolid()) {
            cancel();
            item.remove();
        }
        else {
            double halfSpeed = item.getVelocity().length() / 2;
            for (Entity entity : item.getNearbyEntities(halfSpeed, halfSpeed, halfSpeed)) {
                if (entity instanceof LivingEntity) {
                    LivingEntity target = (LivingEntity)entity;
                    if (Protection.canAttack(thrower, target)) {
                        item.remove();
                        cancel();
                        target.damage(damage, thrower);
                        ItemProjectileHitEvent event = new ItemProjectileHitEvent(this, target);
                        api.getServer().getPluginManager().callEvent(event);
                        return;
                    }
                }
            }
        }
    }

    /**
     * <p>Sets a bit of metadata onto the projectile.</p>
     *
     * @param key  the key for the metadata
     * @param meta the metadata to set
     */
    @Override
    public void setMetadata(String key, MetadataValue meta) {
        boolean hasMeta = hasMetadata(key);
        List<MetadataValue> list = hasMeta ? getMetadata(key) : new ArrayList<MetadataValue>();
        list.add(meta);
        if (!hasMeta) metadata.put(key, list);
    }

    /**
     * <p>Retrieves a metadata value from the projectile.</p>
     * <p>If no metadata was set with the key, this will instead return null</p>
     *
     * @param key the key for the metadata
     * @return    the metadata value
     */
    @Override
    public List<MetadataValue> getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * <p>Checks whether or not this has a metadata set for the key.</p>
     *
     * @param key the key for the metadata
     * @return    whether or not there is metadata set for the key
     */
    @Override
    public boolean hasMetadata(String key) {
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
    public void removeMetadata(String key, Plugin plugin) {
        metadata.remove(key);
    }
}
