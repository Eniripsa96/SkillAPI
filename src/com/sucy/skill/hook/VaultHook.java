/**
 * SkillAPI
 * com.sucy.skill.hook.VaultHook
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

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Manages setting permissions through vault
 */
public class VaultHook
{

    private static Permission permission;
    private static boolean checked = false;

    /**
     * Initializes the permissions manager
     */
    private static void initialize()
    {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null)
        {
            permission = permissionProvider.getProvider();
        }
    }

    /**
     * Checks whether or not the Vault reference is valid with a permissions plugin
     *
     * @return true if valid, false otherwise
     */
    public static boolean isValid()
    {
        if (!checked)
        {
            initialize();
            checked = true;
        }
        return permission != null;
    }

    /**
     * Adds a permission to the player
     *
     * @param player player to add to
     * @param node   permission node to add
     */
    public static void add(Player player, String node)
    {
        permission.playerAdd(player, node);
    }

    /**
     * Removes a permission from the player
     *
     * @param player player to remove from
     * @param node   permission node to remove
     */
    public static void remove(Player player, String node)
    {
        permission.playerRemove(player, node);
    }

    /**
     * Checks whether or not the player has the permission
     *
     * @param player player to check for
     * @param node   permission node to remove
     *
     * @return true if the player has it, false otherwise
     */
    public static boolean has(Player player, String node)
    {
        return permission.has(player, node);
    }
}
