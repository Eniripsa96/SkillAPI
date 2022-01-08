/**
 * SkillAPI
 * com.sucy.skill.api.event.FlagApplyEvent
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
package com.sucy.skill.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.sucy.skill.api.util.BuffType;

/**
 * Event called when a flag is applied to an entity
 */
public class SkillBuffEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private LivingEntity caster;
    private LivingEntity target;
    private boolean cancelled;
    private int ticks;
    private BuffType type;
    private double amount;
    private boolean isMultiplier;

    /**
     * Constructor
     *
     * @param entity the entity the flag was on
     * @param flag   the flag that is to be applied
     */
    public SkillBuffEvent(LivingEntity caster, LivingEntity target, double amount, int ticks, BuffType type, boolean isMultiplier)
    {
    	this.caster = caster;
    	this.target = target;
    	this.amount = amount;
    	this.ticks = ticks;
    	this.type = type;
    	this.isMultiplier = isMultiplier;
        this.cancelled = false;
    }
    
    public LivingEntity getCaster() {
    	return caster;
    }
    
    public void setCaster(LivingEntity caster) {
    	this.caster = caster;
    }
    
    public LivingEntity getTarget() {
    	return caster;
    }
    
    public void setTarget(LivingEntity target) {
    	this.target = target;
    }

	public int getTicks() {
		return ticks;
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	public BuffType getType() {
		return type;
	}

	public void setType(BuffType type) {
		this.type = type;
	}

	public boolean isMultiplier() {
		return isMultiplier;
	}

	public void setMultiplier(boolean isMultiplier) {
		this.isMultiplier = isMultiplier;
	}
    
    public double getAmount() {
    	return this.amount;
    }
    
    public void setAmount(double amount) {
    	this.amount = amount;
    }

    /**
     * Checks whether or not the event is cancelled
     *
     * @return true if cancelled, false otherwise
     */
    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    /**
     * Sets whether or not the event is cancelled
     *
     * @param cancelled true if cancelled, false otherwise
     */
    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    /**
     * @return gets the handlers for the event
     */
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    /**
     * @return gets the handlers for the event
     */
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
