/**
 * SkillAPI
 * com.sucy.skill.api.event.PlayerLevelUpEvent
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

import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player leveled up
 */
public class PlayerLevelUpEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private PlayerClass playerClass;
    private int         level;
    private int         amount;

    /**
     * Constructor
     *
     * @param playerClass data of the player leveling up
     */
    public PlayerLevelUpEvent(PlayerClass playerClass, int amount)
    {
        this.playerClass = playerClass;
        this.level = playerClass.getLevel();
        this.amount = amount;
    }

    /**
     * @return data of the player whose class leveled up
     */
    public PlayerData getPlayerData()
    {
        return playerClass.getPlayerData();
    }

    /**
     * @return the player's class that is leveling up
     */
    public PlayerClass getPlayerClass()
    {
        return playerClass;
    }

    /**
     * @return new level of the player's class
     */
    public int getLevel()
    {
        return level;
    }

    /**
     * @return how many levels the player's class gained
     */
    public int getAmount()
    {
        return amount;
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
