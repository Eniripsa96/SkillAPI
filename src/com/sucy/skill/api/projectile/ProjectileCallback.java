package com.sucy.skill.api.projectile;

import org.bukkit.entity.LivingEntity;

/**
 * Callback interface for receiving when/what a specific projectile hits
 */
public interface ProjectileCallback
{
    /**
     * The callback method for when a projectile hits something or lands
     * on the ground. This is not called when the projectile expires. If
     * the projectile landed on the ground without hitting anything, the
     * "hit" living entity will be null.
     *
     * @param projectile projectile calling back for
     * @param hit        the entity hit by the projectile, if any
     */
    public void callback(CustomProjectile projectile, LivingEntity hit);
}
