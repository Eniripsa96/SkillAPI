/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdAP
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
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * A command that gives a player class experience
 */
public class CmdAP implements IFunction
{
    private static final String NOT_PLAYER   = "not-player";
    private static final String NOT_NUMBER   = "not-number";
    private static final String NOT_POSITIVE = "not-positive";
    private static final String GAVE_AP      = "gave-points";
    private static final String RECEIVED_AP  = "received-points";
    private static final String DISABLED     = "world-disabled";

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
        // Disabled world
        if (sender instanceof Player && !SkillAPI.getSettings().isWorldEnabled(((Player) sender).getWorld()) && args.length == 1)
        {
            cmd.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
        }

        // Only can show info of a player so console needs to provide a name
        else if (args.length >= 1 && (args.length >= 2 || sender instanceof Player))
        {
            // Get the player data
            OfflinePlayer target = args.length == 1 ? (OfflinePlayer) sender : VersionManager.getOfflinePlayer(args[0], false);
            if (target == null)
            {
                cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "That is not a valid player name");
                return;
            }

            // Parse the skill points
            int amount;
            try
            {
                amount = Integer.parseInt(args[args.length == 1 ? 0 : 1]);
            }
            catch (Exception ex)
            {
                cmd.sendMessage(sender, NOT_NUMBER, ChatColor.RED + "That is not a valid skill point amount");
                return;
            }

            // Invalid amount of skill points
            if (amount <= 0)
            {
                cmd.sendMessage(sender, NOT_POSITIVE, ChatColor.RED + "You must give a positive amount of skill points");
                return;
            }

            // Give skill points
            PlayerData data = SkillAPI.getPlayerData(target);
            data.giveAttribPoints(amount);

            // Messages
            if (target != sender)
            {
                cmd.sendMessage(sender, GAVE_AP, ChatColor.DARK_GREEN + "You have given " + ChatColor.GOLD + "{player} {points} attribute points", Filter.PLAYER.setReplacement(target.getName()), RPGFilter.POINTS.setReplacement("" + amount));
            }
            if (target.isOnline())
            {
                cmd.sendMessage(target.getPlayer(), RECEIVED_AP, ChatColor.DARK_GREEN + "You have received " + ChatColor.GOLD + "{points} attribute points " + ChatColor.DARK_GREEN + "from " + ChatColor.GOLD + "{player}", Filter.PLAYER.setReplacement(sender.getName()), RPGFilter.POINTS.setReplacement("" + amount));
            }
        }

        // Not enough arguments
        else
        {
            CommandManager.displayUsage(cmd, sender);
        }
    }
}
