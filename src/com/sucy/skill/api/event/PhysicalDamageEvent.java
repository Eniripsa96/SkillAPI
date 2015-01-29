package com.sucy.skill.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PhysicalDamageEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private LivingEntity damager;
    private LivingEntity target;
    private double       damage;
    private boolean      projectile;
    private boolean      cancelled;

    public PhysicalDamageEvent(LivingEntity damager, LivingEntity target, double damage, boolean projectile)
    {
        this.damager = damager;
        this.target = target;
        this.damage = damage;
        this.projectile = projectile;
        this.cancelled = false;
    }

    public LivingEntity getDamager()
    {
        return damager;
    }

    public LivingEntity getTarget()
    {
        return target;
    }

    public double getDamage()
    {
        return damage;
    }

    public void setAmount(double amount)
    {
        damage = amount;
    }

    public boolean isProjectile()
    {
        return projectile;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
