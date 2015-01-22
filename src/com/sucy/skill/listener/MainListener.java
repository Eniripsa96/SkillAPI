package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.data.Permissions;
import com.sucy.skill.dynamic.mechanic.ProjectileMechanic;
import com.sucy.skill.manager.ClassBoardManager;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * The main listener for SkillAPI  that handles general mechanics
 * such as loading/clearing data, controlling experience gains, and
 * enabling/disabling passive abilities.
 */
public class MainListener implements Listener
{
    public static final String P_CALL = "pmCallback";

    private static final String S_TYPE  = "sType";
    private static final int    SPAWNER = 0, EGG = 1;

    private SkillAPI plugin;

    /**
     * Initializes a new listener for general SkillAPI functions. This is
     * handled by the API and should not be used by other plugins.
     *
     * @param plugin SkillAPI plugin reference
     */
    public MainListener(SkillAPI plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Loads player data asynchronously when a player tries to log in
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLogin(AsyncPlayerPreLoginEvent event)
    {
        SkillAPI.loadPlayerData(event.getName());
    }

    /**
     * Starts passives and applies class data when a player logs in.
     *
     * @param event event details
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        PlayerData data = SkillAPI.getPlayerData(event.getPlayer());

        // Apply player data as long as they have a class
        if (data.hasClass())
        {
            data.updateHealthAndMana(event.getPlayer());
            data.updateLevelBar();
            data.startPassives(event.getPlayer());
            RPGClass classData = data.getMainClass().getData();
            ClassBoardManager.update(data, classData.getPrefix(), classData.getPrefixColor());
        }
    }

    /**
     * Stops passives an applies death penalties when a player dies.
     *
     * @param event event details
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
        PlayerData data = SkillAPI.getPlayerData(event.getEntity());
        if (data.hasClass())
        {
            data.stopPassives(event.getEntity());
            if (SkillAPI.getSettings().getDeathPenalty() > 0)
            {
                data.loseExp(SkillAPI.getSettings().getDeathPenalty());
            }
        }
    }

    /**
     * Grants experience upon killing a monster and blocks experience when
     * the monster originated from a blocked source.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onKill(EntityDeathEvent event)
    {

        // Cancel experience when applicable
        if (event.getEntity().hasMetadata(S_TYPE))
        {
            int value = event.getEntity().getMetadata(S_TYPE).get(0).asInt();

            // Block spawner mob experience
            if (value == SPAWNER && SkillAPI.getSettings().isBlockSpawner())
            {
                if (SkillAPI.getSettings().isUseOrbs())
                {
                    event.setDroppedExp(0);
                }
                return;
            }

            // Block egg mob experience
            else if (value == EGG && SkillAPI.getSettings().isBlockEgg())
            {
                if (SkillAPI.getSettings().isUseOrbs())
                {
                    event.setDroppedExp(0);
                }
                return;
            }
        }

        Player k = event.getEntity().getKiller();
        if (event.getEntity().getKiller() != null && event.getEntity().getKiller().hasPermission(Permissions.EXP))
        {

            // Block creative experience
            if (event.getEntity().getKiller().getGameMode() == GameMode.CREATIVE && SkillAPI.getSettings().isBlockCreative())
            {
                if (SkillAPI.getSettings().isUseOrbs())
                {
                    event.setDroppedExp(0);
                }
                return;
            }

            PlayerData player = SkillAPI.getPlayerData(event.getEntity().getKiller());

            // Give experience based on orbs when enabled
            if (SkillAPI.getSettings().isUseOrbs())
            {
                player.giveExp(event.getDroppedExp(), ExpSource.MOB);
                event.setDroppedExp(0);
            }

            // Give experience based on config when not using orbs
            else
            {
                String name = ListenerUtil.getName(event.getEntity());
                double yield = SkillAPI.getSettings().getYield(name);
                player.giveExp(yield, ExpSource.MOB);
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
        Player player = event.getPlayer();
        if (SkillAPI.getSettings().isUseOrbs() && player != null)
        {
            SkillAPI.getPlayerData(player).giveExp(event.getExpToDrop(), ExpSource.BLOCK_BREAK);
        }
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
        if (SkillAPI.getSettings().isUseOrbs() && player != null)
        {
            SkillAPI.getPlayerData(player).giveExp(event.getExpToDrop(), ExpSource.SMELT);
        }
    }

    /**
     * Handles experience when a Bottle o' Enchanting breaks
     *
     * @param event event details
     */
    @EventHandler
    public void onExpBottleBreak(ExpBottleEvent event)
    {
        if (!(event.getEntity().getShooter() instanceof Player))
        {
            return;
        }
        Player player = (Player) event.getEntity().getShooter();
        if (SkillAPI.getSettings().isUseOrbs())
        {
            SkillAPI.getPlayerData(player).giveExp(event.getExperience(), ExpSource.EXP_BOTTLE);
        }
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
        if (SkillAPI.getSettings().isUseLevelBar() && event.getPlayer().hasPermission(Permissions.EXP))
        {
            event.setAmount(0);
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
        PlayerData data = SkillAPI.getPlayerData(event.getPlayer());
        if (data.hasClass())
        {
            data.startPassives(event.getPlayer());
        }
    }

    /**
     * Marks spawned entities with how they spawned to block experience from certain methods
     *
     * @param event event details
     */
    @EventHandler
    public void onSpawn(CreatureSpawnEvent event)
    {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER)
        {
            event.getEntity().setMetadata(S_TYPE, new FixedMetadataValue(plugin, SPAWNER));
        }
        else if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)
        {
            event.getEntity().setMetadata(S_TYPE, new FixedMetadataValue(plugin, EGG));
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
            ((ProjectileMechanic)event.getEntity().getMetadata(P_CALL).get(0).value()).callback(event.getEntity(), null);
        }
    }

    /**
     * Applies projectile callbacks when striking an enemy
     *
     * @param event event details
     */
    @EventHandler
    public void onHit(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Projectile)
        {
            Projectile p = (Projectile)event.getDamager();
            if (p.hasMetadata(P_CALL) && event.getEntity() instanceof LivingEntity)
            {
                ((ProjectileMechanic)p.getMetadata(P_CALL).get(0).value()).callback(p, (LivingEntity)event.getEntity());
            }
        }
    }
}
