package com.sucy.skill.command.admin;

import com.sucy.skill.PermissionNodes;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.command.CommandHandler;
import com.sucy.skill.command.ICommand;
import com.sucy.skill.command.SenderType;
import com.sucy.skill.language.CommandNodes;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Command to level a player up
 */
public class CmdLevelConsole implements ICommand {

    /**
     * Executes the command
     *
     * @param handler handler for the command
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        SkillAPI api = (SkillAPI)plugin;

        // Requires at least 1 argument
        if (args.length >= 2) {
            PlayerSkills player;

            // Get the target
            player = api.getPlayer(args[1]);

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
                List<String> messages = api.getMessages(CommandNodes.COMPLETE + CommandNodes.LEVEL_CONSOLE, true);
                for (String message : messages) {
                    message = message.replace("{player}", player.getName())
                                     .replace("{amount}", amount + "")
                                     .replace("{level}", player.getLevel() + "");

                    sender.sendMessage(message);
                }
            }
        }

        // Incorrect arguments
        else handler.displayUsage(sender);
    }

    /**
     * @return permission required for this command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNodes.LEVEL;
    }

    /**
     * @return arguments used by this command
     */
    @Override
    public String getArgsString(Plugin plugin) {
        return ((SkillAPI)plugin).getMessage(CommandNodes.ARGUMENTS + CommandNodes.LEVEL_CONSOLE, true);
    }

    /**
     * @return the description of this command
     */
    @Override
    public String getDescription(Plugin plugin) {
        return ((SkillAPI)plugin).getMessage(CommandNodes.DESCRIPTION + CommandNodes.LEVEL_CONSOLE, true);
    }

    /**
     * @return required sender type for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.CONSOLE_ONLY;
    }
}
