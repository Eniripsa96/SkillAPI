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

import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.enums.ManaSource;
import com.sucy.skill.api.event.*;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.hook.CitizensHook;
import com.sucy.skill.log.LogType;
import com.sucy.skill.log.Logger;
import com.sucy.skill.manager.AttributeManager;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;

/**
 * Listener for managing applying attribute bonuses for players
 */
public class AttributeListener extends SkillAPIListener
{
    public static final String PHYSICAL = "physical";
    private static HashMap<String, Double> BONUSES = new HashMap<>();

    @Override
    public void init() {
        MainListener.registerJoin(this::onJoin);
    }

    /**
     * Cleans up the listener on shutdown
     */
    @Override
    public void cleanup() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            clearBonuses(player);
        }
        BONUSES.clear();
    }

    /**
     * Clears stored bonuses for the given player
     *
     * @param player player to clear bonuses for
     */
    public static void clearBonuses(Player player)
    {
        clearLocalBonuses(player);
        BONUSES.remove(player.getName() + ":" + AttributeManager.HEALTH);
        BONUSES.remove(player.getName() + ":" + AttributeManager.MANA);
    }

    private static void clearLocalBonuses(Player player)
    {
        BONUSES.remove(player.getName() + ":" + AttributeManager.MOVE_SPEED);
        player.setWalkSpeed(0.2f);

        if (VersionManager.isVersionAtLeast(VersionManager.V1_9_0)) {
            clear(player, Attribute.GENERIC_ATTACK_SPEED, AttributeManager.ATTACK_SPEED);
            clear(player, Attribute.GENERIC_ARMOR, AttributeManager.ARMOR);
            clear(player, Attribute.GENERIC_LUCK, AttributeManager.LUCK);
            clear(player, Attribute.GENERIC_KNOCKBACK_RESISTANCE, AttributeManager.LUCK);
        }
        if (VersionManager.isVersionAtLeast(11200)) {
            clear(player, Attribute.GENERIC_ARMOR_TOUGHNESS, AttributeManager.ARMOR_TOUGHNESS);
        }
    }

    private static void clear(Player player, Object attribute, String attribKey) {
        if (!BONUSES.containsKey(attribKey)) {
            return;
        }

        double bonus = BONUSES.remove(attribKey);
        AttributeInstance instance = player.getAttribute((Attribute) attribute);
        instance.setBaseValue(instance.getBaseValue() - bonus);
    }

    /**
     * Gives players bonus stats on login
     */
    public void onJoin(final Player player)
    {
        updatePlayer(SkillAPI.getPlayerData(player));
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
            if (CitizensHook.isNPC(player))
                return;

            PlayerData data = SkillAPI.getPlayerData(player);

            double newAmount = data.scaleStat(AttributeManager.PHYSICAL_DAMAGE, event.getDamage());
            if (event.isProjectile()) {
                newAmount = data.scaleStat(AttributeManager.PROJECTILE_DAMAGE, newAmount);
            } else {
                newAmount = data.scaleStat(AttributeManager.MELEE_DAMAGE, newAmount);
            }
            event.setDamage(newAmount);
        }

        // Physical Defense
        if (event.getTarget() instanceof Player)
        {
            Player player = (Player) event.getTarget();
            if (CitizensHook.isNPC(player))
                return;

            PlayerData data = SkillAPI.getPlayerData(player);

            double newAmount = data.scaleStat(AttributeManager.PHYSICAL_DEFENSE, event.getDamage());
            if (event.isProjectile()) {
                newAmount = data.scaleStat(AttributeManager.PROJECTILE_DEFENSE, newAmount);
            } else {
                newAmount = data.scaleStat(AttributeManager.MELEE_DEFENSE, newAmount);
            }
            event.setDamage(newAmount);
        }
    }

    /**
     * Apply skill damage/defense attribute buffs
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSkillDamage(final SkillDamageEvent event)
    {
        // Skill Damage
        if (event.getDamager() instanceof Player)
        {
            final Player player = (Player) event.getDamager();
            if (CitizensHook.isNPC(player))
                return;

            final PlayerData data = SkillAPI.getPlayerData(player);

            if (event.getClassification().equalsIgnoreCase(PHYSICAL)) {
                event.setDamage(data.scaleStat(AttributeManager.PHYSICAL_DAMAGE, event.getDamage()));
            } else {
                final String classified = AttributeManager.SKILL_DAMAGE + "-" + event.getClassification();
                final double firstPass = data.scaleStat(classified, event.getDamage());
                final double newAmount = data.scaleStat(AttributeManager.SKILL_DAMAGE, firstPass);
                event.setDamage(newAmount);
            }
        }

        // Skill Defense
        if (event.getTarget() instanceof Player)
        {
            final Player player = (Player) event.getTarget();
            if (CitizensHook.isNPC(player))
                return;

            final PlayerData data = SkillAPI.getPlayerData(player);

            if (event.getClassification().equalsIgnoreCase(PHYSICAL)) {
                event.setDamage(data.scaleStat(AttributeManager.PHYSICAL_DEFENSE, event.getDamage()));
            } else {
                final String classified = AttributeManager.SKILL_DEFENSE + "-" + event.getClassification();
                final double firstPass = data.scaleStat(classified, event.getDamage());
                final double newAmount = data.scaleStat(AttributeManager.SKILL_DEFENSE, firstPass);
                event.setDamage(newAmount);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        final Player player = (Player)event.getEntity();
        if (CitizensHook.isNPC(player)) {
            return;
        }

        final PlayerData data = SkillAPI.getPlayerData(player);
        event.setDamage(data.scaleStat("defense-" + event.getCause().name().toLowerCase(), event.getDamage()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onExp(final PlayerExperienceGainEvent event) {
        if (event.getSource() != ExpSource.COMMAND) {
            final double newExp = event.getPlayerData().scaleStat(AttributeManager.EXPERIENCE, event.getExp());
            event.setExp(newExp);
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHungerChange(final FoodLevelChangeEvent event) {
        final Player player = (Player)event.getEntity();
        if (event.getFoodLevel() < player.getFoodLevel()) {
            final PlayerData data = SkillAPI.getPlayerData(player);
            final int lost = data.subtractHungerValue(player.getFoodLevel() - event.getFoodLevel());
            event.setFoodLevel(player.getFoodLevel() - lost);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHungerHeal(final EntityRegainHealthEvent event) {
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED
                && event.getEntity() instanceof Player) {
            final Player player = (Player)event.getEntity();
            final PlayerData data = SkillAPI.getPlayerData(player);
            final double scaled = data.scaleStat(AttributeManager.HUNGER_HEAL, event.getAmount());
            event.setAmount(scaled);
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
            double change = updateStat(data, AttributeManager.HEALTH, player.getMaxHealth(), 0, Double.MAX_VALUE);
            data.addMaxHealth(change);

            change = updateStat(data, AttributeManager.MANA, data.getMaxMana(), 0, Double.MAX_VALUE);
            data.addMaxMana(change);

            change = updateStat(data, AttributeManager.MOVE_SPEED, player.getWalkSpeed(), -2, 1);
            player.setWalkSpeed(player.getWalkSpeed() + (float) change);

            if (VersionManager.isVersionAtLeast(VersionManager.V1_9_0)) {
                update(data, player, Attribute.GENERIC_ATTACK_SPEED, AttributeManager.ATTACK_SPEED, 0, 1024);
                update(data, player, Attribute.GENERIC_ARMOR, AttributeManager.ARMOR, 0, 30);
                update(data, player, Attribute.GENERIC_LUCK, AttributeManager.LUCK, -1024, 1024);
                update(data, player, Attribute.GENERIC_KNOCKBACK_RESISTANCE, AttributeManager.KNOCKBACK_RESIST, 0, 1.0);
            }
            if (VersionManager.isVersionAtLeast(110200)) {
                update(data, player, Attribute.GENERIC_ARMOR_TOUGHNESS, AttributeManager.ARMOR_TOUGHNESS, 0, 20);
            }
        }
    }

    private static void update(
            final PlayerData data,
            final Player player,
            final Object attribute,
            final String attribKey,
            final double min,
            final double max) {

        final AttributeInstance instance = player.getAttribute((Attribute) attribute);
        final double change = updateStat(data, attribKey, instance.getBaseValue(), min, max);
        instance.setBaseValue(instance.getBaseValue() + change);
    }

    /**
     * Refreshes player speed after buffs expire
     *
     * @param player player to refresh
     */
    public static void refreshSpeed(Player player)
    {
        BONUSES.remove(player.getName() + ":" + AttributeManager.MOVE_SPEED);
        double speed = updateStat(SkillAPI.getPlayerData(player), AttributeManager.MOVE_SPEED, 0.2, -2, 1);
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
    private static double updateStat(PlayerData data, String key, double value, double min, double max)
    {
        Player player = data.getPlayer();
        if (player != null)
        {
            String mapKey = player.getName() + ":" + key;
            double current = BONUSES.containsKey(mapKey) ? BONUSES.remove(mapKey) : 0;
            double updated = Math.max(min, Math.min(max, data.scaleStat(key, value - current) - value + current));
            BONUSES.put(mapKey, updated);
            return updated - current;
        }
        return 0;
    }
}
