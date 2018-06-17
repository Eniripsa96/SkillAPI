/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdForceProfess
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
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * A command that allows a player to profess through classes
 */
public class CmdForceProfess implements IFunction
{
    private static final String NOT_PLAYER     = "not-player";
    private static final String CANNOT_USE     = "cannot-use";
    private static final String INVALID_CLASS  = "invalid-class";
    private static final String SUCCESSS       = "success";
    private static final String PROFESSED      = "professed";
    private static final String CANNOT_PROFESS = "cannot-profess";
    private static final String DISABLED       = "world-disabled";

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
        if (args.length < 2)
        {
            CommandManager.displayUsage(cmd, sender);
        }
        else
        {
            OfflinePlayer player = VersionManager.getOfflinePlayer(args[0], false);
            if (player == null)
            {
                cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "That is not a valid player name");
                return;
            }

            String name = args[1];
            for (int i = 2; i < args.length; i++) name += ' ' + args[i];

            PlayerData data = SkillAPI.getPlayerData(player);
            RPGClass target = SkillAPI.getClass(name);

            // Invalid class
            if (target == null)
            {
                cmd.sendMessage(sender, INVALID_CLASS, ChatColor.RED + "That is not a valid class");
            }

            // Can profess
            else if (data.canProfess(target))
            {
                data.profess(target);
                if (player.isOnline())
                {
                    cmd.sendMessage(sender, SUCCESSS, ChatColor.GOLD + "{player}" + ChatColor.DARK_GREEN + " is now a " + ChatColor.GOLD + "{class}", Filter.PLAYER.setReplacement(player.getName()), RPGFilter.CLASS.setReplacement(target.getName()));
                    cmd.sendMessage((Player) player, PROFESSED, ChatColor.DARK_GREEN + "You are now a " + ChatColor.GOLD + "{class}", RPGFilter.CLASS.setReplacement(target.getName()));
                }
            }

            // Cannot profess
            else
            {
                cmd.sendMessage(sender, CANNOT_PROFESS, ChatColor.RED + "They cannot profess to this class currently");
            }
        }
    }
}
