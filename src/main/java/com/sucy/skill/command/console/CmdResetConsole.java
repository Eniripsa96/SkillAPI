package com.sucy.skill.command.console;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.language.CommandNodes;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

/**
 * Command to bind a skill to an item
 */
public class CmdResetConsole implements IFunction {

    /**
     * Executes the command
     *
     * @param command handler for the command
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    arguments
     */
    @Override
    public void execute(ConfigurableCommand command, Plugin plugin, CommandSender sender, String[] args) {

        SkillAPI api = (SkillAPI)plugin;

        // Not enough args
        if (args.length == 0) {
            CommandManager.displayUsage(command, sender, 1);
        }

        // Invalid player
        else {
            UUID id = api.getPlayerUUID(args[0]);
            PlayerSkills player = id == null ? null : api.getPlayer(id);

            // Invalid player
            if (player == null) {
                String error = api.getMessage(CommandNodes.NOT_A_PLAYER, true);
                error = error.replace("{player}", args[1]);
                sender.sendMessage(error);
            }

            // No class chosen
            else if (!player.hasClass()) {
                String error = api.getMessage(CommandNodes.NO_CHOSEN_CLASS, true);
                error = error.replace("{player}", args[1]);
                sender.sendMessage(error);
            }

            // Reset the player
            else {
                player.setClass(null);
                List<String> messages = api.getMessages(CommandNodes.COMPLETE + CommandNodes.RESET, true);
                for (String message : messages) {
                    sender.sendMessage(message);
                }
            }
        }
    }
}
