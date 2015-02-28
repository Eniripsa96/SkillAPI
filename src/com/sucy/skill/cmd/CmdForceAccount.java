package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.config.Filter;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command to clear all bound skills
 */
public class CmdForceAccount implements IFunction
{
    private static final String NOT_PLAYER  = "not-player";
    private static final String NOT_ACCOUNT = "not-account";
    private static final String CHANGED     = "account-changed";
    private static final String TARGET      = "target-notice";

    /**
     * Executes the command
     *
     * @param command owning command
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    arguments
     */
    @Override
    public void execute(ConfigurableCommand command, Plugin plugin, CommandSender sender, String[] args)
    {
        // Needs two arguments
        if (args.length < 2)
        {
            command.displayHelp(sender);
        }

        // Switch accounts if valid number
        else
        {
            OfflinePlayer player = VersionManager.getOfflinePlayer(args[0], false);

            if (player == null)
            {
                command.sendMessage(sender, NOT_PLAYER, "&4That is not a valid player name");
                return;
            }

            PlayerAccounts accounts = SkillAPI.getPlayerAccountData(player);
            try
            {
                int id = Integer.parseInt(args[1]);

                if (accounts.getAccountLimit() >= id && id > 0)
                {
                    accounts.setAccount(id);
                    command.sendMessage(sender, CHANGED, ChatColor.GOLD + "{player}'s" + ChatColor.DARK_GREEN + " active account has been changed", Filter.PLAYER.setReplacement(player.getName()));
                    if (player.isOnline())
                    {
                        command.sendMessage((Player) player, TARGET, ChatColor.DARK_GREEN + "Your account has been forced to " + ChatColor.GOLD + "Account #{account}", RPGFilter.ACCOUNT.setReplacement(id + ""));
                    }
                    return;
                }
            }
            catch (Exception ex)
            {
                // Invalid ID
            }

            command.sendMessage(sender, NOT_ACCOUNT, ChatColor.RED + "That is not a valid account ID");
        }
    }
}