/**
 * SkillAPI
 * com.sucy.skill.api.event.FlagExpireEvent
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
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a flag expired on an entity
 */
public class FlagExpireEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private LivingEntity entity;
    private String       flag;
    private ExpireReason reason;

    /**
     * Constructor
     *
     * @param entity the entity the flag was on
     * @param flag   the flag that expired
     */
    public FlagExpireEvent(LivingEntity entity, String flag, ExpireReason reason)
    {
        this.entity = entity;
        this.flag = flag;
        this.reason = reason;
    }

    /**
     * Retrieves the entity that the flag was on
     *
     * @return the entity that the flag was on
     */
    public LivingEntity getEntity()
    {
        return entity;
    }

    /**
     * Retrieves the expired flag
     *
     * @return the flag that expired
     */
    public String getFlag()
    {
        return flag;
    }

    /**
     * @return reason for the flag expiring
     */
    public ExpireReason getReason()
    {
        return reason;
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

    /**
     * The reason the flag expired
     */
    public enum ExpireReason
    {
        // Expired due to running out of time
        TIME,

        // Expired due to being removed
        REMOVED
    }
}
