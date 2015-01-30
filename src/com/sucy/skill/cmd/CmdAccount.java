package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command to clear all bound skills
 */
public class CmdAccount implements IFunction
{
    private static final String NOT_PLAYER  = "not-player";
    private static final String NOT_ACCOUNT = "not-account";
    private static final String CHANGED     = "account-changed";

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
        // Must be a player
        if (!(sender instanceof Player))
        {
            command.sendMessage(sender, NOT_PLAYER, "&4Only players can use this command");
        }

        // Needs an argument
        else if (args.length == 0)
        {
            command.displayHelp(sender);
        }

        // Switch accounts if valid number
        else
        {
            PlayerAccounts player = SkillAPI.getPlayerAccountData((Player) sender);

            try
            {
                int id = Integer.parseInt(args[0]);

                if (player.getAccountLimit() >= id && id > 0)
                {
                    player.setAccount(id);
                    command.sendMessage(sender, CHANGED, ChatColor.DARK_GREEN + "Your account has been changed");
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