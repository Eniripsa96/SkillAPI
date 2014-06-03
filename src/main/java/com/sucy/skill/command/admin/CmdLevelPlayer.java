package com.sucy.skill.command.admin;

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
 * Command to level a player up
 */
public class CmdLevelPlayer implements IFunction {

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

        // Requires at least 1 argument
        if (args.length >= 1) {
            PlayerSkills player;

            // Get the target
            if (args.length == 1) player = api.getPlayer((Player)sender);
            else {
                UUID id = api.getPlayerUUID(args[1]);
                player = id == null ? null : api.getPlayer(id);
            }

            // Get the amount
            int amount = 0;
            try {
                amount = Integer.parseInt(args[0]);
            }
            catch (Exception ex) {
                // Do nothing
            }

            // Invalid amount
            if (amount <= 0) {
                String error = api.getMessage(CommandNodes.NOT_POSITIVE, true);
                error = error.replace("{value}", args[0]);
                sender.sendMessage(error);
            }

            // Invalid target
            else if (player == null) {
                String error = api.getMessage(CommandNodes.NOT_A_PLAYER, true);
                error = error.replace("{player}", args[1]);
                sender.sendMessage(error);
            }

            // Target doesn't have a class
            else if (player.getClassName() == null) {
                String error = api.getMessage(CommandNodes.CANNOT_LEVEL, true);
                error = error.replace("{player}", player.getName());
                sender.sendMessage(error);
            }

            // Target is max level
            else if (player.getLevel() >= api.getClass(player.getClassName()).getMaxLevel()) {
                String error = api.getMessage(CommandNodes.MAX_LEVEL, true);
                error = error.replace("{player}", player.getName())
                        .replace("{level}", player.getLevel() + "");

                sender.sendMessage(error);
            }

            // Give them the levels
            else {
                player.levelUp(amount);

                // Confirmation message
                List<String> messages = api.getMessages(CommandNodes.COMPLETE + CommandNodes.LEVEL, true);
                for (String message : messages) {
                    message = message.replace("{player}", player.getName())
                                     .replace("{amount}", amount + "")
                                     .replace("{level}", player.getLevel() + "");

                    sender.sendMessage(message);
                }
            }
        }

        // Incorrect arguments
        else CommandManager.displayUsage(command, sender, 1);
    }
}
