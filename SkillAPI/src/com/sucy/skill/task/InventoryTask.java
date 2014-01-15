package com.sucy.skill.task;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.language.OtherNodes;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Repeating task to check for equipment requirements
 */
public class InventoryTask extends BukkitRunnable {

    private static SkillAPI plugin;
    private int playersPerCheck;
    private int index = -1;

    /**
     * Task constructor
     *
     * @param p               API reference
     * @param playersPerCheck how many players to check each tick
     */
    public InventoryTask(SkillAPI p, int playersPerCheck) {
        this.playersPerCheck = playersPerCheck;
        if (plugin != null) return;
        plugin = p;
        runTaskTimer(plugin, 1, 1);
    }

    /**
     * Clears the plugin reference on cancel
     */
    @Override
    public void cancel() {
        super.cancel();
        plugin = null;
    }

    /**
     * Checks player equipment for requirements
     */
    @Override
    public void run() {
        Player[] players = plugin.getServer().getOnlinePlayers();
        for (int i = 0; i < playersPerCheck; i++) {
            if (!getNextPlayer(players)) return;
            if (i >= players.length) return;

            // Get the player data
            Player player = players[index];
            if (player.getGameMode() == GameMode.CREATIVE) continue;
            PlayerSkills data = plugin.getPlayer(player.getName());

            // Check for lore strings
            int index = 0;
            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (cannotUse(data, item)) removeArmor(player, index);
                index++;
            }
        }
    }

    /**
     * <p>Checks if the player cannot use the item</p>
     * <p>If SkillAPI is not enabled or it's lore requirement setting
     * is disabled, this will always return false</p>
     *
     * @param player player to check for
     * @param item   item to check
     * @return       true if cannot use, false otherwise
     */
    public static boolean cannotUse(PlayerSkills player, ItemStack item) {
        if (plugin == null) return false;
        if (item == null) return false;
        boolean hasRequirement = false;
        boolean needsRequirement = false;
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<String> lore = item.getItemMeta().getLore();

            // Check each line of the lore
            for (String line : lore) {
                String colorless = ChatColor.stripColor(line);

                // Level requirements
                if (colorless.matches("Level Req: [0-9]+")) {
                    int level = Integer.parseInt(colorless.substring(11));
                    if (player.getLevel() < level) {
                        return true;
                    }
                }

                // Class requirements
                else if (colorless.matches("Class Req: .+")) {
                    needsRequirement = true;
                    String name = colorless.substring(11);
                    if (name.contains(", ")) {
                        String[] names = name.split(", ");
                        for (String n : names) {
                            if (isMatchingClass(n, player.getClassName())) {
                                hasRequirement = true;
                            }
                        }
                    }
                    else {
                        if (isMatchingClass(name, player.getClassName())) {
                            hasRequirement = true;
                        }
                    }
                }

                // Class exclusion
                else if (colorless.matches("Excluded Class: .+")) {
                    String name = colorless.substring(16);
                    if (name.contains(", ")) {
                        String[] names = name.split(", ");
                        for (String n : names) {
                            if (isMatchingClass(n, player.getClassName())) {
                                return true;
                            }
                        }
                    }
                    else {
                        if (isMatchingClass(name, player.getClassName())) {
                            return true;
                        }
                    }
                }
            }
        }
        return needsRequirement != hasRequirement;
    }

    /**
     * Checks if the player's class matches a required class
     *
     * @param req    required class
     * @param actual actual class
     * @return       true if matches, false otherwise
     */
    private static boolean isMatchingClass(String req, String actual) {

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
    private boolean getNextPlayer(Player[] players) {
        index++;

        // Limit the index
        if (index >= players.length) {
            players = plugin.getServer().getOnlinePlayers();
            index = 0;
        }

        // Make sure its a valid player
        return players.length > 0 && (players[index].isOnline() || getNextPlayer(players));
    }
}
