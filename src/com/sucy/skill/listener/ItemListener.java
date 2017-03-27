/**
 * SkillAPI
 * com.sucy.skill.listener.ItemListener
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

import com.rit.sucy.config.FilterType;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.language.ErrorNodes;
import com.sucy.skill.task.InventoryTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener that handles weapon item lore requirements
 */
public class ItemListener extends SkillAPIListener
{
    /**
     * Clear attribute buff data on quit
     *
     * @param event event details
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        InventoryTask.clear(event.getPlayer().getUniqueId());
    }

    /**
     * Cancels left clicks on disabled items
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onAttack(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Player)
        {
            Player player = (Player) event.getDamager();
            if (InventoryTask.cannotUse(SkillAPI.getPlayerData(player), player.getItemInHand()))
            {
                SkillAPI.getLanguage().sendMessage(ErrorNodes.CANNOT_USE, player, FilterType.COLOR);
                event.setCancelled(true);
            }
        }
        if (event.getEntity() instanceof Player && VersionManager.isVersionAtLeast(VersionManager.V1_9_0))
        {
            Player player = (Player) event.getEntity();
            if (event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) < 0
                && InventoryTask.cannotUse(SkillAPI.getPlayerData(player), player.getInventory().getItemInMainHand()))
            {
                SkillAPI.getLanguage().sendMessage(ErrorNodes.CANNOT_USE, event.getEntity(), FilterType.COLOR);
                event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0);
            }
        }
    }

    /**
     * Cancels firing a bow with a disabled weapon
     *
     * @param event event details
     */
    @EventHandler
    public void onShoot(EntityShootBowEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            if (InventoryTask.cannotUse(SkillAPI.getPlayerData((Player) event.getEntity()), event.getBow()))
            {
                SkillAPI.getLanguage().sendMessage(ErrorNodes.CANNOT_USE, event.getEntity(), FilterType.COLOR);
                event.setCancelled(true);
            }
        }
    }
}
