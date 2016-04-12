/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdLore
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
package com.sucy.skill.cmd;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.text.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * A command that gives a player class experience
 */
public class CmdLore implements IFunction
{
    private static final String NOT_PLAYER = "not-player";
    private static final String NO_ITEM    = "no-item";
    private static final String LORE_ADDED = "lore-added";

    /**
     * Runs the command
     *
     * @param cmd    command that was executed
     * @param plugin plugin reference
     * @param sender sender of the command
     * @param args   argument list
     */
    @Override
    public void execute(ConfigurableCommand cmd, Plugin plugin, CommandSender sender, String[] args)
    {
        // Must be a player with an argument
        if (args.length >= 1 && sender instanceof Player)
        {
            Player player = (Player) sender;
            ItemStack held = player.getInventory().getItemInHand();

            // No held item
            if (held == null)
            {
                cmd.sendMessage(sender, NO_ITEM, ChatColor.RED + "You are not holding an item");
                return;
            }

            ItemMeta meta = held.getItemMeta();
            List<String> lore = meta.getLore();
            if (lore == null) lore = new ArrayList<String>();
            String combined = args[0];
            for (int i = 1; i < args.length; i++) combined += " " + args[i];
            lore.add(TextFormatter.colorString(combined));
            meta.setLore(lore);
            held.setItemMeta(meta);

            // Messages
            cmd.sendMessage(sender, LORE_ADDED, ChatColor.DARK_GREEN + "The lore has been added to your item");
        }

        // Not a player
        else if (!(sender instanceof Player))
        {
            cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "Only players can use that command");
        }

        // Not enough arguments
        else
        {
            CommandManager.displayUsage(cmd, sender);
        }
    }
}
