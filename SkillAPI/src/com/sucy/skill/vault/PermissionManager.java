package com.sucy.skill.vault;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Manages setting permissions through vault
 */
public class PermissionManager {

    private static Permission permission;

    /**
     * Initializes the permissions manager
     */
    public static void initialize() {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
    }

    /**
     * Adds a permission to the player
     *
     * @param player player to add to
     * @param node   permission node to add
     */
    public static void add(Player player, String node) {
        if (!has(player, node)) {
            permission.playerAdd(player, node);
        }
    }

    /**
     * Removes a permission from the player
     *
     * @param player player to remove from
     * @param node   permission node to remove
     */
    public static void remove(Player player, String node) {
        if (has(player, node)) {
            permission.playerRemove(player, node);
        }
    }

    /**
     * Checks whether or not the player has the permission
     *
     * @param player player to check for
     * @param node   permission node to remove
     * @return       true if the player has it, false otherwise
     */
    public static boolean has(Player player, String node) {
        return permission.has(player, node);
    }
}
