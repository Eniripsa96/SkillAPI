/**
 * SkillAPI
 * com.sucy.skill.api.event.PlayerGainSkillPointsEvent
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

import com.sucy.skill.api.enums.PointSource;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player gains class experience
 */
public class PlayerGainSkillPointsEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private PlayerClass playerClass;
    private boolean     cancelled;
    private PointSource source;
    private double      amount;

    /**
     * Constructor
     *
     * @param playerClass class of the player gaining skill points
     * @param amount      amount of skill points being gained
     * @param source      source of the points
     */
    public PlayerGainSkillPointsEvent(PlayerClass playerClass, double amount, PointSource source)
    {
        this.playerClass = playerClass;
        this.amount = amount;
        this.source = source;
        cancelled = false;
    }

    /**
     * @return data of the player gaining experience
     */
    public PlayerData getPlayerData()
    {
        return playerClass.getPlayerData();
    }

    /**
     * @return player's class that is receiving the experience
     */
    public PlayerClass getPlayerClass()
    {
        return playerClass;
    }

    /**
     * @return amount of experience being gained
     */
    public double getAmount()
    {
        return amount;
    }

    /**
     * @return where the experience came from
     */
    public PointSource getSource()
    {
        return source;
    }

    /**
     * Sets the amount of experience being gained
     *
     * @param amount new amount of experience
     *
     * @throws IllegalArgumentException if experience is less than 0
     */
    public void setAmount(int amount)
    {
        if (amount < 0)
        {
            throw new IllegalArgumentException("Points cannot be negative");
        }

        this.amount = amount;
    }

    /**
     * @return whether or not the gain in experience is cancelled
     */
    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    /**
     * Sets whether or not the gain in experience is cancelled
     *
     * @param cancelled true/false
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
