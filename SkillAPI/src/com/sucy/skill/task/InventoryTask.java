package com.sucy.skill.task;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.language.OtherNodes;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Repeating task to check for equipment requirements
 */
public class InventoryTask extends BukkitRunnable {

    private SkillAPI plugin;
    private Player[] players;
    private int playersPerCheck;
    private int index = -1;

    /**
     * Task constructor
     *
     * @param plugin          API reference
     * @param playersPerCheck how many players to check each tick
     */
    public InventoryTask(SkillAPI plugin, int playersPerCheck) {
        this.plugin = plugin;
        this.playersPerCheck = playersPerCheck;
        players = plugin.getServer().getOnlinePlayers();
        runTaskTimer(plugin, 1, 1);
    }

    /**
     * Checks player equipment for requirements
     */
    @Override
    public void run() {
        for (int i = 0; i < playersPerCheck; i++) {
            if (!getNextPlayer()) return;
            if (i >= players.length) return;

            // Get the player data
            Player player = players[index];
            PlayerSkills data = plugin.getPlayer(player.getName());

            // Check for lore strings
            int index = 0;
            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (item == null) continue;
                if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
                    List<String> lore = item.getItemMeta().getLore();

                    // Check each line of the lore
                    for (String line : lore) {
                        String colorless = ChatColor.stripColor(line);

                        // Level requirements
                        if (colorless.matches("Level Req: [0-9]+")) {
                            int level = Integer.parseInt(colorless.substring(11));
                            if (data.getLevel() < level) {
                                removeArmor(player, index);
                            }
                        }

                        // Class requirements
                        else if (colorless.matches("Class Req: .+")) {
                            String name = colorless.substring(11);
                            if (!isMatchingClass(name, data.getClassName())) {
                                removeArmor(player, index);
                            }
                        }
                    }
                }
                index++;
            }
        }
    }

    /**
     * Checks if the player's class matches a required class
     *
     * @param req    required class
     * @param actual actual class
     * @return       true if matches, false otherwise
     */
    private boolean isMatchingClass(String req, String actual) {

        // If the player doesn't have a class, it doesn't match any
        if (actual == null) return false;

        // If it is the class, it matches
        if (actual.equalsIgnoreCase(req)) {
            return true;
        }

        // Check child classes
        List<String> children = plugin.getChildren(req);
        for (String child : children) {
            if (isMatchingClass(child, actual)) return true;
        }

        // Doesn't match the required class
        return false;
    }

    /**
     * Removes the armor piece at the given index
     *
     * @param player player to remove for
     * @param index  index of the armor piece to remove
     */
    private void removeArmor(Player player, int index) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        player.getInventory().addItem(armor[index]);
        armor[index] = null;
        player.getInventory().setArmorContents(armor);
        player.sendMessage(plugin.getMessage(OtherNodes.CANNOT_USE_ITEM, true));
    }

    /**
     * Gets the next player to check
     *
     * @return true if found a player, false otherwise
     */
    private boolean getNextPlayer() {
        index++;

        // Limit the index
        if (index >= players.length) {
            players = plugin.getServer().getOnlinePlayers();
            index = 0;
        }

        // Make sure its a valid player
        return players.length > 0 && (players[index].isOnline() || getNextPlayer());
    }
}
