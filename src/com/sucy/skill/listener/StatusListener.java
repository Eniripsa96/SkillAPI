package com.sucy.skill.listener;

import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.FlagApplyEvent;
import com.sucy.skill.api.event.PlayerCastSkillEvent;
import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.api.util.StatusFlag;
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

public class StatusListener implements Listener
{
    SkillAPI plugin;

    public StatusListener(SkillAPI plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event)
    {
        FlagManager.clearFlags(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event)
    {
        check(event, event.getPlayer(), StatusFlag.STUN, StatusFlag.ROOT);
    }

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM)
        {
            return;
        }

        LivingEntity damager = ListenerUtil.getDamager(event);
        check(event, damager, StatusFlag.STUN, StatusFlag.DISARM);
    }

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
            if (check(event, entity, StatusFlag.ABSORB))
            {
                VersionManager.heal(entity, event.getDamage());
            }
            else
            {
                check(event, entity, StatusFlag.INVINCIBLE);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLaunch(ProjectileLaunchEvent event)
    {
        if (event.getEntity().getShooter() instanceof LivingEntity)
        {
            LivingEntity shooter = (LivingEntity) event.getEntity().getShooter();
            check(event, shooter, StatusFlag.STUN, StatusFlag.DISARM);
        }
    }

    @EventHandler
    public void onCast(PlayerCastSkillEvent event)
    {
        check(event, event.getPlayer(), StatusFlag.SILENCE, StatusFlag.STUN);
    }

    private boolean check(Cancellable event, LivingEntity entity, String... flags)
    {
        for (String flag : flags)
        {
            if (FlagManager.hasFlag(entity, flag))
            {
                // TODO status message

                event.setCancelled(true);
                return true;
            }
        }
        return false;
    }
}
