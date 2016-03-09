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
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a flag is applied to an entity
 */
public class FlagApplyEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private LivingEntity entity;
    private String       flag;
    private boolean      cancelled;
    private int          ticks;

    /**
     * Constructor
     *
     * @param entity the entity the flag was on
     * @param flag   the flag that is to be applied
     */
    public FlagApplyEvent(LivingEntity entity, String flag, int ticks)
    {
        this.entity = entity;
        this.flag = flag;
        this.ticks = ticks;
        this.cancelled = false;
    }

    /**
     * Retrieves the entity that the flag is being applied to
     *
     * @return the entity having the flag applied to
     */
    public LivingEntity getEntity()
    {
        return entity;
    }

    /**
     * Retrieves the flag that is to be applied
     *
     * @return the flag being applied
     */
    public String getFlag()
    {
        return flag;
    }

    /**
     * Retrieves the number of ticks the flag is to be applied for
     *
     * @return the number of ticks
     */
    public int getTicks()
    {
        return ticks;
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
