/**
 * SkillAPI
 * com.sucy.skill.api.event.PlayerClassChangeEvent
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

import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player changes classes
 */
public class PlayerClassChangeEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private PlayerClass playerClass;
    private RPGClass    previousClass;
    private RPGClass    newClass;

    /**
     * Constructor
     *
     * @param playerClass   data of the player changing classes
     * @param previousClass previous class of the player (null if wasn't a profession)
     * @param newClass      new class of the player (null if using the reset command)
     */
    public PlayerClassChangeEvent(PlayerClass playerClass, RPGClass previousClass, RPGClass newClass)
    {
        this.playerClass = playerClass;
        this.previousClass = previousClass;
        this.newClass = newClass;
    }

    /**
     * @return modified player class
     */
    public PlayerClass getPlayerClass()
    {
        return playerClass;
    }

    /**
     * @return Data of the player changing classes
     */
    public PlayerData getPlayerData()
    {
        return playerClass.getPlayerData();
    }

    /**
     * @return previous class of the player
     */
    public RPGClass getPreviousClass()
    {
        return previousClass;
    }

    /**
     * @return new class of the player
     */
    public RPGClass getNewClass()
    {
        return newClass;
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
