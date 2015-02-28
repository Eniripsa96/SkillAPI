package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.task.InventoryTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

/**
 * Listener that handles weapon item lore requirements
 */
public class ItemListener implements Listener
{

    /**
     * Sets up the listener. This should not be used
     * by other plugins as it is already handled by
     * the API. Creating another one would cause
     * duplicated checks.
     *
     * @param api API reference
     */
    public ItemListener(SkillAPI api)
    {
        api.getServer().getPluginManager().registerEvents(this, api);
    }

    /**
     * Cancels left clicks on disabled items
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onAttack(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Player)
        {
            Player player = (Player) event.getDamager();
            if (InventoryTask.cannotUse(SkillAPI.getPlayerData(player), player.getItemInHand()))
            {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Cancels firing a bow with a disabled weapon
     *
     * @param event event details
     */
    @EventHandler
    public void onShoot(EntityShootBowEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            if (InventoryTask.cannotUse(SkillAPI.getPlayerData((Player) event.getEntity()), event.getBow()))
            {
                event.setCancelled(true);
            }
        }
    }
}
