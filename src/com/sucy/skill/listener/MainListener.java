package com.sucy.skill.listener;

import com.rit.sucy.version.VersionManager;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.event.PhysicalDamageEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.util.BuffManager;
import com.sucy.skill.api.util.Combat;
import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.data.Permissions;
import com.sucy.skill.manager.ClassBoardManager;
import org.bukkit.Bukkit;
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
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * The main listener for SkillAPI  that handles general mechanics
 * such as loading/clearing data, controlling experience gains, and
 * enabling/disabling passive abilities.
 */
public class MainListener implements Listener
{
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
        if (VersionManager.isVersionAtLeast(VersionManager.V1_7_5))
        {
            SkillAPI.loadPlayerData(Bukkit.getOfflinePlayer(event.getUniqueId()));
        }
        else
        {
            SkillAPI.loadPlayerData(VersionManager.getOfflinePlayer(event.getName()));
        }
    }

    /**
     * Starts passives and applies class data when a player logs in.
     *
     * @param event event details
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        double health = event.getPlayer().getHealth();
        PlayerData data = SkillAPI.getPlayerData(event.getPlayer());

        // Apply player data as long as they have a class
        if (data.hasClass() && SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
        {
            data.updateHealthAndMana(event.getPlayer());
            data.updateLevelBar();
            data.startPassives(event.getPlayer());
            data.updateScoreboard();
        }

        // Attempted workaround for weird health bug
        // TODO figure out what actually causes it
        if (health > 0 && health < event.getPlayer().getMaxHealth())
        {
            event.getPlayer().setHealth(health);
        }
    }

    /**
     * Saves player data when they log out and stops passives
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event)
    {
        FlagManager.clearFlags(event.getPlayer());
        BuffManager.clearData(event.getPlayer());
        Combat.clearData(event.getPlayer());

        PlayerData data = SkillAPI.getPlayerData(event.getPlayer());
        if (SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
        {
            data.stopPassives(event.getPlayer());
        }
        SkillAPI.unloadPlayerData(event.getPlayer());
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

        PlayerData data = SkillAPI.getPlayerData(event.getEntity());
        if (data.hasClass() && SkillAPI.getSettings().isWorldEnabled(event.getEntity().getWorld()))
        {
            data.stopPassives(event.getEntity());
            data.loseExp();
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
        FlagManager.clearFlags(event.getEntity());
        BuffManager.clearData(event.getEntity());

        // Disabled world
        if (!SkillAPI.getSettings().isWorldEnabled(event.getEntity().getWorld()))
        {
            return;
        }

        // Cancel experience when applicable
        if (event.getEntity().hasMetadata(S_TYPE))
        {
            int value = event.getEntity().getMetadata(S_TYPE).get(0).asInt();

            // Block spawner mob experience
            if (value == SPAWNER && SkillAPI.getSettings().isBlockSpawner())
            {
                return;
            }

            // Block egg mob experience
            else if (value == EGG && SkillAPI.getSettings().isBlockEgg())
            {
                return;
            }
        }

        Player k = event.getEntity().getKiller();
        if (k != null && k.hasPermission(Permissions.EXP))
        {
            // Block creative experience
            if (k.getGameMode() == GameMode.CREATIVE && SkillAPI.getSettings().isBlockCreative())
            {
                return;
            }

            PlayerData player = SkillAPI.getPlayerData(k);

            // Give experience based on orbs when enabled
            if (SkillAPI.getSettings().isUseOrbs())
            {
                player.giveExp(event.getDroppedExp(), ExpSource.MOB);
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
        if (SkillAPI.getSettings().isUseOrbs() && player != null && SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
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
        if (SkillAPI.getSettings().isUseOrbs() && player != null && SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
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
        if (!(event.getEntity().getShooter() instanceof Player) || !SkillAPI.getSettings().isWorldEnabled(((Player) event.getEntity().getShooter()).getWorld()))
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
        if (SkillAPI.getSettings().isUseLevelBar()
            && event.getPlayer().hasPermission(Permissions.EXP)
            && SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
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
        if (data.hasClass() && SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
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
     * Damage type immunities
     *
     * @param event event details
     */
    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof LivingEntity && FlagManager.hasFlag((LivingEntity) event.getEntity(), "immune:" + event.getCause().name()))
        {
            event.setCancelled(true);
        }
    }

    /**
     * Applies damage and defense buffs when something takes or deals
     * damage to something else.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) return;

        // Damage buff application
        LivingEntity damager = ListenerUtil.getDamager(event);
        VersionManager.setDamage(event, BuffManager.modifyDealtDamage(damager, event.getDamage()));

        // Cancel event if no damage
        if (event.getDamage() <= 0)
        {
            event.setCancelled(true);
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity))
        {
            return;
        }

        // Defense buff application
        LivingEntity damaged = (LivingEntity) event.getEntity();
        VersionManager.setDamage(event, BuffManager.modifyTakenDefense(damaged, event.getDamage()));

        // Cancel event if no damage
        if (event.getDamage() <= 0)
        {
            event.setCancelled(true);
        }
    }

    /**
     * Launches physical damage events to differentiate skill damage from physical damage
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPhysicalDamage(EntityDamageByEntityEvent event)
    {
        if (Skill.isSkillDamage() || !(event.getEntity() instanceof LivingEntity) || event.getCause() == EntityDamageEvent.DamageCause.CUSTOM)
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
        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) return;

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
        boolean oldEnabled = SkillAPI.getSettings().isWorldEnabled(event.getFrom());
        boolean newEnabled = SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld());
        if (oldEnabled && !newEnabled)
        {
            PlayerData data = SkillAPI.getPlayerData(event.getPlayer());
            data.stopPassives(event.getPlayer());
            data.getSkillBar().clear(event.getPlayer());
            ClassBoardManager.clear(new VersionPlayer(event.getPlayer()));
            event.getPlayer().setHealth(20);
            if (SkillAPI.getSettings().isUseLevelBar())
            {
                event.getPlayer().setLevel(0);
                event.getPlayer().setExp(0);
            }
        }
        else if (!oldEnabled && newEnabled)
        {
            PlayerData data = SkillAPI.getPlayerData(event.getPlayer());
            data.startPassives(event.getPlayer());
            data.getSkillBar().setup(event.getPlayer());
            data.updateScoreboard();
            data.updateHealthAndMana(event.getPlayer());
            data.updateLevelBar();
        }
    }
}
