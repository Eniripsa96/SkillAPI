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
import com.sucy.skill.api.event.PlayerClassChangeEvent;
import com.sucy.skill.api.event.PlayerSkillUnlockEvent;
import com.sucy.skill.cast.PlayerCastBars;
import com.sucy.skill.thread.MainThread;
import com.sucy.skill.thread.ThreadTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Listener for the main casting system
 */
public class CastListener extends SkillAPIListener
{
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
     * Cleans up
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
        if (SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
            forceCleanup(player);
    }

    private static void forceCleanup(Player player) {
        SkillAPI.getPlayerData(player).getCastBars().restore(player);
        player.getInventory().setItem(slot, null);
    }

    @EventHandler
    public void onDamaged(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            SkillAPI.getPlayerData(player).getCastBars().restore(player);
        }
    }

    @EventHandler
    public void onClassChange(PlayerClassChangeEvent event)
    {
        event.getPlayerData().getCastBars().reset();
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event)
    {
        boolean from = SkillAPI.getSettings().isWorldEnabled(event.getFrom());
        boolean to = SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld());
        if (from && !to)
            forceCleanup(event.getPlayer());
        else
            init(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (SkillAPI.getSettings().isWorldEnabled(event.getEntity().getWorld())) {
            event.getDrops().remove(event.getEntity().getInventory().getItem(slot));
        }
    }

    private void init(Player player)
    {
        if (SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
        {
            PlayerInventory inv = player.getInventory();
            int slot = SkillAPI.getSettings().getCastSlot();
            ItemStack item = inv.getItem(slot);
            inv.setItem(slot, SkillAPI.getSettings().getCastItem());
            if (item != null && item.getType() != Material.AIR)
                inv.addItem(item);
            inv.getItem(slot).setAmount(1);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        cleanup(event.getPlayer());
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event)
    {
        SkillAPI.getPlayerData((Player) event.getPlayer()).getCastBars().handleOpen((Player) event.getPlayer());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event)
    {
        SkillAPI.getPlayerData((Player) event.getPlayer()).getCastBars().restore((Player) event.getPlayer());
        init((Player) event.getPlayer());
    }

    /**
     * Adds unlocked skills to the skill bar if applicable
     *
     * @param event event details
     */
    @EventHandler
    public void onUnlock(PlayerSkillUnlockEvent event)
    {
        if (event.getUnlockedSkill().getData().canCast() && event.getPlayerData().getPlayer() != null)
            event.getPlayerData().getCastBars().unlock(event.getUnlockedSkill());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        if (SkillAPI.getSettings().isWorldEnabled(event.getWhoClicked().getWorld())) {
            if (event.getSlot() == slot && event.getSlotType() == InventoryType.SlotType.QUICKBAR)
                event.setCancelled(true);
            else if ((event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD)
                    && event.getHotbarButton() == slot)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event)
    {
        if (!SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
            return;

        if (SkillAPI.getPlayerData(event.getPlayer()).getCastBars().handleInteract(event.getPlayer()))
        {
            event.getItemDrop().remove();
        }

        else if (event.getPlayer().getInventory().getHeldItemSlot() == slot)
        {
            event.getItemDrop().remove();
            MainThread.register(new OrganizerTask(event.getPlayer()));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        if (!SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
            return;

        PlayerCastBars bars = SkillAPI.getPlayerData(event.getPlayer()).getCastBars();

        // Interaction while in a view
        if (bars.isHovering())
            event.setCancelled(true);

            // Entering a view
        else if (event.getPlayer().getInventory().getHeldItemSlot() == slot)
        {
            event.setCancelled(true);
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
                bars.showHoverBar(event.getPlayer());
            else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                bars.showInstantBar(event.getPlayer());
        }
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent event)
    {
        SkillAPI.getPlayerData(event.getPlayer()).getCastBars().handle(event);
    }

    private void handleClear(final Player player) {
        player.getInventory().setItem(slot, SkillAPI.getSettings().getCastItem());
    }

    private class OrganizerTask extends ThreadTask
    {
        private Player player;

        public OrganizerTask(Player player)
        {
            this.player = player;
        }

        @Override
        public void run()
        {
            SkillAPI.getPlayerData(player).getCastBars().showOrganizer(player);
        }
    }
}
