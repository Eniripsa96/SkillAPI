/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdLevel
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
import com.rit.sucy.config.parse.NumberParser;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.language.RPGFilter;
import com.sucy.skill.manager.CmdManager;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.regex.Pattern;

/**
 * A command that gives a player class levels
 */
public class CmdLevel implements IFunction
{
    private static final Pattern IS_NUMBER = Pattern.compile("[0-9]+");
    private static final Pattern IS_BOOL   = Pattern.compile("(true)|(false)");

    private static final String NOT_PLAYER     = "not-player";
    private static final String NOT_POSITIVE   = "not-positive";
    private static final String GAVE_LEVEL     = "gave-level";
    private static final String RECEIVED_LEVEL = "received-level";
    private static final String DISABLED       = "world-disabled";
    private static final String NO_CLASSES     = "no-classes";

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
        else if ((args.length >= 1 && sender instanceof Player && IS_NUMBER.matcher(args[0]).matches())
                || (args.length >= 2 && !IS_NUMBER.matcher(args[0]).matches()))
        {
            int numberIndex = IS_NUMBER.matcher(args[0]).matches() ? 0 : 1;
            if (args.length > 1 && IS_NUMBER.matcher(args[1]).matches()) numberIndex = 1;

            // Get the player data
            OfflinePlayer target = numberIndex == 0 ? (OfflinePlayer) sender : VersionManager.getOfflinePlayer(args[0], false);
            if (target == null)
            {
                cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "That is not a valid player name");
                return;
            }
            PlayerData data = SkillAPI.getPlayerData(target);

            // Parse the levels
            int amount;
            amount = NumberParser.parseInt(args[numberIndex]);

            // Invalid amount of levels
            if (amount <= 0)
            {
                cmd.sendMessage(sender, NOT_POSITIVE, ChatColor.RED + "You must give a positive amount of levels");
                return;
            }

            int lastArg = args.length - 1;
            boolean message = IS_BOOL.matcher(args[lastArg]).matches();
            boolean showMessage = !message || Boolean.parseBoolean(args[lastArg]);
            if (message) lastArg--;


            // Give levels to a specific class group
            boolean success;
            if (numberIndex + 1 <= lastArg)
            {
                PlayerClass playerClass = data.getClass(CmdManager.join(args, numberIndex + 1, lastArg));
                if (playerClass == null) {
                    CommandManager.displayUsage(cmd, sender);
                    return;
                }

                playerClass.giveLevels(amount);
                success = true;
            }

            // Give levels
            else
                success = data.giveLevels(amount, ExpSource.COMMAND);

            // Messages
            if (showMessage) {
                if (!success) {
                    cmd.sendMessage(
                            sender,
                            NO_CLASSES,
                            ChatColor.RED + "You aren't professed as a class that receives experience from commands",
                            Filter.PLAYER.setReplacement(target.getName()),
                            RPGFilter.LEVEL.setReplacement("" + amount)
                    );
                }
                else if (target != sender) {
                    cmd.sendMessage(
                            sender,
                            GAVE_LEVEL,
                            ChatColor.DARK_GREEN + "You have given " + ChatColor.GOLD + "{player} {level} levels",
                            Filter.PLAYER.setReplacement(target.getName()),
                            RPGFilter.LEVEL.setReplacement("" + amount));
                }
                if (target.isOnline()) {
                    cmd.sendMessage(
                            target.getPlayer(),
                            RECEIVED_LEVEL,
                            ChatColor.DARK_GREEN + "You have received " + ChatColor.GOLD + "{level} levels " + ChatColor.DARK_GREEN + "from " + ChatColor.GOLD + "{player}",
                            Filter.PLAYER.setReplacement(sender.getName()),
                            RPGFilter.LEVEL.setReplacement("" + amount));
                }
            }
        }

        // Not enough arguments
        else
        {
            CommandManager.displayUsage(cmd, sender);
        }
    }
}
