/**
 * SkillAPI
 * com.sucy.skill.api.event.PlayerAccountChangeEvent
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

import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player changes classes
 */
public class PlayerAccountChangeEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private PlayerAccounts accounts;
    private boolean cancelled = false;
    private int prevId;
    private int newId;

    /**
     * Constructor
     *
     * @param accounts player accounts data
     * @param prevId   previous account ID
     * @param newId    new account ID
     */
    public PlayerAccountChangeEvent(PlayerAccounts accounts, int prevId, int newId)
    {
        this.accounts = accounts;
        this.prevId = prevId;
        this.newId = newId;
    }

    /**
     * @return player's account data
     */
    public PlayerAccounts getAccountData()
    {
        return accounts;
    }

    /**
     * @return account the player is switching from
     */
    public PlayerData getPreviousAccount()
    {
        return accounts.getData(prevId);
    }

    /**
     * @return account the player is switching to
     */
    public PlayerData getNewAccount()
    {
        return accounts.getData(newId);
    }

    /**
     * @return previously active account ID of the player
     */
    public int getPreviousId()
    {
        return prevId;
    }

    /**
     * @return new active account ID of the player
     */
    public int getNewID()
    {
        return newId;
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
     * Sets whether or not the switch should be cancelled
     *
     * @param cancelled cancelled state of the event
     */
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    /**
     * @return gets the handlers for the event
     */
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
