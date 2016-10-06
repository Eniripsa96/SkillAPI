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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

/**
 * Listener that handles weapon item lore requirements
 */
public class ItemListener extends SkillAPIListener
{
    /**
     * Removes weapon bonuses when dropped
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event)
    {
        if (SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
            SkillAPI.getPlayerData(event.getPlayer()).getEquips().clearWeapon();
    }

    /**
     * Updates player equips when an item breaks
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(PlayerItemBreakEvent event)
    {
        if (ARMOR.contains(event.getBrokenItem().getType()))
            check(event.getPlayer(), event.getBrokenItem().getType());
        else
            SkillAPI.schedule(new UpdateTask(event.getPlayer(), 0), 1);
    }

    /**
     * Updates equipment data on join
     *
     * @param event event details
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        if (SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
            SkillAPI.getPlayerData(event.getPlayer()).getEquips().update(event.getPlayer());
    }

    /**
     * Updates weapon on pickup
     * Clear attribute buff data on quit
     *
     * @param event event details
     */
    @EventHandler
    public void onPickup(PlayerPickupItemEvent event)
    {
        if (event.getPlayer().getItemInHand() == null)
            SkillAPI.schedule(new UpdateTask(event.getPlayer(), 0), 1);
    }

    /**
     * Update equips on world change into an active world
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onWorld(PlayerChangedWorldEvent event)
    {
        if (!SkillAPI.getSettings().isWorldEnabled(event.getFrom())
            && SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
            SkillAPI.getPlayerData(event.getPlayer()).getEquips().update(event.getPlayer());
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHeld(PlayerItemHeldEvent event)
    {
        SkillAPI.schedule(new UpdateTask(event.getPlayer(), 0), 1);
    }

    /**
     * Updates equip data when clicking on important slots
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event)
    {
        if (event.getSlotType() == InventoryType.SlotType.ARMOR)
            SkillAPI.schedule(new ArmorTask((Player) event.getWhoClicked(), 39 - event.getSlot()), 1);
        else if (event.getSlotType() == InventoryType.SlotType.QUICKBAR
            && event.getSlot() == event.getWhoClicked().getInventory().getHeldItemSlot())
            SkillAPI.schedule(new UpdateTask((Player) event.getWhoClicked(), 0), 1);
        else if (event.getSlotType() == InventoryType.SlotType.QUICKBAR
            && event.getSlot() == 9)
            SkillAPI.schedule(new UpdateTask((Player) event.getWhoClicked(), 1), 1);
        else if (event.getClick() == ClickType.SHIFT_LEFT)
        {
            if (event.getClickedInventory() == event.getWhoClicked().getInventory()
                && ARMOR.contains(event.getCurrentItem().getType()))
                check((Player)event.getWhoClicked(), event.getCurrentItem().getType());
            else if (event.getClickedInventory() != event.getWhoClicked().getInventory()
                && event.getWhoClicked().getItemInHand() == null)
                SkillAPI.schedule(new UpdateTask((Player) event.getWhoClicked(), 0), 1);
        }
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event)
    {
        Material type;
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
            && event.getPlayer().getItemInHand() != null
            && ARMOR.contains(type = event.getPlayer().getItemInHand().getType()))
        {
            check(event.getPlayer(), type);
            SkillAPI.schedule(new UpdateTask(event.getPlayer(), 0), 1);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event)
    {
        SkillAPI.schedule(new UpdateTask((Player)event.getWhoClicked(), 2), 1);
    }

    private void check(Player player, Material type)
    {
        String name = type.name();
        if (name.endsWith("HELMET"))
            SkillAPI.schedule(new ArmorTask(player, 0), 1);
        else if (name.endsWith("CHESTPLATE"))
            SkillAPI.schedule(new ArmorTask(player, 1), 1);
        else if (name.endsWith("LEGGINGS"))
            SkillAPI.schedule(new ArmorTask(player, 2), 1);
        else if (name.endsWith("BOOTS"))
            SkillAPI.schedule(new ArmorTask(player, 3), 1);
    }

    /**
     * Cancels left clicks on disabled items
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onAttack(EntityDamageByEntityEvent event)
    {
        if (!SkillAPI.getSettings().isWorldEnabled(event.getEntity().getWorld()))
            return;

        if (event.getDamager() instanceof Player)
        {
            Player player = (Player) event.getDamager();
            if (!SkillAPI.getPlayerData(player).getEquips().canHit())
            {
                SkillAPI.getLanguage().sendMessage(ErrorNodes.CANNOT_USE, player, FilterType.COLOR);
                event.setCancelled(true);
            }
        }
        if (event.getEntity() instanceof Player && VersionManager.isVersionAtLeast(VersionManager.V1_9_0))
        {
            Player player = (Player) event.getEntity();
            if (event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) < 0
                && SkillAPI.getPlayerData(player).getEquips().canHit())
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
    @EventHandler (priority = EventPriority.LOWEST)
    public void onShoot(EntityShootBowEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            if (!SkillAPI.getPlayerData((Player)event.getEntity()).getEquips().canHit())
            {
                SkillAPI.getLanguage().sendMessage(ErrorNodes.CANNOT_USE, event.getEntity(), FilterType.COLOR);
                event.setCancelled(true);
            }
        }
    }

    private static final HashSet<Material> ARMOR = new HashSet<Material>()
    {{
            add(Material.LEATHER_HELMET);
            add(Material.IRON_HELMET);
            add(Material.CHAINMAIL_HELMET);
            add(Material.GOLD_HELMET);
            add(Material.DIAMOND_HELMET);
            add(Material.LEATHER_CHESTPLATE);
            add(Material.IRON_CHESTPLATE);
            add(Material.CHAINMAIL_CHESTPLATE);
            add(Material.GOLD_CHESTPLATE);
            add(Material.DIAMOND_CHESTPLATE);
            add(Material.LEATHER_LEGGINGS);
            add(Material.IRON_LEGGINGS);
            add(Material.CHAINMAIL_LEGGINGS);
            add(Material.GOLD_LEGGINGS);
            add(Material.DIAMOND_LEGGINGS);
            add(Material.LEATHER_BOOTS);
            add(Material.IRON_BOOTS);
            add(Material.CHAINMAIL_BOOTS);
            add(Material.GOLD_BOOTS);
            add(Material.DIAMOND_BOOTS);
        }};

    /**
     * Handles updating equipped armor
     */
    private class ArmorTask extends BukkitRunnable
    {
        private Player player;
        private int    slot;

        /**
         * @param player player reference
         * @param slot   armor slot
         */
        public ArmorTask(Player player, int slot)
        {
            this.player = player;
            this.slot = slot;
        }

        /**
         * Applies the update
         */
        @Override
        public void run()
        {
            SkillAPI.getPlayerData(player).getEquips().updateArmor(player.getInventory(), slot);
        }
    }

    /**
     * Handles updating equipped armor
     */
    private class UpdateTask extends BukkitRunnable
    {
        private Player player;
        private int    type;

        /**
         * @param player player reference
         */
        public UpdateTask(Player player, int type)
        {
            this.player = player;
            this.type = type;
        }

        /**
         * Applies the update
         */
        @Override
        public void run()
        {
            if (type == 0)
                SkillAPI.getPlayerData(player).getEquips().updateWeapon(player.getInventory());
            else if (type == 1)
                SkillAPI.getPlayerData(player).getEquips().updateOffhand(player.getInventory());
            else
                SkillAPI.getPlayerData(player).getEquips().update(player);
        }
    }
}
