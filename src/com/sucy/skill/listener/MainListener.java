/**
 * SkillAPI
 * com.sucy.skill.listener.MainListener
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

import com.rit.sucy.version.VersionManager;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.event.PhysicalDamageEvent;
import com.sucy.skill.api.event.PlayerLevelUpEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.util.BuffManager;
import com.sucy.skill.api.util.Combat;
import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.data.Permissions;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.dynamic.mechanic.ImmunityMechanic;
import com.sucy.skill.manager.ClassBoardManager;
import com.sucy.skill.task.InventoryTask;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The main listener for SkillAPI  that handles general mechanics
 * such as loading/clearing data, controlling experience gains, and
 * enabling/disabling passive abilities.
 */
public class MainListener extends SkillAPIListener
{
    private static final List<Consumer<Player>> JOIN_HANDLERS = new ArrayList<>();
    private static final List<Consumer<Player>> CLEAR_HANDLERS = new ArrayList<>();

    public static void registerJoin(final Consumer<Player> joinHandler) {
        JOIN_HANDLERS.add(joinHandler);
    }

    public static void registerClear(final Consumer<Player> joinHandler) {
        CLEAR_HANDLERS.add(joinHandler);
    }

    @Override
    public void cleanup() {
        JOIN_HANDLERS.clear();
    }

    /**
     * Loads player data asynchronously when a player tries to log in
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        final OfflinePlayer player;
        if (VersionManager.isVersionAtLeast(VersionManager.V1_7_5))
            player = Bukkit.getOfflinePlayer(event.getUniqueId());
        else
            player = VersionManager.getOfflinePlayer(event.getName());

        if (SkillAPI.getSettings().isUseSql() && SkillAPI.getSettings().getSqlDelay() > 0)
            SkillAPI.initFakeData(player);
        else
            SkillAPI.loadPlayerData(player);
    }

    /**
     * Starts passives and applies class data when a player logs in.
     */
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (player.hasMetadata("NPC") || !SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
            return;

        final int delay = Math.min(100, SkillAPI.getSettings().getSqlDelay());
        if (SkillAPI.getSettings().isUseSql() && delay > 0) {
            SkillAPI.schedule(() -> {
                SkillAPI.reloadPlayerData(player);
                init(player);
            }, delay);
        } else {
            init(player);
        }
    }

    private void init(final Player player) {
        final PlayerData data = SkillAPI.getPlayerData(player);
        data.init(player);
        data.autoLevel();
        data.updateScoreboard();
        JOIN_HANDLERS.forEach(handler -> handler.accept(player));
    }

