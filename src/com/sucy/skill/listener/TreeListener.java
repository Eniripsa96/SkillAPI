package com.sucy.skill.listener;

import com.rit.sucy.items.InventoryManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.tree.SkillTree;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TreeListener implements Listener
{
    SkillAPI plugin;

    public TreeListener(SkillAPI plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Handles skill tree interaction
     *
     * @param event event details
     */
    @EventHandler
    public void onClick(InventoryClickEvent event)
    {

        // Make sure its a skill tree inventory
        if (InventoryManager.isMatching(event.getInventory(), SkillTree.INVENTORY_KEY))
        {
            SkillTree tree = SkillAPI.getClass(event.getInventory().getName()).getSkillTree();

            // Do nothing when clicking outside the inventory
            if (event.getSlot() == -999)
            {
                return;
            }

            boolean top = event.getRawSlot() < event.getView().getTopInventory().getSize();

            // Interact with the skill tree when clicking in the top region
            if (top)
            {
                event.setCancelled(tree.checkClick(event.getSlot()));

                // If they clicked on a skill, try upgrading it
                if (tree.isSkill(event.getWhoClicked(), event.getSlot()))
                {
                    PlayerData player = SkillAPI.getPlayerData((Player) event.getWhoClicked());
                    if (event.isLeftClick())
                    {
                        if (player.upgradeSkill(tree.getSkill(event.getSlot())))
                        {
                            tree.update(event.getInventory(), player);
                        }
                    }
                    else if (event.isRightClick() && SkillAPI.getSettings().isAllowDowngrade())
                    {
                        if (player.downgradeSkill(tree.getSkill(event.getSlot())))
                        {
                            tree.update(event.getInventory(), player);
                        }
                    }
                }
            }

            // Do not allow shift clicking items into the inventory
            else if (event.isShiftClick())
            {
                event.setCancelled(true);
            }
        }
    }
}
