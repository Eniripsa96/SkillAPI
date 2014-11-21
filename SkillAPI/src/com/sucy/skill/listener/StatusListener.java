package com.sucy.skill.listener;

import com.rit.sucy.config.FilterType;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.Status;
import com.sucy.skill.api.event.PlayerCastSkillEvent;
import com.sucy.skill.api.util.StatusManager;
import com.sucy.skill.language.RPGFilter;
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
        StatusManager.clearStatuses(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event)
    {
        check(event, event.getPlayer(), Status.STUN, Status.ROOT);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM)
        {
            return;
        }

        LivingEntity damager = ListenerUtil.getDamager(event);
        check(event, damager, Status.STUN, Status.DISARM);
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
            if (check(event, entity, Status.ABSORB))
            {
                VersionManager.heal(entity, event.getDamage());
            }
            else
            {
                check(event, entity, Status.INVINCIBLE);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLaunch(ProjectileLaunchEvent event)
    {
        if (event.getEntity().getShooter() instanceof LivingEntity)
        {
            LivingEntity shooter = (LivingEntity) event.getEntity().getShooter();
            check(event, shooter, Status.STUN, Status.DISARM);
        }
    }

    @EventHandler
    public void onCast(PlayerCastSkillEvent event)
    {
        check(event, event.getPlayer(), Status.SILENCE, Status.STUN);
    }

    private boolean check(Cancellable event, LivingEntity entity, Status... statuses)
    {
        for (Status status : statuses)
        {
            if (StatusManager.hasStatus(entity, status))
            {
                if (status.hasMessageNode() && entity instanceof Player)
                {
                    plugin.getLanguage().sendMessage(status.getMessageNode(), (Player) entity, FilterType.COLOR,
                            RPGFilter.DURATION.setReplacement(StatusManager.getTimeLeft(entity, status) + ""));
                }
                event.setCancelled(true);
                return true;
            }
        }
        return false;
    }
}