    /**
     * Saves player data when they log out and stops passives
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event)
    {
        unload(event.getPlayer());
    }

    /**
     * Unloads a player's data from the server
     *
     * @param player player to unload
     */
    public static void unload(Player player)
    {
        if (player.hasMetadata("NPC"))
            return;

        PlayerData data = SkillAPI.getPlayerData(player);
        if (SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
        {
            data.record(player);
            data.stopPassives(player);
        }

        FlagManager.clearFlags(player);
        BuffManager.clearData(player);
        Combat.clearData(player);
        DynamicSkill.clearCastData(player);
        InventoryTask.clear(player.getUniqueId());

        player.setDisplayName(player.getName());
        if (VersionManager.isVersionAtLeast(VersionManager.V1_9_0)) {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        } else {
            player.setMaxHealth(20);
        }
        player.setWalkSpeed(0.2f);
        SkillAPI.unloadPlayerData(player);
    }

    /**
     * Stops passives an applies death penalties when a player dies.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event)
    {
        FlagManager.clearFlags(event.getEntity());
        BuffManager.clearData(event.getEntity());
        DynamicSkill.clearCastData(event.getEntity());

        if (event.getEntity().hasMetadata("NPC"))
            return;

        PlayerData data = SkillAPI.getPlayerData(event.getEntity());
        if (data.hasClass() && SkillAPI.getSettings().isWorldEnabled(event.getEntity().getWorld()))
        {
            data.stopPassives(event.getEntity());
            data.loseExp();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(final EntityDeathEvent event) {
        DynamicSkill.clearCastData(event.getEntity());
        FlagManager.clearFlags(event.getEntity());
        BuffManager.clearData(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUnload(final ChunkUnloadEvent event) {
        for (final Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                final LivingEntity livingEntity = (LivingEntity) entity;
                DynamicSkill.clearCastData(livingEntity);
                FlagManager.clearFlags(livingEntity);
                BuffManager.clearData(livingEntity);
            }
        }
    }

    /**
     * Handles experience when a block is broken
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event)
    {
        if (event.getPlayer().hasMetadata("NPC"))
            return;

        Player player = event.getPlayer();
        if (SkillAPI.getSettings().isUseOrbs() && player != null && SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
            SkillAPI.getPlayerData(player).giveExp(event.getExpToDrop(), ExpSource.BLOCK_BREAK);
    }

    /**
     * Handles experience when ore is smelted in a furnace
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSmelt(FurnaceExtractEvent event)
    {
        Player player = event.getPlayer();
        if (SkillAPI.getSettings().isUseOrbs() && player != null && SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
            SkillAPI.getPlayerData(player).giveExp(event.getExpToDrop(), ExpSource.SMELT);
    }

    /**
     * Handles experience when a Bottle o' Enchanting breaks
     *
     * @param event event details
     */
    @EventHandler
    public void onExpBottleBreak(ExpBottleEvent event)
    {
        if (!(event.getEntity().getShooter() instanceof Player) || !SkillAPI.getSettings().isWorldEnabled(((Player) event.getEntity().getShooter()).getWorld()))
            return;

        Player player = (Player) event.getEntity().getShooter();
        if (player.hasMetadata("NPC"))
            return;

        if (SkillAPI.getSettings().isUseOrbs())
            SkillAPI.getPlayerData(player).giveExp(event.getExperience(), ExpSource.EXP_BOTTLE);
    }

    /**
     * Prevents experience orbs from modifying the level bar when it
     * is used for displaying class level.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExpChange(PlayerExpChangeEvent event)
    {
        // Prevent it from changing the level bar when that is being used to display class level
        if (!SkillAPI.getSettings().getLevelBar().equalsIgnoreCase("none")
                && event.getPlayer().hasPermission(Permissions.EXP)
                && SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
        {
            event.setAmount(0);
        }
    }

    /**
     * Handles updating level displays for players
     *
     * @param event event details
     */
    @EventHandler
    public void onLevelUp(final PlayerLevelUpEvent event) {
        if (SkillAPI.getSettings().isShowClassLevel()) {
            ClassBoardManager.updateLevel(event.getPlayerData());
        }
    }

    /**
     * Starts passive abilities again after respawning
     *
     * @param event event details
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event)
    {
        if (event.getPlayer().hasMetadata("NPC"))
            return;

        PlayerData data = SkillAPI.getPlayerData(event.getPlayer());
        if (data.hasClass() && SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
        {
            data.startPassives(event.getPlayer());
            data.updateScoreboard();
        }
    }

    /**
     * Damage type immunities
     *
     * @param event event details
     */
    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof LivingEntity && FlagManager.hasFlag((LivingEntity) event.getEntity(), "immune:" + event.getCause().name())) {
            double multiplier = SkillAPI.getMetaDouble(event.getEntity(), ImmunityMechanic.META_KEY);
            if (multiplier <= 0)
                event.setCancelled(true);
            else
                event.setDamage(event.getDamage() * multiplier);
        }
    }

    /**
     * Cancels food damaging the player when the bar is being used
     * for GUI features instead of normal hunger.
     *
     * @param event event details
     */
    @EventHandler
    public void onStarve(EntityDamageEvent event)
    {
        if (event.getCause() == EntityDamageEvent.DamageCause.STARVATION
                && !SkillAPI.getSettings().getFoodBar().equalsIgnoreCase("none"))
        {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels saturation heal
     *
     * @param event event details
     */
    @EventHandler
    public void onSaturationHeal(EntityRegainHealthEvent event)
    {
        String foodBar = SkillAPI.getSettings().getFoodBar().toLowerCase();
        if ((foodBar.equals("mana") || foodBar.equals("exp"))
                && event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED)
        {
            event.setCancelled(true);
        }
    }

    /**
     * Launches physical damage events to differentiate skill damage from physical damage
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPhysicalDamage(EntityDamageByEntityEvent event)
    {
        if (Skill.isSkillDamage()
                || event.getCause() == EntityDamageEvent.DamageCause.CUSTOM
                || !(event.getEntity() instanceof LivingEntity)
                || event.getDamage() <= 0)
        {
            return;
        }

        PhysicalDamageEvent e = new PhysicalDamageEvent(ListenerUtil.getDamager(event), (LivingEntity) event.getEntity(), event.getDamage(), event.getDamager() instanceof Projectile);
        Bukkit.getPluginManager().callEvent(e);
        event.setDamage(e.getDamage());
        event.setCancelled(e.isCancelled());
    }

    /**
     * Handles marking players as in combat
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCombat(EntityDamageByEntityEvent event)
    {
        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM
                || !(event.getEntity() instanceof LivingEntity)) return;

        if (event.getEntity() instanceof Player)
        {
            Combat.applyCombat((Player) event.getEntity());
        }

        LivingEntity damager = ListenerUtil.getDamager(event);
        if (damager instanceof Player)
        {
            Combat.applyCombat((Player) damager);
        }
    }

    /**
     * Applies or removes SkillAPI features from a player upon switching worlds
     *
     * @param event event details
     */
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event)
    {
        if (event.getPlayer().hasMetadata("NPC"))
            return;

        boolean oldEnabled = SkillAPI.getSettings().isWorldEnabled(event.getFrom());
        boolean newEnabled = SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld());
        if (oldEnabled && !newEnabled)
        {
            PlayerData data = SkillAPI.getPlayerData(event.getPlayer());
            data.clearBonuses();
            data.stopPassives(event.getPlayer());
            ClassBoardManager.clear(new VersionPlayer(event.getPlayer()));
            event.getPlayer().setMaxHealth(SkillAPI.getSettings().getDefaultHealth());
            event.getPlayer().setHealth(SkillAPI.getSettings().getDefaultHealth());
            if (!SkillAPI.getSettings().getLevelBar().equalsIgnoreCase("none"))
            {
                event.getPlayer().setLevel(0);
                event.getPlayer().setExp(0);
            }
            if (!SkillAPI.getSettings().getFoodBar().equalsIgnoreCase("none"))
            {
                event.getPlayer().setFoodLevel(20);
            }
        }
        else if (!oldEnabled && newEnabled) {
            init(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        if (!SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
            return;

        if (event.getMessage().equals("/clear")) {
            handleClear(event.getPlayer());
        }
        else if (event.getMessage().startsWith("/clear ")) {
            handleClear(VersionManager.getPlayer(event.getMessage().substring(7)));
        }
    }

    @EventHandler
    public void onCommand(final ServerCommandEvent event) {
        if (event.getCommand().startsWith("clear ")) {
            handleClear(VersionManager.getPlayer(event.getCommand().substring(6)));
        }
    }

    private void handleClear(final Player player) {
        if (player != null) {
            SkillAPI.schedule(() -> CLEAR_HANDLERS.forEach(handler -> handler.accept(player)), 1);
        }
    }
}
