/**
 * SkillAPI
 * com.sucy.skill.listener.ClickListener
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
package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerCombos;
import com.sucy.skill.data.Click;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Handles transferring click actions by the player to
 * combos that cast skills.
 */
public class ClickListener extends SkillAPIListener
{
    /**
     * Registers clicks as they happen
     *
     * @param event event details
     */
    @EventHandler
    public void onClick(PlayerInteractEvent event)
    {

        // Get the history
        PlayerCombos combo = SkillAPI.getPlayerData(event.getPlayer()).getComboData();

        // Left clicks
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            combo.applyClick(Click.LEFT);
        }

        // Right clicks
        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
        {
            combo.applyClick(Click.RIGHT);
        }
    }

    /**
     * Registers shift clicks as they happen
     *
     * @param event event details
     */
    @EventHandler
    public void onShiftClick(PlayerToggleSneakEvent event)
    {
        if (event.isSneaking())
        {
            SkillAPI.getPlayerData(event.getPlayer()).getComboData().applyClick(Click.SHIFT);
        }
    }
}
