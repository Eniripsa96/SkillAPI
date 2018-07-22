/**
 * SkillAPI
 * com.sucy.skill.listener.CastItemListener
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

import com.rit.sucy.player.PlayerUUIDs;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerClassChangeEvent;
import com.sucy.skill.api.event.PlayerSkillUnlockEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkillSlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.UUID;

/**
 * Handles the alternate casting option for casting via a cycling slot
 */
public class CastItemListener extends SkillAPIListener
{
    private static HashMap<UUID, PlayerSkillSlot> data = new HashMap<UUID, PlayerSkillSlot>();

    private static int slot = SkillAPI.getSettings().getCastSlot();

    @Override
    public void init()
    {
        MainListener.registerJoin(this::init);
        MainListener.registerClear(this::handleClear);
        for (Player player : Bukkit.getOnlinePlayers())
            init(player);
    }

    /**
     * Cleans up the listener functions
     */
    @Override
    public void cleanup()
    {
        if (slot == -1)
            return;

        for (Player player : Bukkit.getOnlinePlayers())
            cleanup(player);
        slot = -1;
    }

    private static void cleanup(Player player)
    {
        data.remove(player.getUniqueId());
        if (SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
            player.getInventory().setItem(slot, null);
    }

    /**
     * Re-initializes cast data on class change
     *
     * @param event event details
     */
    @EventHandler
    public void onClassChange(PlayerClassChangeEvent event)
    {
        data.get(event.getPlayerData().getPlayer().getUniqueId()).init(event.getPlayerData());
    }

    /**
     * Enables/disables cast when changing worlds
     *
     * @param event event details
     */
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event)
    {
        boolean from = SkillAPI.getSettings().isWorldEnabled(event.getFrom());
        boolean to = SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld());
        if (from && !to)
            event.getPlayer().getInventory().setItem(SkillAPI.getSettings().getCastSlot(), null);
        else
            init(event.getPlayer());
    }

    private PlayerSkillSlot get(Player player)
    {
        return data.get(player.getUniqueId());
    }

    private PlayerSkillSlot get(PlayerData data)
    {
        return this.data.get(PlayerUUIDs.getUUID(data.getPlayerName()));
    }

    /**
     * Gives the player the cast item
     *
     * @param player player to give to
     */
    private void init(Player player)
    {
        if (SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
        {
            PlayerSkillSlot slotData = new PlayerSkillSlot();
            data.put(player.getUniqueId(), slotData);
            slotData.init(SkillAPI.getPlayerData(player));

            PlayerInventory inv = player.getInventory();
            int slot = SkillAPI.getSettings().getCastSlot();
            ItemStack item = inv.getItem(slot);
            slotData.updateItem(player);
            if (item != null && item.getType() != Material.AIR)
                inv.addItem(item);
        }
    }

    /**
     * Removes the cast item on quit
     *
     * @param event event details
     */

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        cleanup(event.getPlayer());
    }

    /**
     * Adds unlocked skills to the skill bar if applicable
     *
     * @param event event details
     */
    @EventHandler
    public void onUnlock(PlayerSkillUnlockEvent event)
    {
        get(event.getPlayerData()).unlock(event.getUnlockedSkill());
    }

    /**
     * Prevents moving the cast item
     *
     * @param event event details
     */
    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        if (SkillAPI.getSettings().isWorldEnabled(event.getWhoClicked().getWorld())) {
            if (event.getSlot() == slot && event.getSlotType() == InventoryType.SlotType.QUICKBAR)
                event.setCancelled(true);
            else if (event.getAction() == InventoryAction.HOTBAR_SWAP
                    && event.getHotbarButton() == slot)
                event.setCancelled(true);
        }
    }

    /**
     * Casts a skill when dropping the cast item
     *
     * @param event event details
     */
    @EventHandler
    public void onDrop(PlayerDropItemEvent event)
    {
        if (SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld())
            && event.getPlayer().getInventory().getHeldItemSlot() == slot)
        {
            event.setCancelled(true);
            get(event.getPlayer()).activate();
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (SkillAPI.getSettings().isWorldEnabled(event.getEntity().getWorld())) {
            event.getDrops().remove(event.getEntity().getInventory().getItem(slot));
        }
    }

    /**
     * Cycles through skills upon interact
     *
     * @param event event details
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        // Cycling skills
        if (SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld())
            && event.getPlayer().getInventory().getHeldItemSlot() == slot)
        {
            event.setCancelled(true);
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
                get(event.getPlayer()).next();
            else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                get(event.getPlayer()).prev();
        }
    }

    private void handleClear(final Player player) {
        player.getInventory().setItem(slot, SkillAPI.getSettings().getCastItem());
    }
}
