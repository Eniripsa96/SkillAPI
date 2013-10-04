package com.sucy.skill.mccore;

import org.bukkit.Bukkit;

/**
 * Helper class for checking for MCCore
 */
public class CoreChecker {

    /**
     * Checks for MCCore
     */
    public static boolean isCoreActive() {
        return Bukkit.getPluginManager().isPluginEnabled("MCCore");
    }
}
