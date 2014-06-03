package com.sucy.skill.vault;

import org.bukkit.Bukkit;

/**
 * Helper class for checking for Vault
 */
public class VaultChecker {

    /**
     * Checks if vault is active on the server
     *
     * @return true if active, false otherwise
     */
    public static boolean isVaultActive() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            return PermissionManager.isValid();
        }
        else return false;
    }
}
