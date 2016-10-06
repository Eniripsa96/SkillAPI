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

import com.rit.sucy.gui.MapData;
import com.rit.sucy.gui.MapMenu;
import com.rit.sucy.gui.MapMenuManager;
import com.rit.sucy.items.InventoryManager;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerClassChangeEvent;
import com.sucy.skill.api.event.PlayerSkillDowngradeEvent;
import com.sucy.skill.api.event.PlayerSkillUnlockEvent;
import com.sucy.skill.api.event.PlayerSkillUpgradeEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkillBar;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.gui.SkillDetailMenu;
import com.sucy.skill.gui.SkillListMenu;
import com.sucy.skill.tree.basic.InventoryTree;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

/**
 * Handles interactions with skill bars. This shouldn't be
 * use by other plugins as it is handled by the API.
 */
public class BarListener implements Listener
{
    public BarListener()
    {
        for (Player player : VersionManager.getOnlinePlayers())
        {
            PlayerData data = SkillAPI.getPlayerData(player);
            if (data.hasClass())
                data.getSkillBar().setup(player);
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
        PlayerData data = SkillAPI.getPlayerData(event.getPlayer());
        if (data.hasClass())
        {
            data.getSkillBar().setup(event.getPlayer());
        }
    }

    /**
     * Clears skill bars upon quitting the game
     *
     * @param event event details
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        PlayerData data = SkillAPI.getPlayerData(event.getPlayer());
        if (data.hasClass())
        {
            data.getSkillBar().clear(event.getPlayer());
        }
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

        // Professing as a first class sets up the skill bar
        if (event.getPreviousClass() == null && event.getNewClass() != null)
        {
            PlayerSkillBar bar = event.getPlayerData().getSkillBar();
            if (!bar.isSetup())
            {
                bar.setup(p);
            }
        }

        // Resetting your class clears the skill bar
        else if (event.getPreviousClass() != null && event.getNewClass() == null)
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
    public void onUpgrade(PlayerSkillUpgradeEvent event)
    {
        final Player player = event.getPlayerData().getPlayer();
        if (player != null)
        {
            SkillAPI.schedule(new Runnable()
            {
                @Override
                public void run()
                {
                    SkillAPI.getPlayerData(player).getSkillBar().update(player);
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
        SkillAPI.schedule(new Runnable()
        {
            @Override
            public void run()
            {
                SkillAPI.getPlayerData(event.getPlayerData().getPlayer()).getSkillBar().update(event.getPlayerData().getPlayer());
            }
        }, 1);

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
        if (data.hasClass())
            data.getSkillBar().clear(event);
    }

    /**
     * Sets the skill bar back up on respawn
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event)
    {
        PlayerData data = SkillAPI.getPlayerData(event.getPlayer());
        if (data.hasClass())
        {
            data.getSkillBar().setup(event.getPlayer());
            data.getSkillBar().update(event.getPlayer());
        }
    }

    /**
     * Event for assigning skills to the skill bar
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAssign(InventoryClickEvent event)
    {
        // Players without a class aren't effected
        PlayerData data = SkillAPI.getPlayerData((Player) event.getWhoClicked());
        if (!data.hasClass())
            return;

        // Disabled skill bars aren't affected either
        final PlayerSkillBar skillBar = data.getSkillBar();
        if (!skillBar.isSetup())
            return;

        boolean top = event.getRawSlot() < event.getView().getTopInventory().getSize();

        // Prevent moving skill icons
        if (event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD)
        {
            if (!skillBar.isWeaponSlot(event.getHotbarButton()) || !skillBar.isWeaponSlot(event.getSlot()))
                event.setCancelled(true);
        }
        else if (!top && event.getSlot() < 9)
        {
            int slot = event.getSlot();
            if (slot < 9 && slot >= 0)
            {
                if (!skillBar.isWeaponSlot(slot))
                {
                    event.setCancelled(true);
                }
                if (event.getClick() == ClickType.RIGHT)
                {
                    if (!skillBar.isWeaponSlot(slot) || (skillBar.isWeaponSlot(slot) && (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)))
                    {
                        event.setCancelled(true);
                        skillBar.toggleSlot(slot);
                    }
                }
            }
        }

        // Make sure it's the right type of click action
        if (event.getAction() != InventoryAction.HOTBAR_MOVE_AND_READD && event.getAction() != InventoryAction.HOTBAR_SWAP)
            return;

        // Must be a skill tree
        if (InventoryManager.isMatching(event.getInventory(), InventoryTree.INVENTORY_KEY))
        {
            InventoryTree tree = (InventoryTree) SkillAPI.getClass(data.getShownClassName()).getSkillTree();

            // Must be hovering over a skill
            if (tree.isSkill(event.getWhoClicked(), event.getRawSlot()))
            {
                Skill skill = tree.getSkill(event.getRawSlot());

                // Must be an active skill
                if (skill.canCast())
                {
                    // Assign the skill if the player has it
                    if (data.hasSkill(skill.getName()))
                    {
                        skillBar.assign(data.getSkill(skill.getName()), event.getHotbarButton());
                    }
                }
            }
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
        // Doesn't do anything without a class
        PlayerData data = SkillAPI.getPlayerData(event.getPlayer());
        if (!data.hasClass())
        {
            return;
        }

        // Must be a skill slot when the bar is set up
        PlayerSkillBar bar = data.getSkillBar();
        if (!bar.isWeaponSlot(event.getNewSlot()) && bar.isSetup())
        {
            event.setCancelled(true);

            MapData held = MapMenuManager.getActiveMenuData(event.getPlayer());
            if (held != null)
            {
                MapMenu menu = held.getMenu(event.getPlayer());
                if (menu instanceof SkillListMenu || menu instanceof SkillDetailMenu)
                {
                    bar.assign(SkillListMenu.getSkill(event.getPlayer()), event.getNewSlot());
                }
            }
            else
            {
                bar.apply(event.getNewSlot());
            }
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
        if (event.getNewGameMode() == GameMode.CREATIVE && data.hasClass())
        {
            data.getSkillBar().clear(event.getPlayer());
        }

        // Setup on leaving creative mode
        else if (event.getPlayer().getGameMode() == GameMode.CREATIVE && data.hasClass())
        {
            final Player player = event.getPlayer();
            SkillAPI.schedule(new Runnable()
            {
                @Override
                public void run()
                {
                    SkillAPI.getPlayerData(player).getSkillBar().setup(player);
                }
            }, 0);
        }
    }
}
