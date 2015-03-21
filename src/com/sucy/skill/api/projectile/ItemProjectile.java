package com.sucy.skill.api.projectile;

import com.rit.sucy.player.Protection;
import com.sucy.skill.api.event.ItemProjectileHitEvent;
import com.sucy.skill.api.event.ItemProjectileLandEvent;
import com.sucy.skill.api.event.ItemProjectileLaunchEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/**
 * <p>Represents a projectile that uses an item as the actual projectile.</p>
 */
public class ItemProjectile extends CustomProjectile
{
    private static final String NAME = "SkillAPI#";
    private static       int    NEXT = 0;

    private Item item;

    /**
     * <p>Constructs a new item projectile.</p>
     *
     * @param thrower the entity throwing the projectile
     * @param loc     the location to shoot from
     * @param item    the item to represent the projectile
     * @param vel     the velocity of the projectile
     */
    public ItemProjectile(LivingEntity thrower, Location loc, ItemStack item, Vector vel)
    {
        super(thrower);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(NAME + NEXT++);
        item.setItemMeta(meta);

        this.item = thrower.getWorld().dropItem(loc.add(0, 1, 0), item);
        this.item.setVelocity(vel);
        this.item.setPickupDelay(999999);

        Bukkit.getPluginManager().callEvent(new ItemProjectileLaunchEvent(this));
    }

    /**
     * Retrieves the location of the projectile
     *
     * @return location of the projectile
     */
    public Location getLocation()
    {
        return item.getLocation();
    }

    /**
     * <p>Updates the projectile's position.</p>
     * <p>This is for the repeating task and if you call it yourself, it
     * will move faster than it should.</p>
     */
    @Override
    public void run()
    {
        //if (item.getLocation().add(0, -0.2, 0).getBlock().getType().isSolid())
        if (item.isOnGround())
        {
            cancel();
            Bukkit.getPluginManager().callEvent(new ItemProjectileLandEvent(this));
            if (callback != null)
            {
                callback.callback(this, null);
            }
            item.remove();
        }
        else
        {
            double halfSpeed = item.getVelocity().length() / 2;
            for (Entity entity : item.getNearbyEntities(halfSpeed, halfSpeed, halfSpeed))
            {
                if (entity instanceof LivingEntity)
                {
                    LivingEntity target = (LivingEntity) entity;
                    if (Protection.canAttack(thrower, target))
                    {
                        cancel();
                        ItemProjectileHitEvent event = new ItemProjectileHitEvent(this, target);
                        Bukkit.getPluginManager().callEvent(event);
                        if (callback != null)
                        {
                            callback.callback(this, target);
                        }
                        item.remove();
                        return;
                    }
                }
            }
        }
    }

    /**
     * Fires a spread of projectiles from the location.
     *
     * @param shooter  entity shooting the projectiles
     * @param center   the center velocity of the spread
     * @param loc      location to shoot from
     * @param item     the item to use for the projectile
     * @param angle    angle of the spread
     * @param amount   number of projectiles to fire
     * @param callback optional callback for when projectiles hit
     *
     * @return list of fired projectiles
     */
    public static ArrayList<ItemProjectile> spread(LivingEntity shooter, Vector center, Location loc, ItemStack item, double angle, int amount, ProjectileCallback callback)
    {
        double speed = center.length();
        center.normalize();
        ArrayList<Vector> dirs = calcSpread(shooter.getLocation().getDirection(), angle, amount);
        ArrayList<ItemProjectile> list = new ArrayList<ItemProjectile>();
        for (Vector dir : dirs)
        {
            Vector vel = dir.multiply(speed);
            ItemProjectile p = new ItemProjectile(shooter, loc, item, vel);
            p.setCallback(callback);
            list.add(p);
        }
        return list;
    }

    /**
     * Fires a spread of projectiles from the location.
     *
     * @param shooter  entity shooting the projectiles
     * @param center   the center location to rain on
     * @param item     the item to use for the projectile
     * @param radius   radius of the circle
     * @param height   height above the center location
     * @param speed    speed of the projectiles
     * @param amount   number of projectiles to fire
     * @param callback optional callback for when projectiles hit
     *
     * @return list of fired projectiles
     */
    public static ArrayList<ItemProjectile> rain(LivingEntity shooter, Location center, ItemStack item, double radius, double height, double speed, int amount, ProjectileCallback callback)
    {
        Vector vel = new Vector(0, speed, 0);
        if (vel.getY() == 0)
        {
            vel.setY(1);
        }
        ArrayList<Location> locs = calcRain(center, radius, height, amount);
        ArrayList<ItemProjectile> list = new ArrayList<ItemProjectile>();
        for (Location l : locs)
        {
            l.setDirection(vel);
            ItemProjectile p = new ItemProjectile(shooter, l, item, vel);
            p.setCallback(callback);
            list.add(p);
        }
        return list;
    }
}
