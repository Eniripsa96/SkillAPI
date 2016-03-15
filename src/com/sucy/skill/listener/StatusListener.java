/**
 * SkillAPI
 * com.sucy.skill.listener.StatusListener
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
import com.sucy.skill.api.event.FlagApplyEvent;
import com.sucy.skill.api.event.PlayerCastSkillEvent;
import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.api.util.StatusFlag;
import com.sucy.skill.data.TitleType;
import com.sucy.skill.language.RPGFilter;
import com.sucy.skill.manager.TitleManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;

/**
 * Listener for applying default status flags for the API. You should
 * not use this class as it is already set up by the API.
 */
public class StatusListener implements Listener
{
    private static final HashMap<String, Long> messageTimers = new HashMap<String, Long>();

    private static final HashMap<String, String> messageMap = new HashMap<String, String>()
    {{
            put("stun", "stunned");
            put("root", "rooted");
            put("invincible", "invincible");
            put("absorb", "absorbed");
            put("disarm", "disarmed");
            put("silence", "silenced");
        }};

    private final Vector ZERO = new Vector(0, 0, 0);

    /**
     * Initializes a new StatusListener. Do not use this constructor
     * as the API already handles it.
     *
     * @param plugin API plugin reference
     */
    public StatusListener(SkillAPI plugin)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Clears data for players leaving the server
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event)
    {
        FlagManager.clearFlags(event.getPlayer());
    }

    /**
     * Cancels player movement when stunned or rooted
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event)
    {
        if (((LivingEntity) event.getPlayer()).isOnGround() && check(event, event.getPlayer(), event.getPlayer(), StatusFlag.STUN, StatusFlag.ROOT))
        {
            event.getPlayer().setVelocity(ZERO);
        }
    }

    /**
     * Applies a slow potion to mobs when stunned/rooted due to
     * them not having a move event like the players.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onFlag(FlagApplyEvent event)
    {
        if (event.getFlag().equals(StatusFlag.STUN) || event.getFlag().equals(StatusFlag.ROOT))
        {
            if (!(event.getEntity() instanceof Player))
            {
                event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, event.getTicks(), 100));
            }
        }
    }

    /**
     * Cancels damage when an attacker is disarmed.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM)
        {
            return;
        }

        LivingEntity damager = ListenerUtil.getDamager(event);
        check(event, damager, damager, StatusFlag.STUN, StatusFlag.DISARM);
    }

    /**
     * Cancels damage when a defender is invincible or inverting damage
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamaged(EntityDamageEvent event)
    {
        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM)
        {
            return;
        }

        if (event.getEntity() instanceof LivingEntity)
        {
            LivingEntity entity = (LivingEntity) event.getEntity();
            if (check(event, entity, null, StatusFlag.ABSORB))
            {
                VersionManager.heal(entity, event.getDamage());
            }
            else
            {
                check(event, entity, null, StatusFlag.INVINCIBLE);
            }
        }
    }

    /**
     * Cancels firing projectiles when the launcher is stunned or disarmed.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLaunch(ProjectileLaunchEvent event)
    {
        if (event.getEntity().getShooter() instanceof LivingEntity)
        {
            LivingEntity shooter = (LivingEntity) event.getEntity().getShooter();
            check(event, shooter, shooter, StatusFlag.STUN, StatusFlag.DISARM);
        }
    }

    /**
     * Cancels players casting skills while stunned or silenced
     *
     * @param event event details
     */
    @EventHandler
    public void onCast(PlayerCastSkillEvent event)
    {
        check(event, event.getPlayer(), event.getPlayer(), StatusFlag.SILENCE, StatusFlag.STUN);
    }

    /**
     * Checks for the delay between sending status messages
     *
     * @param player player to check for
     *
     * @return true if can send a message, false otherwise
     */
    private boolean checkTime(Player player)
    {
        if (!messageTimers.containsKey(player.getName())
            || System.currentTimeMillis() - messageTimers.get(player.getName()) > 1000)
        {
            messageTimers.put(player.getName(), System.currentTimeMillis());
            return true;
        }
        return false;
    }

    /**
     * Checks an entity for flags which cancel the event if applied.
     *
     * @param event    event that is cancelled if a flag is applied
     * @param entity   entity to check for flags
     * @param receiver entity to send messages to
     * @param flags    flags to check for
     *
     * @return the canceled state of the event
     */
    private boolean check(Cancellable event, LivingEntity entity, LivingEntity receiver, String... flags)
    {
        for (String flag : flags)
        {
            if (FlagManager.hasFlag(entity, flag))
            {
                if (receiver instanceof Player)
                {
                    Player player = (Player) receiver;
                    if (checkTime(player))
                    {
                        TitleManager.show(
                            player,
                            TitleType.STATUS,
                            "Status." + messageMap.get(flag),
                            RPGFilter.DURATION.setReplacement("" + FlagManager.getTimeLeft(entity, flag))
                        );
                    }
                }

                event.setCancelled(true);
                return true;
            }
        }
        return false;
    }
}
