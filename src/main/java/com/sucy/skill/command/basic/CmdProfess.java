package com.sucy.skill.command.basic;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.language.CommandNodes;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Command to bind a skill to an item
 */
public class CmdProfess implements IFunction {

    /**
     * Executes the command
     *
     * @param command owning command
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    arguments
     */
    @Override
    public void execute(ConfigurableCommand command, Plugin plugin, CommandSender sender, String[] args) {

        SkillAPI api = (SkillAPI)plugin;
        PlayerSkills player = api.getPlayer((Player)sender);

        // Requires at least 1 argument
        if (args.length >= 1) {

            // Invalid class
            if (api.getClass(args[0]) == null) {
                String error = api.getMessage(CommandNodes.NOT_A_CLASS, true);
                error = error.replace("{class}", args[0]);
                sender.sendMessage(error);
            }

            // Profess
            else if (player.canProfess(args[0])) {
                args[0] = api.getClass(args[0]).getName();
                player.setClass(args[0]);
                List<String> messages = api.getMessages(CommandNodes.COMPLETE + CommandNodes.PROFESS, true);
                for (String message : messages) {
                    message = message.replace("{class}", api.getClass(args[0]).getName());
                    sender.sendMessage(message);
                }
            }

            // Unable to profess just yet
            else {
                String error = api.getMessage(CommandNodes.CANNOT_PROFESS, true);
                sender.sendMessage(error);
            }
        }

        // Invalid arguments
        else CommandManager.displayUsage(command, sender, 1);
    }
}
