/**
 * SkillAPI
 * com.sucy.skill.api.event.PlayerCastSkillEvent
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

import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCastSkillEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private PlayerData  playerData;
    private PlayerSkill skill;
    private Player      player;
    private double      manaCost;
    private boolean     cancelled;

    public PlayerCastSkillEvent(PlayerData playerData, PlayerSkill skill, Player player)
    {
        this.playerData = playerData;
        this.skill = skill;
        this.player = player;
        this.manaCost = skill.getManaCost();
        this.cancelled = false;
    }

    public Player getPlayer()
    {
        return player;
    }

    public PlayerData getPlayerData()
    {
        return playerData;
    }

    public PlayerSkill getSkill()
    {
        return skill;
    }

    public double getManaCost() {
        return manaCost;
    }

    public void setManaCost(final double manaCost) {
        this.manaCost = manaCost;
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
