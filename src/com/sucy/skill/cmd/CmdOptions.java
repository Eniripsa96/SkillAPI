package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

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
    private static final String NO_OPTIONS = "no-options";
    private static final String DISABLED   = "world-disabled";

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
        if (sender instanceof Player && !SkillAPI.getSettings().isWorldEnabled(((Player) sender).getWorld()))
        {
            cmd.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
        }

        // Only players have profession options
        else if (sender instanceof Player)
        {
            cmd.sendMessage(sender, TITLE, ChatColor.DARK_GRAY + "--" + ChatColor.DARK_GREEN + " Profess Options " + ChatColor.DARK_GRAY + "-----------");
            PlayerData data = SkillAPI.getPlayerData((Player) sender);
            String categoryTemplate = cmd.getMessage(CATEGORY, ChatColor.GOLD + "{category}" + ChatColor.GRAY + ": ");
            String optionTemplate = cmd.getMessage(OPTION, ChatColor.LIGHT_PURPLE + "{option}" + ChatColor.GRAY);
            String separator = cmd.getMessage(SEPARATOR, ChatColor.DARK_GRAY + "----------------------------");
            String none = cmd.getMessage(NO_OPTIONS, ChatColor.GRAY + "None");
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

                    // Get the options list
                    List<RPGClass> options;
                    if (c != null)
                    {
                        options = c.getData().getOptions();
                    }
                    else
                    {
                        options = SkillAPI.getBaseClasses(group);
                    }

                    // Compose the message
                    String list = categoryTemplate.replace("{category}", TextFormatter.format(group));
                    boolean firstOption = true;
                    for (RPGClass option : options)
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
                    if (options.size() == 0)
                    {
                        list += none;
                    }

                    // Send the result
                    sender.sendMessage(list);
                }
            }
            cmd.sendMessage(sender, END, ChatColor.DARK_GRAY + "----------------------------");
        }

        // Console doesn't have profession options
        else
        {
            cmd.sendMessage(sender, CANNOT_USE, ChatColor.RED + "This cannot be used by the console");
        }
    }
}
