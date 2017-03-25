/**
 * SkillAPI
 * com.sucy.skill.listener.AttributeListener
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
import com.sucy.skill.api.enums.ManaSource;
import com.sucy.skill.api.event.*;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.log.LogType;
import com.sucy.skill.log.Logger;
import com.sucy.skill.manager.AttributeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;

/**
 * Listener for managing applying attribute bonuses for players
 */
public class AttributeListener extends SkillAPIListener
{
    private static HashMap<String, Double> bonuses = new HashMap<String, Double>();

    /**
     * Cleans up the listener on shutdown
     */
    @Override
    public void cleanup()
    {
        bonuses.clear();
    }

    /**
     * Clears stored bonuses for the given player
     *
     * @param player player to clear bonuses for
     */
    public static void clearBonuses(Player player)
    {
        clearLocalBonuses(player);
        bonuses.remove(player.getName() + ":" + AttributeManager.HEALTH);
        bonuses.remove(player.getName() + ":" + AttributeManager.MANA);
    }

    private static void clearLocalBonuses(Player player)
    {
        bonuses.remove(player.getName() + ":" + AttributeManager.MOVE_SPEED);
        player.setWalkSpeed(0.2f);
    }

    /**
     * Gives players bonus stats on login
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event)
    {
        if (event.getPlayer().hasMetadata("NPC"))
            return;

        updatePlayer(SkillAPI.getPlayerData(event.getPlayer()));
    }

    /**
     * Updates attributes on respawn
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onRespawn(PlayerRespawnEvent event)
    {
        if (event.getPlayer().hasMetadata("NPC"))
            return;

        updatePlayer(SkillAPI.getPlayerData(event.getPlayer()));
    }

    /**
     * Clears stored bonuses for a player when they quit
     *
     * @param event event details
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        if (event.getPlayer().hasMetadata("NPC"))
            return;

        clearBonuses(event.getPlayer());
    }

    /**
     * Applies health and mana bonus attributes
     *
     * @param event event details
     */
    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent event)
    {
        updatePlayer(event.getPlayerData());
    }

    /**
     * Applies health and mana attribute bonuses on upgrading the attribute
     *
     * @param event event details
     */
    @EventHandler
    public void onInvest(PlayerUpAttributeEvent event)
    {
        updatePlayer(event.getPlayerData());
    }

    /**
     * Apply attributes to mana regen
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onManaRegen(PlayerManaGainEvent event)
    {
        // Bonus to regen from attributes
        if (event.getSource() == ManaSource.REGEN)
        {
            double newAmount = event.getPlayerData().scaleStat(AttributeManager.MANA_REGEN, event.getAmount());
            Logger.log(LogType.MANA, 3, "Attributes scaled mana gain to " + newAmount);
            event.setAmount(newAmount);
        }
    }

    /**
     * Apply physical damage/defense attribute buffs
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPhysicalDamage(PhysicalDamageEvent event)
    {
        // Physical Damage
        if (event.getDamager() instanceof Player)
        {
            Player player = (Player) event.getDamager();
            if (player.hasMetadata("NPC"))
                return;

            PlayerData data = SkillAPI.getPlayerData(player);

            double newAmount = data.scaleStat(AttributeManager.PHYSICAL_DAMAGE, event.getDamage());
            event.setDamage(newAmount);
        }

        // Physical Defense
        if (event.getTarget() instanceof Player)
        {
            Player player = (Player) event.getTarget();
            if (player.hasMetadata("NPC"))
                return;

            PlayerData data = SkillAPI.getPlayerData(player);

            double newAmount = data.scaleStat(AttributeManager.PHYSICAL_DEFENSE, event.getDamage());
            event.setDamage(newAmount);
        }
    }

    /**
     * Apply skill damage/defense attribute buffs
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSkillDamage(SkillDamageEvent event)
    {
        // Skill Damage
        if (event.getDamager() instanceof Player)
        {
            Player player = (Player) event.getDamager();
            if (player.hasMetadata("NPC"))
                return;

            PlayerData data = SkillAPI.getPlayerData(player);

            double newAmount = data.scaleStat(AttributeManager.SKILL_DAMAGE, event.getDamage());
            event.setDamage(newAmount);
        }

        // Skill Defense
        if (event.getTarget() instanceof Player)
        {
            Player player = (Player) event.getTarget();
            if (player.hasMetadata("NPC"))
                return;

            PlayerData data = SkillAPI.getPlayerData(player);

            double newAmount = data.scaleStat(AttributeManager.SKILL_DEFENSE, event.getDamage());
            event.setDamage(newAmount);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        boolean oldEnabled = SkillAPI.getSettings().isWorldEnabled(event.getFrom());
        boolean newEnabled = SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld());
        if (oldEnabled && !newEnabled) {
            clearBonuses(event.getPlayer());
        } else if (!oldEnabled && newEnabled) {
            updatePlayer(SkillAPI.getPlayerData(event.getPlayer()));
        }
    }

    /**
     * Updates the stats of a player based on their current attributes
     *
     * @param data player to update
     */
    public static void updatePlayer(PlayerData data)
    {
        Player player = data.getPlayer();
        if (player != null && SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
        {
            double change = updateStat(data, AttributeManager.HEALTH, player.getMaxHealth());
            data.addMaxHealth(change);

            change = updateStat(data, AttributeManager.MANA, data.getMaxMana());
            data.addMaxMana(change);

            change = updateStat(data, AttributeManager.MOVE_SPEED, player.getWalkSpeed());
            if (change + player.getWalkSpeed() > 1)
            {
                bonuses.put(player.getName() + ":" + AttributeManager.MOVE_SPEED, 0.8);
                change = 1 - player.getWalkSpeed();
            }
            else if (change + player.getWalkSpeed() < -1)
            {
                bonuses.put(player.getName() + ":" + AttributeManager.MOVE_SPEED, -1.8);
                change = -1 - player.getWalkSpeed();
            }
            player.setWalkSpeed(player.getWalkSpeed() + (float) change);
        }
    }

    /**
     * Refreshes player speed after buffs expire
     *
     * @param player player to refresh
     */
    public static void refreshSpeed(Player player)
    {
        bonuses.remove(player.getName() + ":" + AttributeManager.MOVE_SPEED);
        double speed = updateStat(SkillAPI.getPlayerData(player), AttributeManager.MOVE_SPEED, 0.2);
        player.setWalkSpeed((float) (0.2 + speed));
    }

    /**
     * Updates an individual stat for a player
     *
     * @param data  player data
     * @param key   stat key
     * @param value current value
     *
     * @return change in the stat based on current attributes
     */
    private static double updateStat(PlayerData data, String key, double value)
    {
        Player player = data.getPlayer();
        if (player != null)
        {
            String mapKey = player.getName() + ":" + key;
            double current = bonuses.containsKey(mapKey) ? bonuses.remove(mapKey) : 0;
            double updated = data.scaleStat(key, value - current) - value + current;
            bonuses.put(mapKey, updated);
            return updated - current;
        }
        return 0;
    }
}
