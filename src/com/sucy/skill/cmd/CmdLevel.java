package com.sucy.skill.cmd;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.config.Filter;
import com.rit.sucy.player.PlayerUUIDs;
import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * A command that gives a player class levels
 */
public class CmdLevel implements IFunction
{
    private static final String NOT_PLAYER     = "not-player";
    private static final String NOT_NUMBER     = "not-number";
    private static final String NOT_POSITIVE   = "not-positive";
    private static final String GAVE_LEVEL     = "gave-level";
    private static final String RECEIVED_LEVEL = "received-level";

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
        // Only can show info of a player so console needs to provide a name
        if (args.length >= 1 && (args.length >= 2 || sender instanceof Player))
        {
            // Get the player data
            OfflinePlayer target = args.length == 1 ? (OfflinePlayer) sender : PlayerUUIDs.getOfflinePlayer(args[0]);
            if (target == null)
            {
                cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "That is not a valid player name");
                return;
            }

            // Parse the level
            int amount = 0;
            try
            {
                amount = Integer.parseInt(args[args.length == 1 ? 0 : 1]);
            }
            catch (Exception ex)
            {
                cmd.sendMessage(sender, NOT_NUMBER, ChatColor.RED + "That is not a valid level amount");
                return;
            }

            // Invalid amount of levels
            if (amount <= 0)
            {
                cmd.sendMessage(sender, NOT_POSITIVE, ChatColor.RED + "You must give a positive amount of levels");
                return;
            }

            // Give levels
            PlayerData data = SkillAPI.getPlayerData(target);
            data.giveLevels(amount, ExpSource.COMMAND);

            // Messages
            if (target != sender)
            {
                cmd.sendMessage(sender, GAVE_LEVEL, ChatColor.DARK_GREEN + "You have given " + ChatColor.GOLD + "{player} {level} levels", Filter.PLAYER.setReplacement(target.getName()), RPGFilter.LEVEL.setReplacement("" + amount));
            }
            if (target.isOnline())
            {
                cmd.sendMessage(target.getPlayer(), RECEIVED_LEVEL, ChatColor.DARK_GREEN + "You have received " + ChatColor.GOLD + "{level} levels " + ChatColor.DARK_GREEN + "from " + ChatColor.GOLD + "{player}", Filter.PLAYER.setReplacement(sender.getName()), RPGFilter.LEVEL.setReplacement("" + amount));
            }
        }

        // Not enough arguments
        else
        {
            CommandManager.displayUsage(cmd, sender);
        }
    }
}
