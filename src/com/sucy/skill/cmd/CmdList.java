package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.config.Filter;
import com.rit.sucy.player.PlayerUUIDs;
import com.rit.sucy.text.TextFormatter;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * A command that displays a player's account information
 */
public class CmdList implements IFunction
{
    private static final String NEEDS_ARGS = "needs-player";
    private static final String TITLE      = "title";
    private static final String LINE       = "line";
    private static final String END        = "end";
    private static final String NOT_PLAYER = "not-player";

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
        if (sender instanceof Player || args.length >= 1)
        {
            OfflinePlayer target = args.length == 0 ? (OfflinePlayer) sender : VersionManager.getOfflinePlayer(args[0], false);
            if (target == null)
            {
                cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "That is not a valid player name");
                return;
            }

            PlayerAccounts accounts = SkillAPI.getPlayerAccountData(target);
            cmd.sendMessage(sender, TITLE, ChatColor.DARK_GRAY + "--" + ChatColor.DARK_GREEN + " {player} " + ChatColor.DARK_GRAY + "-----------", Filter.PLAYER.setReplacement(target.getName()));
            String line = cmd.getMessage(LINE, ChatColor.GRAY + "[" + ChatColor.GOLD + "{id}" + ChatColor.GRAY + "] " + ChatColor.DARK_GREEN + "Lv" + ChatColor.GOLD + "{level} {class}");
            if (accounts != null)
            {
                for (int i = 1; i <= accounts.getAccountLimit(); i++)
                {
                    PlayerData data = accounts.getData(i);
                    PlayerClass cData = data == null ? null : data.getMainClass();
                    String name = cData == null ? ChatColor.GRAY + "Not Professed" : cData.getData().getPrefix();
                    String level = cData == null ? "0" : cData.getLevel() + "";
                    sender.sendMessage(line
                        .replace("{id}", i + "")
                        .replace("{level}", level)
                        .replace("{class}", name)
                    );
                }
            }
            cmd.sendMessage(sender, END, ChatColor.DARK_GRAY + "----------------------------");
        }

        // Console doesn't have profession options
        else
        {
            cmd.sendMessage(sender, NEEDS_ARGS, ChatColor.RED + "A player name is required from the console");
        }
    }
}
