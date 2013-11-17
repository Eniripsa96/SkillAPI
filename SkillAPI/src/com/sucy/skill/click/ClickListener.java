package com.sucy.skill.click;

import com.sucy.skill.SkillAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

/**
 * Listener for click skills
 */
public class ClickListener implements Listener {

    private final HashMap<String, ClickHistory> histories = new HashMap<String, ClickHistory>();
    private final SkillAPI api;

    /**
     * Constructor
     *
     * @param api API reference
     */
    public ClickListener(SkillAPI api) {
        api.getServer().getPluginManager().registerEvents(this, api);
        this.api = api;
    }

    /**
     * Registers clicks as they happen
     *
     * @param event event details
     */
    @EventHandler
    public void onClick(PlayerInteractEvent event) {

        // Add the player history if not found
        if (!histories.containsKey(event.getPlayer().getName())) {
            histories.put(event.getPlayer().getName(), new ClickHistory(api, event.getPlayer()));
        }

        // Get the history
        ClickHistory history = histories.get(event.getPlayer().getName());

        // Left clicks
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            history.addClick(MouseClick.LEFT);
        }

        // Right clicks
        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            history.addClick(MouseClick.RIGHT);
        }
    }

    /**
     * Clear history data for disconnecting players
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        histories.remove(event.getPlayer().getName());
    }
}
