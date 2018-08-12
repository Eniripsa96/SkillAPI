/**
 * SkillAPI
 * com.sucy.skill.hook.PluginChecker
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
     * Checks whether or not Lib's Disguises is active
     *
     * @return true if active
     */
    public static boolean isDisguiseActive()
    {
        return Bukkit.getPluginManager().getPlugin("LibsDisguises") != null;
    }

    /**
     * Checks whether or not NoCheatPlus is active on the server
     *
     * @return true if active, false otherwise
     */
    public static boolean isNoCheatActive()
    {
        return Bukkit.getPluginManager().getPlugin("NoCheatPlus") != null;
    }

    public static boolean isMythicMobsActive() {
        return Bukkit.getPluginManager().isPluginEnabled("MythicMobs");
    }

    public static boolean isWorldGuardActive() {
        return Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
    }
}
