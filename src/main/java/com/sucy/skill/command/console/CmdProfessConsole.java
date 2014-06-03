package com.sucy.skill.command.console;

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
import java.util.UUID;

/**
 * Command to bind a skill to an item
 */
public class CmdProfessConsole implements IFunction {

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

        // Requires at least 2 arguments
        if (args.length >= 2) {

            UUID id = api.getPlayerUUID(args[1]);
            PlayerSkills player = id == null ? null : api.getPlayer(id);

            // Invalid class
            if (api.getClass(args[0]) == null) {
                String error = api.getMessage(CommandNodes.NOT_A_CLASS, true);
                error = error.replace("{class}", args[0]);
                sender.sendMessage(error);
            }

            // Invalid player
            else if (player == null) {
                String error = api.getMessage(CommandNodes.NOT_A_PLAYER, true);
                error = error.replace("{player}", args[1]);
                sender.sendMessage(error);
            }

            // Profess
            else {

                // Must be able to profess to the class
                if (player.canProfess(args[0])) {
                    args[0] = api.getClass(args[0]).getName();
                    player.setClass(args[0]);

                    // Notify them of the profession
                    Player target = player.getPlayer();
                    List<String> messages = api.getMessages(CommandNodes.COMPLETE + CommandNodes.PROFESS, true);
                    for (String message : messages) {
                        message = message.replace("{class}", api.getClass(args[0]).getName());
                        target.sendMessage(message);
                    }
                }
            }
        }

        // Invalid arguments
        else CommandManager.displayUsage(command, sender, 1);
    }
}
