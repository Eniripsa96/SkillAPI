package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * A command that displays the list of available profess options
 */
public class CmdOptions implements IFunction
{
    private static final String TITLE      = "title";
    private static final String CATEGORY   = "category";
    private static final String OPTION     = "option";
    private static final String SEPARATOR  = "separator";
    private static final String END        = "end";
    private static final String CANNOT_USE = "cannot-use";

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
        if (sender instanceof Player)
        {
            cmd.sendMessage(sender, TITLE, ChatColor.DARK_GREEN + "-- Profess Options -----------");
            PlayerData data = SkillAPI.getPlayerData((Player) sender);
            String categoryTemplate = cmd.getMessage(CATEGORY, ChatColor.GOLD + "{category}" + ChatColor.GRAY + ": ");
            String optionTemplate = cmd.getMessage(OPTION, ChatColor.LIGHT_PURPLE + "{option}" + ChatColor.GRAY);
            String separator = cmd.getMessage(SEPARATOR, ChatColor.DARK_GRAY + "----------------------------");
            boolean first = true;
            for (PlayerClass c : data.getClasses())
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    sender.sendMessage(separator);
                }
                String list = categoryTemplate.replace("{category}", c.getData().getGroup());
                boolean firstOption = true;
                for (RPGClass option : c.getData().getOptions())
                {
                    if (firstOption)
                    {
                        firstOption = false;
                    }
                    else
                    {
                        list += ", ";
                    }
                    list += optionTemplate.replace("{option}", option.getName());
                }
                sender.sendMessage(list);
            }
        }
        else
        {
            sender.sendMessage(cmd.getMessage(CANNOT_USE, ChatColor.RED + "This cannot be used by the console"));
        }
    }
}
