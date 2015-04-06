package com.sucy.skill.hook;

import org.bukkit.Bukkit;

/**
 * Handler for checking whether or not hooked plugins are present
 * and active before using related code.
 */
public class PluginChecker
{
    /**
     * Checks if vault is active on the server
     *
     * @return true if active with permissions plugin, false otherwise
     */
    public static boolean isVaultActive()
    {
        return Bukkit.getPluginManager().getPlugin("Vault") != null && VaultHook.isValid();
    }

    /**
     * Checks whether or not BetonQuest is active on the server
     *
     * @return true if active, false otherwise
     */
    public static boolean isBetonActive()
    {
        return Bukkit.getPluginManager().getPlugin("BetonQuest") != null;
    }
}
