/**
 * SkillAPI
 * com.sucy.skill.gui.tool.InventoryData
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package com.sucy.skill.gui.tool;

import com.rit.sucy.version.VersionManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Handles keeping track of player inventory data when overwriting it
 * for tool GUIs, allowing the plugin to restore it as they close the menu.
 */
public class InventoryData
{
    private ItemStack[] main;
    private ItemStack[] armor;
    private ItemStack   sidearm;

    /**
     * Creates a backup of the player's inventory contents
     *
     * @param player player to make a backup for
     */
    public InventoryData(Player player)
    {
        main = player.getInventory().getContents();
        armor = player.getInventory().getArmorContents();
        if (VersionManager.isVersionAtLeast(VersionManager.V1_9_0))
            sidearm = player.getInventory().getItemInOffHand();
    }

    /**
     * Restores the player's inventory contents
     *
     * @param player player to restore for
     */
    public void restore(Player player)
    {
        player.getInventory().setContents(main);
        player.getInventory().setArmorContents(armor);
        if (VersionManager.isVersionAtLeast(VersionManager.V1_9_0))
            player.getInventory().setItemInOffHand(sidearm);
    }
}
