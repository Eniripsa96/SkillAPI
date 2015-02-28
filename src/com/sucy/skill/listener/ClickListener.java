package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerCombos;
import com.sucy.skill.data.Click;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Handles transferring click actions by the player to
 * combos that cast skills.
 */
public class ClickListener implements Listener
{

    /**
     * Sets up the listener. This should not be used by other plugins
     * as it is handled by the API. Duplicate listeners will cause
     * click registrations to double up, causing a lot of problems.
     *
     * @param api API reference
     */
    public ClickListener(SkillAPI api)
    {
        api.getServer().getPluginManager().registerEvents(this, api);
    }

    /**
     * Registers clicks as they happen
     *
     * @param event event details
     */
    @EventHandler
    public void onClick(PlayerInteractEvent event)
    {

        // Get the history
        PlayerCombos combo = SkillAPI.getPlayerData(event.getPlayer()).getComboData();

        // Left clicks
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            combo.applyClick(Click.LEFT);
        }

        // Right clicks
        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
        {
            combo.applyClick(Click.RIGHT);
        }
    }

    /**
     * Registers shift clicks as they happen
     *
     * @param event event details
     */
    @EventHandler
    public void onShiftClick(PlayerToggleSneakEvent event)
    {
        if (event.isSneaking())
        {
            SkillAPI.getPlayerData(event.getPlayer()).getComboData().applyClick(Click.SHIFT);
        }
    }
}
