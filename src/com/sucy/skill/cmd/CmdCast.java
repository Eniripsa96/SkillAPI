package com.sucy.skill.cmd;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command to bind a skill to an item
 */
public class CmdCast implements IFunction
{

    private static final String NOT_SKILL    = "not-skill";
    private static final String NOT_UNLOCKED = "not-unlocked";
    private static final String NOT_PLAYER   = "not-player";
    private static final String DISABLED     = "world-disabled";

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
        // Player only command
        if (!(sender instanceof Player))
        {
            command.sendMessage(sender, NOT_PLAYER, "&4Only players can use this command");
        }

        // Disabled world
        else if (!SkillAPI.getSettings().isWorldEnabled(((Player) sender).getWorld()))
        {
            command.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
        }

        // Requires at least one argument
        else if (args.length >= 1)
        {

            PlayerData player = SkillAPI.getPlayerData((Player) sender);

            // Get the skill name
            String skill = args[0];
            for (int i = 1; i < args.length; i++)
            {
                skill += " " + args[i];
            }

            // Invalid skill
            if (!SkillAPI.isSkillRegistered(skill))
            {
                command.sendMessage(sender, NOT_SKILL, ChatColor.RED + "That is not a valid skill name");
            }

            // Player doesn't have the skill
            else if (!player.hasSkill(skill) || player.getSkillLevel(skill) == 0)
            {
                command.sendMessage(sender, NOT_UNLOCKED, ChatColor.RED + "You cannot cast that skill");
            }

            // Cast the skill
            else
            {
                player.cast(skill);
            }
        }

        // Invalid arguments
        else
        {
            CommandManager.displayUsage(command, sender, 1);
        }
    }
}
