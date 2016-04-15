/**
 * SkillAPI
 * com.sucy.skill.listener.CastListener
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
import com.sucy.skill.api.event.PlayerSkillUnlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listener for the main casting system
 */
public class CastListener implements Listener
{
    private static final HashMap<UUID, ItemStack[]> backup = new HashMap<UUID, ItemStack[]>();

    /**
     * Restores all player backups
     */
    public static void cleanup()
    {
        for (Map.Entry<UUID, ItemStack[]> entry : backup.entrySet())
        {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null)
                player.getInventory().setContents(entry.getValue());
        }
        backup.clear();
    }

    private int slot;

    /**
     * @param plugin API reference
     */
    public CastListener(SkillAPI plugin)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        slot = SkillAPI.getSettings().getCastSlot() - 1;
    }

    /**
     * Adds unlocked skills to the skill bar if applicable
     *
     * @param event event details
     */
    @EventHandler
    public void onUnlock(PlayerSkillUnlockEvent event)
    {
        if (event.getUnlockedSkill().getData().canCast() && event.getPlayerData().getPlayer() == null)
            event.getPlayerData().getCastBars().unlock(event.getUnlockedSkill());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        if (event.getInventory() == event.getWhoClicked().getInventory() && event.getSlot() == slot)
            event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event)
    {
        if (event.getPlayer().getInventory().getHeldItemSlot() == slot && SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
        {
            event.setCancelled(true);

            // Open skill organizer
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        if (event.getPlayer().getInventory().getHeldItemSlot() == slot && SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
        {
            event.setCancelled(true);
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
                SkillAPI.getPlayerData(event.getPlayer()).getCastBars().showHoverBar(event.getPlayer());
            else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                SkillAPI.getPlayerData(event.getPlayer()).getCastBars().showInstantBar(event.getPlayer());
        }
    }
}
