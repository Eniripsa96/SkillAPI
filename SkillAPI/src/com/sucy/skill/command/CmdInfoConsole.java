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
public class CmdInfoConsole implements ICommand {

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
        PlayerSkills player;

        // Not enough arguments
        if (args.length == 0) {
            handler.displayUsage(sender);
            return;
        }

        // Otherwise get the target player
        else player = api.getPlayer(args[0]);


        // Invalid player
        if (player == null) {
            String error = api.getMessage(CommandNodes.NOT_A_PLAYER, true);
            error = error.replace("{player}", args[0]);
            sender.sendMessage(error);
        }

        // Display the information
        else {

            // Get the messages
            List<String> messages;
            String base = CommandNodes.COMPLETE + CommandNodes.INFO_CONSOLE;
            if (player.getTree() == null) messages = api.getMessages(base + CommandNodes.NO_CLASS, true);
            else messages = api.getMessages(base + CommandNodes.HAS_CLASS, true);

            // Filter and send the messages
            for (String string : messages) {
                string = string.replace("{player}", player.getName())
                               .replace("{class}", player.getTree())
                               .replace("{level}", player.getLevel() + "")
                               .replace("{exp}", player.getExp() + "")
                               .replace("{points}", player.getPoints() + "");

                sender.sendMessage(string);
            }
        }
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
        return ((SkillAPI)plugin).getMessage(CommandNodes.ARGUMENTS + CommandNodes.INFO_CONSOLE, true);
    }

    /**
     * @return the description of this command
     */
    @Override
    public String getDescription(Plugin plugin) {
        return ((SkillAPI)plugin).getMessage(CommandNodes.DESCRIPTION + CommandNodes.INFO_CONSOLE, true);
    }

    /**
     * @return required sender type for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.CONSOLE_ONLY;
    }
}
