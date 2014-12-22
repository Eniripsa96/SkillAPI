package com.sucy.skill.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkillDamageEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private LivingEntity caster;
    private LivingEntity target;
    private double       damage;
    private boolean      cancelled;

    public SkillDamageEvent(LivingEntity caster, LivingEntity target, double damage)
    {
        this.caster = caster;
        this.target = target;
        this.damage = damage;
        this.cancelled = false;
    }

    public LivingEntity getCaster()
    {
        return caster;
    }

    public LivingEntity getTarget()
    {
        return target;
    }

    public double getAmount()
    {
        return damage;
    }

    public void setAmount(double amount)
    {
        damage = amount;
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
