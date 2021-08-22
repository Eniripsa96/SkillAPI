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
import com.sucy.skill.api.event.PlayerAccountChangeEvent;
import com.sucy.skill.api.event.PlayerClassChangeEvent;
import com.sucy.skill.api.event.PlayerSkillDowngradeEvent;
import com.sucy.skill.api.event.PlayerSkillUnlockEvent;
import com.sucy.skill.api.event.PlayerSkillUpgradeEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkillBar;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.util.ItemSerializer;
import com.sucy.skill.gui.handlers.SkillHandler;
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
    private final String ITEM_SAVE_KEY = "combatItems";

    private final HashMap<UUID, ItemStack[]> backup = new HashMap<UUID, ItemStack[]>();

    private final HashSet<UUID> ignored = new HashSet<UUID>();

    private int slot = SkillAPI.getSettings().getCastSlot();

    @Override
    public void init()
    {
        MainListener.registerJoin(this::init);
        MainListener.registerClear(this::handleClear);
        for (Player player : Bukkit.getOnlinePlayers())
            init(player);
    }

    private void init(Player player)
    {
        if (!SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
            return;

        PlayerData data = SkillAPI.getPlayerData(player);
        if (data.getExtraData().has(ITEM_SAVE_KEY)) {
            ItemStack[] items = ItemSerializer.fromBase64(data.getExtraData().getString(ITEM_SAVE_KEY));
            if (items != null)
                backup.put(player.getUniqueId(), items);
            else
                backup.put(player.getUniqueId(), new ItemStack[9]);
        }
        else
            backup.put(player.getUniqueId(), new ItemStack[9]);
        if (SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
        {
            PlayerInventory inv = player.getInventory();
            ItemStack item = inv.getItem(slot);
            inv.setItem(slot, SkillAPI.getSettings().getCastItem());
            if (item != null && item.getType() != Material.AIR) {
                for (ItemStack overflow : inv.addItem(item).values())
                    player.getWorld().dropItemNaturally(player.getLocation(), overflow);
            }
            inv.getItem(slot).setAmount(1);

            int playerSlot = player.getInventory().getHeldItemSlot();
            while (!data.getSkillBar().isWeaponSlot(playerSlot) || slot == playerSlot) {
                playerSlot = (playerSlot + 1) % 9;
            }
            if (playerSlot != player.getInventory().getHeldItemSlot()) {
                player.getInventory().setHeldItemSlot(playerSlot);
            }
        }
    }

    @Override
    public void cleanup() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (SkillAPI.getSettings().isWorldEnabled(player.getWorld())) {
                cleanup(player);
            }
        }
    }

    private void cleanup(Player player)
    {
        ignored.remove(player.getUniqueId());
        PlayerData data = SkillAPI.getPlayerData(player);
        PlayerSkillBar bar = data.getSkillBar();
        if (bar.isSetup())
            toggle(player);
        player.getInventory().setItem(slot, null);
        ItemStack[] restore = backup.remove(player.getUniqueId());
        data.getExtraData().set(ITEM_SAVE_KEY, ItemSerializer.toBase64(restore));
    }

    private void toggle(Player player)
    {
        ItemStack[] items = backup.get(player.getUniqueId());
        if (items == null) {
            items = new ItemStack[9];
        }
        ItemStack[] temp = new ItemStack[9];
        PlayerData data = SkillAPI.getPlayerData(player);
        for (int i = 0; i < items.length; i++) {
            if (i == slot)
                continue;
            if (data.getSkillBar().isSetup() && !data.getSkillBar().isWeaponSlot(i))
                continue;

            temp[i] = player.getInventory().getItem(i);
            player.getInventory().setItem(i, null);
        }
        backup.put(player.getUniqueId(), temp);
        if (data.getSkillBar().isSetup())
            data.getSkillBar().clear(player);
        else
            data.getSkillBar().setup(player);
        for (int i = 0; i < 9; i++) {
            if (items[i] != null)
                player.getInventory().setItem(i, items[i]);
        }
    }

    /**
     * Clears skill bars upon quitting the game
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event)
    {
        if (SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
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
     * Handles assigning skills to the skill bar
     *
     * @param event event details
     */
    @EventHandler
    public void onAssign(final InventoryClickEvent event) {
        if (event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD && event.getInventory().getHolder() instanceof SkillHandler) {
            final PlayerData data = SkillAPI.getPlayerData((Player) event.getWhoClicked());
            if (data.getSkillBar().isSetup() && !data.getSkillBar().isWeaponSlot(event.getHotbarButton())) {
                final SkillHandler handler = (SkillHandler) event.getInventory().getHolder();
                final Skill skill = handler.get(event.getSlot());
                if (skill != null && skill.canCast()) {
                    data.getSkillBar().assign(data.getSkill(skill.getName()), event.getHotbarButton());
                }
            }
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
        if (!SkillAPI.getSettings().isWorldEnabled(event.getEntity().getWorld()))
            return;
        if (event.getEntity().getWorld().getGameRuleValue("keepInventory").equals("true"))
            return;

        PlayerData data = SkillAPI.getPlayerData(event.getEntity());
        if (data.getSkillBar().isSetup()) {
            for (int i = 0; i < 9; i++) {
                if (!data.getSkillBar().isWeaponSlot(i))
                    event.getDrops().remove(event.getEntity().getInventory().getItem(i));
            }
            data.getSkillBar().clear(event.getEntity());
        }
        event.getDrops().remove(event.getEntity().getInventory().getItem(slot));
        event.getEntity().getInventory().setItem(slot, null);

        ItemStack[] hidden = backup.get(event.getEntity().getUniqueId());
        for (ItemStack item : hidden) {
            if (item != null) {
                event.getDrops().add(item);
            }
        }
        backup.put(event.getEntity().getUniqueId(), new ItemStack[9]);
    }

    /**
     * Sets the skill bar back up on respawn
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event)
    {
        if (!event.getPlayer().getWorld().getGameRuleValue("keepInventory").equals("true"))
            init(event.getPlayer());
    }

    @EventHandler
    public void onDupe(InventoryClickEvent event)
    {
        if (SkillAPI.getSettings().isWorldEnabled(event.getWhoClicked().getWorld())) {
            if (event.getSlot() == slot && event.getSlotType() == InventoryType.SlotType.QUICKBAR)
                event.setCancelled(true);
            else if ((event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD)
                    && event.getHotbarButton() == slot)
                event.setCancelled(true);
        }
    }

    /**
     * Event for assigning skills to the skill bar
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onToggle(InventoryClickEvent event)
    {
        // Must click on an active skill bar
        PlayerData data = SkillAPI.getPlayerData((Player) event.getWhoClicked());
        final PlayerSkillBar skillBar = data.getSkillBar();
        if (!skillBar.isSetup())
            return;

        if ((event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD)
                && (!skillBar.isWeaponSlot(event.getHotbarButton()) || !skillBar.isWeaponSlot(event.getSlot())))
        {
            event.setCancelled(true);
            return;
        }


        // Prevent moving skill icons
        int slot = event.getSlot();
        if (event.getSlot() < 9 && event.getRawSlot() > event.getView().getTopInventory().getSize())
        {
            if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.SHIFT_LEFT)
                event.setCancelled(!skillBar.isWeaponSlot(slot));
            else if ((event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT)
                && (!skillBar.isWeaponSlot(slot) || (skillBar.isWeaponSlot(slot) && (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR))))
            {
                event.setCancelled(true);
                skillBar.toggleSlot(slot);
            }
            else if (event.getAction().name().startsWith("DROP"))
                event.setCancelled(!skillBar.isWeaponSlot(slot));
        }
    }

    /**
     * Ignores the next cast upon changing worlds due to the forced slot
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChangeWorld(PlayerChangedWorldEvent event)
    {
        PlayerData data = SkillAPI.getPlayerData(event.getPlayer());
        boolean enabled = SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld());
        boolean wasEnabled = SkillAPI.getSettings().isWorldEnabled(event.getFrom());
        if (data.hasClass() && data.getSkillBar().isSetup() && enabled)
            ignored.add(event.getPlayer().getUniqueId());
        if (enabled && !wasEnabled)
            init(event.getPlayer());
        else if (!enabled && wasEnabled)
            cleanup(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChangeAccount(PlayerAccountChangeEvent event) {
        if (event.getPreviousAccount().getSkillBar().isSetup()) {
            toggle(event.getPreviousAccount().getPlayer());
        }
    }

    /**
     * Applies skill bar actions when pressing the number keys
     *
     * @param event event details
     */
    @EventHandler
    public void onCast(PlayerItemHeldEvent event)
    {
        if (!SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
            return;

        if (event.getNewSlot() == slot)
        {
            event.setCancelled(true);
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
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

    private void handleClear(final Player player) {
        backup.put(player.getUniqueId(), new ItemStack[9]);
        PlayerSkillBar skillBar = SkillAPI.getPlayerData(player).getSkillBar();
        if (skillBar.isSetup())
            skillBar.update(player);
        player.getInventory().setItem(slot, SkillAPI.getSettings().getCastItem());
    }
}
