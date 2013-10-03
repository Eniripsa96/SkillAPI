package com.sucy.skill.command;

import com.sucy.skill.PermissionNodes;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.language.CommandNodes;
import com.sucy.skill.skills.PlayerSkills;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Command to bind a skill to an item
 */
public class CmdProfess implements ICommand {

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
        PlayerSkills player = api.getPlayer(sender.getName());

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
                player.setClass(args[0]);
                List<String> messages = api.getMessages(CommandNodes.COMPLETE + CommandNodes.PROFESS, true);
                for (String message : messages) {
                    message = message.replace("{class}", args[0]);
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
        else handler.displayUsage(sender);
    }

    /**
     * @return permission required for this command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNodes.BASIC;
    }

    /**
     * @return arguments used by this command
     */
    @Override
    public String getArgsString(Plugin plugin) {
        return ((SkillAPI)plugin).getMessage(CommandNodes.ARGUMENTS + CommandNodes.PROFESS, true);
    }

    /**
     * @return the description of this command
     */
    @Override
    public String getDescription(Plugin plugin) {
        return ((SkillAPI)plugin).getMessage(CommandNodes.DESCRIPTION + CommandNodes.PROFESS, true);
    }

    /**
     * @return required sender type for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}
