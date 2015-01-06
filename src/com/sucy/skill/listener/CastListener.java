package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * A listener that handles casting skills through binds. This shouldn't be
 * use by other plugins as it is handled by the API.
 */
public class CastListener implements Listener
{
    /**
     * Initializes a new Castlistener. Do not use this constructor as
     * the API handles it already.
     *
     * @param plugin plugin reference
     */
    public CastListener(SkillAPI plugin)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Handles interact events to check when a player right clicks with
     * a bound item to cast a skill.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        PlayerData data = SkillAPI.getPlayerData(player);
        Material heldItem = player.getItemInHand().getType();

        // Must be on right click
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
        {
            return;
        }

        // Cannot be cancelled if clicking on a block
        if (event.isCancelled() && event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            return;
        }

        // Must have a valid item
        if (heldItem == null || data.getBoundSkill(heldItem) == null || !SkillAPI.isSkillRegistered(data.getBoundSkill(heldItem).getData().getName()))
        {
            return;
        }

        // Cast the skill
        data.cast(data.getBoundSkill(heldItem));
    }
}
