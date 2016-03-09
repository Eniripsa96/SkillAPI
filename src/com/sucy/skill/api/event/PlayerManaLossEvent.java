/**
 * SkillAPI
 * com.sucy.skill.api.event.PlayerManaLossEvent
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

import com.sucy.skill.api.enums.ManaCost;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player regenerates mana via natural regeneration
 */
public class PlayerManaLossEvent extends Event implements Cancellable
{

    private static final HandlerList handlers = new HandlerList();
    private PlayerData player;
    private ManaCost   source;
    private boolean    cancelled;
    private double     amount;

    /**
     * Constructor
     *
     * @param player class of the player gaining experience
     * @param amount amount of mana regenerated
     * @param source the cause of the mana loss
     */
    public PlayerManaLossEvent(PlayerData player, double amount, ManaCost source)
    {
        this.player = player;
        this.source = source;
        this.amount = amount;
        cancelled = false;
    }

    /**
     * @return data of the player gaining experience
     */
    public PlayerData getPlayerData()
    {
        return player;
    }

    /**
     * @return amount of experience being gained
     */
    public double getAmount()
    {
        return amount;
    }

    /**
     * @return source of the gained mana
     */
    public ManaCost getSource()
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
    public void setAmount(double amount)
    {
        if (amount < 0)
        {
            throw new IllegalArgumentException("Regenerated mana cannot be negative");
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
