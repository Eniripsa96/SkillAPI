package com.sucy.skill.command.console;

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
 * Command to bind a skill to an item
 */
public class CmdReset implements ICommand {

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

        // Not enough args
        if (args.length == 0) {
            handler.displayUsage(sender);
        }

        // Invalid player
        else if (!api.getServer().getOfflinePlayer(args[0]).hasPlayedBefore()) {
            String error = api.getMessage(CommandNodes.NOT_A_PLAYER, true);
            error = error.replace("{player}", args[1]);
            sender.sendMessage(error);
        }

        // Try to reset the player
        else {

            // Reset the player's stats
            PlayerSkills data = api.getPlayer(args[0]);
            if (data.hasClass()) {
                data.setClass(null);
                List<String> messages = api.getMessages(CommandNodes.COMPLETE + CommandNodes.RESET, true);
                for (String message : messages) {
                    sender.sendMessage(message);
                }
            }
        }
    }

    /**
     * @return permission required for this command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNodes.RESET;
    }

    /**
     * @return arguments used by this command
     */
    @Override
    public String getArgsString(Plugin plugin) {
        return ((SkillAPI)plugin).getMessage(CommandNodes.ARGUMENTS + CommandNodes.ADMIN_RESET, true);
    }

    /**
     * @return the description of this command
     */
    @Override
    public String getDescription(Plugin plugin) {
        return ((SkillAPI)plugin).getMessage(CommandNodes.DESCRIPTION + CommandNodes.ADMIN_RESET, true);
    }

    /**
     * @return required sender type for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.CONSOLE_ONLY;
    }
}
