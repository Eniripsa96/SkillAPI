/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdForceAttr
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
import com.rit.sucy.config.Filter;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * A command that resets the attributes of a player
 */
public class CmdForceAttr implements IFunction
{
    private static final String NOT_PLAYER = "not-player";
    private static final String RESET      = "reset";
    private static final String RESET_ONE  = "reset-one";
    private static final String NOT_ATTR   = "not-attribute";
    private static final String NOT_NUM    = "not-number";
    private static final String GAVE_ATTR  = "gave-attributes";

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
        // Only players have profession options
        if (args.length < 1)
        {
            CommandManager.displayUsage(cmd, sender);
            return;
        }

        // Grab the player data
        OfflinePlayer player = VersionManager.getOfflinePlayer(args[0], false);
        if (player == null)
        {
            cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "That is not a valid player name");
            return;
        }
        PlayerData data = SkillAPI.getPlayerData(player);

        // Reset their attributes
        if (args.length == 1)
        {
            data.refundAttributes();
            cmd.sendMessage(sender, RESET, ChatColor.GOLD + "{player}'s " + ChatColor.DARK_GREEN + "attributes were refunded", Filter.PLAYER.setReplacement(args[0]));
            return;
        }

        // Validate the attribute
        if (SkillAPI.getAttributeManager().getAttribute(args[1]) == null)
        {
            cmd.sendMessage(sender, NOT_ATTR, ChatColor.GOLD + "{name}" + ChatColor.RED + " is not a valid attribute name", RPGFilter.NAME.setReplacement(args[1]));
            return;
        }

        // Reset a specific attribute
        if (args.length == 2)
        {
            data.refundAttributes(args[1]);
            cmd.sendMessage(sender, RESET_ONE, ChatColor.GOLD + "{player}'s {name}" + ChatColor.DARK_GREEN + " attributes were refunded", Filter.PLAYER.setReplacement(args[0]), RPGFilter.NAME.setReplacement(args[1]));
        }

        // Give a specific attribute
        else if (args.length >= 3)
        {
            try
            {
                int amount = Integer.parseInt(args[2]);
                data.giveAttribute(args[1], amount);
                cmd.sendMessage(sender, GAVE_ATTR, ChatColor.GOLD + "{player}" + ChatColor.DARK_GREEN + " was given " + ChatColor.GOLD + "{amount} {name} points", Filter.PLAYER.setReplacement(args[0]), RPGFilter.NAME.setReplacement(args[1]), Filter.AMOUNT.setReplacement(amount + ""));
            }
            catch (Exception ex)
            {
                cmd.sendMessage(sender, NOT_NUM, ChatColor.GOLD + "{amount} " + ChatColor.RED + "is not an integer number", Filter.AMOUNT.setReplacement(args[2]));
            }
        }
    }
}
