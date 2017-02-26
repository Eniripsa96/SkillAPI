/**
 * SkillAPI
 * com.sucy.skill.listener.BarListener
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
import com.sucy.skill.api.event.PlayerClassChangeEvent;
import com.sucy.skill.api.event.PlayerSkillDowngradeEvent;
import com.sucy.skill.api.event.PlayerSkillUnlockEvent;
import com.sucy.skill.api.event.PlayerSkillUpgradeEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkillBar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Handles interactions with skill bars. This shouldn't be
 * use by other plugins as it is handled by the API.
 */
public class CastCombatListener extends SkillAPIListener
{
    private static final HashMap<UUID, ItemStack[]> backup = new HashMap<UUID, ItemStack[]>();

    private final HashSet<UUID> ignored = new HashSet<UUID>();

    private static int slot;

    @Override
    public void init()
    {
        slot = SkillAPI.getSettings().getCastSlot();

        for (Player player : Bukkit.getOnlinePlayers())
            init(player);
    }

    private void init(Player player)
    {
        backup.put(player.getUniqueId(), new ItemStack[9]);
        if (SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
        {
            PlayerInventory inv = player.getInventory();
            ItemStack item = inv.getItem(slot);
            inv.setItem(slot, SkillAPI.getSettings().getCastItem());
            if (item != null && item.getType() != Material.AIR)
                inv.addItem(item);
            inv.getItem(slot).setAmount(1);
        }
    }

    private static void cleanup(Player player)
    {
        if (SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
        {
            PlayerSkillBar bar = SkillAPI.getPlayerData(player).getSkillBar();
            if (bar.isSetup()) {
                toggle(player);
                bar.clear(player);
            }
            player.getInventory().setItem(slot, null);
            ItemStack[] restore = backup.get(player.getUniqueId());
            for (ItemStack item : restore)
                if (item != null)
                    player.getInventory().addItem(item);
        }
    }

    private static void toggle(Player player)
    {
        ItemStack[] items = backup.get(player.getUniqueId());
        ItemStack[] temp = new ItemStack[9];
        PlayerData data = SkillAPI.getPlayerData(player);
        for (int i = 0; i < 9; i++) {
            if (i == slot)
                continue;
            if (data.getSkillBar().isSetup() && !data.getSkillBar().isWeaponSlot(i))
                continue;

            temp[i] = player.getInventory().getItem(i);
            player.getInventory().setItem(i, null);
        }
        backup.put(player.getUniqueId(), temp);
        data.getSkillBar().toggleEnabled();
        for (int i = 0; i < 9; i++) {
            if (items[i] != null)
                player.getInventory().setItem(i, items[i]);
        }
    }

    /**
     * Sets up skill bars on joining
     *
     * @param event event details
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        init(event.getPlayer());
    }

    /**
     * Clears skill bars upon quitting the game
     *
     * @param event event details
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        cleanup(event.getPlayer());
    }

    /**
     * Manages setting up and clearing the skill bar when a player changes professions
     *
     * @param event event details
     */
    @EventHandler
    public void onProfess(PlayerClassChangeEvent event)
    {
        Player p = event.getPlayerData().getPlayer();

        if (event.getPreviousClass() != null && event.getNewClass() == null)
        {
            PlayerSkillBar bar = event.getPlayerData().getSkillBar();
            bar.reset();
            bar.clear(p);
        }
    }

    /**
     * Adds unlocked skills to the skill bar if applicable
     *
     * @param event event details
     */
    @EventHandler
    public void onUnlock(PlayerSkillUnlockEvent event)
    {
        if (!event.getUnlockedSkill().getData().canCast() || event.getPlayerData().getPlayer() == null)
        {
            return;
        }
        event.getPlayerData().getSkillBar().unlock(event.getUnlockedSkill());
    }

    /**
     * Updates the skill bar when a skill is upgraded
     *
     * @param event event details
     */
    @EventHandler
    public void onUpgrade(final PlayerSkillUpgradeEvent event)
    {
        final Player player = event.getPlayerData().getPlayer();
        if (player != null && event.getPlayerData().getSkillBar().isSetup())
        {
            SkillAPI.schedule(new Runnable()
            {
                @Override
                public void run()
                {
                    event.getPlayerData().getSkillBar().update(player);
                }
            }, 0);
        }
    }

    /**
     * Updates a player's skill bar when downgrading a skill to level 0
     *
     * @param event event details
     */
    @EventHandler
    public void onDowngrade(final PlayerSkillDowngradeEvent event)
    {
        if (event.getPlayerData().getSkillBar().isSetup()) {
            SkillAPI.schedule(new Runnable() {
                @Override
                public void run() {
                    SkillAPI.getPlayerData(event.getPlayerData().getPlayer())
                            .getSkillBar()
                            .update(event.getPlayerData().getPlayer());
                }
            }, 1);
        }
    }

    /**
     * Clears the skill bar on death
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event)
    {
        PlayerData data = SkillAPI.getPlayerData(event.getEntity());
        data.getSkillBar().clear(event.getEntity());
        event.getEntity().getInventory().setItem(slot, null);
    }

    /**
     * Sets the skill bar back up on respawn
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event)
    {
        init(event.getPlayer());
    }

    @EventHandler
    public void onDupe(InventoryClickEvent event)
    {
        if (event.getClickedInventory() == event.getWhoClicked().getInventory() && event.getSlot() == slot)
            event.setCancelled(true);
        else if (event.getAction() == InventoryAction.HOTBAR_SWAP
                && event.getHotbarButton() == slot)
            event.setCancelled(true);
    }

    /**
     * Event for assigning skills to the skill bar
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onToggle(InventoryClickEvent event)
    {
        // Must click on an active skill bar
        PlayerData data = SkillAPI.getPlayerData((Player) event.getWhoClicked());
        final PlayerSkillBar skillBar = data.getSkillBar();
        if (!skillBar.isSetup())
            return;

        if (event.getAction() == InventoryAction.HOTBAR_SWAP
                && !skillBar.isWeaponSlot(event.getHotbarButton()))
        {
            event.setCancelled(true);
            return;
        }

        // Prevent moving skill icons
        int slot = event.getSlot();
        if (event.getSlotType() == InventoryType.SlotType.QUICKBAR && slot < 9)
        {
            if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.SHIFT_LEFT)
                event.setCancelled(!skillBar.isWeaponSlot(slot));
            else if ((event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT)
                && (!skillBar.isWeaponSlot(slot) || (skillBar.isWeaponSlot(slot) && (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR))))
            {
                event.setCancelled(true);
                skillBar.toggleSlot(slot);
            }
        }
    }

    /**
     * Ignores the next cast upon changing worlds due to the forced slot
     *
     * @param event event details
     */
    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event)
    {
        PlayerData data = SkillAPI.getPlayerData(event.getPlayer());
        if (data.hasClass() && data.getSkillBar().isSetup() && SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
            ignored.add(event.getPlayer().getUniqueId());
    }

    /**
     * Applies skill bar actions when pressing the number keys
     *
     * @param event event details
     */
    @EventHandler
    public void onCast(PlayerItemHeldEvent event)
    {
        if (event.getNewSlot() == slot)
        {
            event.setCancelled(true);
            toggle(event.getPlayer());
            return;
        }

        // Must be a skill slot when the bar is set up
        PlayerData data = SkillAPI.getPlayerData(event.getPlayer());
        PlayerSkillBar bar = data.getSkillBar();
        if (!bar.isWeaponSlot(event.getNewSlot()) && bar.isSetup())
        {
            event.setCancelled(true);
            if (ignored.remove(event.getPlayer().getUniqueId()))
                return;
            bar.apply(event.getNewSlot());
        }
    }

    /**
     * Clears or sets up the skill bar upon changing from or to creative mode
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChangeMode(PlayerGameModeChangeEvent event)
    {
        // Clear on entering creative mode
        final PlayerData data = SkillAPI.getPlayerData(event.getPlayer());
        if (event.getNewGameMode() == GameMode.CREATIVE && data.getSkillBar().isSetup())
            toggle(data.getPlayer());
    }
}
