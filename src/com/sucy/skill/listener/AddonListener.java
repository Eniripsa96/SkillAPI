package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerCastSkillEvent;
import com.sucy.skill.api.event.PlayerExperienceGainEvent;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.hook.PluginChecker;
import com.sucy.skill.hook.WorldGuardHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Series of fixes/improvements created by EvrimSen and touched up by Eniripsa96
 *
 * See https://www.spigotmc.org/resources/addonforskillapi.55857/ for extra features
 */
public class AddonListener extends SkillAPIListener {
    private static final Set<UUID> IGNORE_CASTING = new HashSet<>();

    /**
     * Cancels damage between friendly classes
     *
     * @param event damage event
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerHit(final EntityDamageByEntityEvent event) {
        if (!SkillAPI.getSettings().isWorldEnabled(event.getEntity().getWorld())) {
            return;
        }

        final LivingEntity damager = ListenerUtil.getDamager(event);
        if (event.getEntity() instanceof Player && damager instanceof Player) {
            final PlayerData attackerData = SkillAPI.getPlayerData((Player) damager);
            final PlayerData defenderData = SkillAPI.getPlayerData((Player) event.getEntity());

            for (final String group : SkillAPI.getGroups()) {
                final boolean friendly = SkillAPI.getSettings().getGroupSettings(group).isFriendly();
                final PlayerClass attacker = attackerData.getClass(group);
                final PlayerClass defender = defenderData.getClass(group);
                if (friendly && attacker != null && defender != null && attacker.getData().getRoot() == defender.getData().getRoot()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Tracks when a player changes worlds for avoiding accidental skill casts
     * @see AddonListener#onSkillUse(PlayerCastSkillEvent)
     */
    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent e) {
        startIgnoring(e.getPlayer());
    }

    /**
     * Tracks when a player joins for avoiding accidental skill casts
     * @see AddonListener#onSkillUse(PlayerCastSkillEvent)
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        startIgnoring(e.getPlayer());
    }

    private void startIgnoring(final Player player) {
        if (!SkillAPI.getSettings().isWorldEnabled(player.getWorld())) {
            return;
        }

        final UUID uuid = player.getUniqueId();
        IGNORE_CASTING.add(uuid);
        SkillAPI.schedule(() -> IGNORE_CASTING.remove(uuid), 40);
    }

    /**
     * Cancels skill casts shortly after changing worlds or joining the server.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSkillUse(final PlayerCastSkillEvent e) {
        if (IGNORE_CASTING.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        } else if (PluginChecker.isWorldGuardActive()) {
            if (WorldGuardHook.getRegionIds(e.getPlayer().getLocation()).stream()
                    .anyMatch(id -> SkillAPI.getSettings().areSkillsDisabledForRegion(id))) {
                e.setCancelled(true);
            }
        }
    }

    /**
     * Cancels skill casts shortly after changing worlds or joining the server.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExpGain(final PlayerExperienceGainEvent e) {
        if (PluginChecker.isWorldGuardActive()) {
            if (WorldGuardHook.getRegionIds(e.getPlayerData().getPlayer().getLocation()).stream()
                    .anyMatch(id -> SkillAPI.getSettings().isExpDisabledForRegion(id))) {
                e.setCancelled(true);
            }
        }
    }
}
