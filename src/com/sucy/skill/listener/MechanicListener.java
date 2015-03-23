package com.sucy.skill.listener;

import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.FlagExpireEvent;
import com.sucy.skill.api.event.PlayerLandEvent;
import com.sucy.skill.dynamic.mechanic.PotionProjectileMechanic;
import com.sucy.skill.dynamic.mechanic.ProjectileMechanic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;

/**
 * The listener for handling events related to dynamic mechanics
 */
public class MechanicListener implements Listener
{
    public static final String SUMMON_DAMAGE     = "sapiSumDamage";
    public static final String P_CALL            = "pmCallback";
    public static final String POTION_PROJECTILE = "potionProjectile";
    public static final String SPEED_KEY         = "sapiSpeedKey";

    private HashSet<Integer> flying = new HashSet<Integer>();

    /**
     * Initializes a new listener for dynamic mechanic related events.
     * This is handled by the API and shouldn't be used by other plugins.
     *
     * @param api api reference
     */
    public MechanicListener(SkillAPI api)
    {
        api.getServer().getPluginManager().registerEvents(this, api);
    }

    /**
     * Checks for landing on the ground
     *
     * @param event event details
     */
    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        boolean inMap = flying.contains(event.getPlayer().getEntityId());
        if (inMap == ((Entity) event.getPlayer()).isOnGround())
        {
            if (inMap)
            {
                flying.remove(event.getPlayer().getEntityId());
                Bukkit.getPluginManager().callEvent(new PlayerLandEvent(event.getPlayer()));
            }
            else
            {
                flying.add(event.getPlayer().getEntityId());
            }
        }
    }

    /**
     * Resets walk speed and clears them from the map when quitting
     *
     * @param event event details
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        flying.remove(event.getPlayer().getEntityId());
        event.getPlayer().setWalkSpeed(0.2f);
    }

    /**
     * Clears speed modifiers when the flag expires
     *
     * @param event event details
     */
    @EventHandler
    public void onExpire(FlagExpireEvent event)
    {
        if (event.getFlag().equals(SPEED_KEY))
        {
            ((Player) event.getEntity()).setWalkSpeed(0.2f);
        }
    }

    /**
     * Applies projectile callbacks when landing on the ground
     *
     * @param event event details
     */
    @EventHandler
    public void onLand(ProjectileHitEvent event)
    {
        if (event.getEntity().hasMetadata(P_CALL))
        {
            ((ProjectileMechanic) event.getEntity().getMetadata(P_CALL).get(0).value()).callback(event.getEntity(), null);
        }
    }

    /**
     * Applies projectile callbacks when striking an enemy
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onShoot(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Projectile)
        {
            Projectile p = (Projectile) event.getDamager();
            if (p.hasMetadata(P_CALL) && event.getEntity() instanceof LivingEntity)
            {
                ((ProjectileMechanic) p.getMetadata(P_CALL).get(0).value()).callback(p, (LivingEntity) event.getEntity());
                event.setCancelled(true);
            }
        }
    }

    /**
     * Handles when summoned monsters deal damage
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onSummonDamage(EntityDamageByEntityEvent event)
    {
        if (event.getDamager().hasMetadata(SUMMON_DAMAGE))
        {
            VersionManager.setDamage(event, event.getDamager().getMetadata(SUMMON_DAMAGE).get(0).asDouble());
        }
    }

    /**
     * Handles when a potion projectile hits things
     *
     * @param event event details
     */
    @EventHandler
    public void onSplash(PotionSplashEvent event)
    {
        if (event.getEntity().hasMetadata(POTION_PROJECTILE))
        {
            event.setCancelled(true);
            PotionProjectileMechanic mechanic = (PotionProjectileMechanic) event.getEntity().getMetadata(POTION_PROJECTILE).get(0).value();
            mechanic.callback(event.getEntity(), event.getAffectedEntities());
            event.getAffectedEntities().clear();
        }
    }
}
