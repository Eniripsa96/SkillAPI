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
import org.bukkit.util.Vector;

/**
 * <p>Represents a projectile that uses an item as the actual projectile.</p>
 */
public class ItemProjectile extends CustomProjectile
{
    private Item item;

    /**
     * <p>Constructs a new item projectile.</p>
     *
     * @param thrower the entity throwing the projectile
     * @param item    the item to represent the projectile
     * @param vel     the velocity of the projectile
     */
    public ItemProjectile(LivingEntity thrower, ItemStack item, Vector vel)
    {
        super(thrower);

        this.item = thrower.getWorld().dropItem(thrower.getLocation().add(0, 1, 0), item);
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


}
