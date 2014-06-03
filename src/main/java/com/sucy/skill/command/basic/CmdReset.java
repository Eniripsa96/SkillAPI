package com.sucy.skill.command.basic;

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
public class CmdReset implements IFunction {

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

        // Requires a class to reset
        if (player.getClassName() != null) {

            List<String> messages;
            String base = CommandNodes.COMPLETE + CommandNodes.RESET;

            // If confirmed, reset their stats
            if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
                player.setClass(null);
                messages = api.getMessages(base + CommandNodes.CONFIRMED, true);
            }

            // Otherwise prompt for them to confirm
            else messages = api.getMessages(base + CommandNodes.NOT_CONFIRMED, true);

            // Display the messages
            for (String message : messages) {
                sender.sendMessage(message);
            }
        }

        // No class
        else {
            String error = api.getMessage(CommandNodes.NO_CHOSEN_CLASS, true);
            sender.sendMessage(error);
        }
    }
}
