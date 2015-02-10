package com.sucy.skill.cmd;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * A command that allows a player to profess through classes
 */
public class CmdReset implements IFunction
{
    private static final String CANNOT_USE   = "cannot-use";
    private static final String RESET        = "reset";
    private static final String CONFIRM      = "confirm";
    private static final String INSTRUCTIONS = "instructions";
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
        if (sender instanceof Player && !SkillAPI.getSettings().isWorldEnabled(((Player) sender).getWorld()))
        {
            cmd.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
        }

        // Only players have profession options
        else if (sender instanceof Player)
        {
            if (args.length == 0 || !args[1].equalsIgnoreCase("confirm"))
            {
                cmd.sendMessage(sender, CONFIRM, ChatColor.DARK_RED + "This will delete your active account's data entirely");
                cmd.sendMessage(sender, INSTRUCTIONS, ChatColor.GRAY + "Type " + ChatColor.GOLD + "/class reset confirm" + ChatColor.GRAY + " to continue");
            }
            else
            {
                PlayerData data = SkillAPI.getPlayerData((Player) sender);
                data.resetAll();
                cmd.sendMessage(sender, RESET, ChatColor.DARK_GREEN + "You have reset your active account data");
            }
        }

        // Console doesn't have profession options
        else
        {
            cmd.sendMessage(sender, CANNOT_USE, ChatColor.RED + "This cannot be used by the console");
        }
    }
}
