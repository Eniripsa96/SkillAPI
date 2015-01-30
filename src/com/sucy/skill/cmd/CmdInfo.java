package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.config.Filter;
import com.rit.sucy.text.TextFormatter;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * A command that displays a player's current class information
 */
public class CmdInfo implements IFunction
{
    private static final String NEEDS_ARGS = "needs-player";
    private static final String TITLE      = "title";
    private static final String CATEGORY   = "category";
    private static final String PROFESSION = "profession";
    private static final String EXP        = "exp";
    private static final String SEPARATOR  = "separator";
    private static final String END        = "end";
    private static final String NO_CLASS   = "no-class";
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

            PlayerData data = SkillAPI.getPlayerData(target);
            cmd.sendMessage(sender, TITLE, ChatColor.DARK_GRAY + "--" + ChatColor.DARK_GREEN + " {player} " + ChatColor.DARK_GRAY + "-----------", Filter.PLAYER.setReplacement(target.getName()));
            String separator = cmd.getMessage(SEPARATOR, ChatColor.DARK_GRAY + "----------------------------");
            boolean first = true;
            if (data != null)
            {
                for (String group : SkillAPI.getGroups())
                {
                    PlayerClass c = data.getClass(group);

                    // Separator message if not the first group
                    if (first)
                    {
                        first = false;
                    }
                    else
                    {
                        sender.sendMessage(separator);
                    }

                    // Compose the message
                    cmd.sendMessage(sender, CATEGORY, ChatColor.GOLD + "{group}" + ChatColor.GRAY + ": ", RPGFilter.GROUP.setReplacement(TextFormatter.format(group)));
                    PlayerClass profession = data.getClass(group);
                    if (profession == null)
                    {
                        cmd.sendMessage(sender, NO_CLASS, ChatColor.GRAY + "Not Professed");
                    }
                    else
                    {
                        cmd.sendMessage(sender, PROFESSION, ChatColor.AQUA + "Lv{level} " + ChatColor.DARK_GREEN + "{profession}", RPGFilter.LEVEL.setReplacement(profession.getLevel() + ""), RPGFilter.PROFESSION.setReplacement(profession.getData().getName()));
                        cmd.sendMessage(sender, EXP, ChatColor.AQUA + "Exp " + ChatColor.DARK_GREEN + "{exp}", RPGFilter.EXP.setReplacement((int) profession.getExp() + "/" + (int) profession.getRequiredExp()));
                    }
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
