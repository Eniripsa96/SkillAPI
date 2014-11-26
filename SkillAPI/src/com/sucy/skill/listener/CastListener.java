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

public class CastListener implements Listener
{
    public CastListener(SkillAPI plugin)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

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
