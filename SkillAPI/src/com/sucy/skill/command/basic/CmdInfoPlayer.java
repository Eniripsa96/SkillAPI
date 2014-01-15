package com.sucy.skill.command.basic;

import com.sucy.skill.PermissionNodes;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.command.CommandHandler;
import com.sucy.skill.command.ICommand;
import com.sucy.skill.command.SenderType;
import com.sucy.skill.language.CommandNodes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Command to bind a skill to an item
 */
public class CmdInfoPlayer implements ICommand {

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

        // 0 arguments for own info
        if (args.length == 0) {
            player = api.getPlayer(sender.getName());
        }

        // Not having the permission is not allowed
        else if (!sender.hasPermission(PermissionNodes.STATS)) {
            String error = api.getMessage(CommandNodes.CANNOT_SEE_STATS, true);
            sender.sendMessage(error);
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
            String base = CommandNodes.COMPLETE + CommandNodes.INFO_PLAYER;
            if (player.getClassName() == null) messages = api.getMessages(base + CommandNodes.NO_CLASS, true);
            else messages = api.getMessages(base + CommandNodes.HAS_CLASS, true);

            // Filter and send the messages
            for (String string : messages) {
                string = string.replace("{player}", player.getPlayer().getName());
                if (player.getClassName() != null)
                    string = string.replace("{class}", player.getClassName())
                                   .replace("{level}", player.getLevel() + "")
                                   .replace("{exp}", player.getExp() + "")
                                   .replace("{req-exp}", player.getRequiredExp() + "")
                                   .replace("{exp-left}", player.getExpToNextLevel() + "")
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
        return ((SkillAPI)plugin).getMessage(CommandNodes.ARGUMENTS + CommandNodes.INFO_PLAYER, true);
    }

    /**
     * @return the description of this command
     */
    @Override
    public String getDescription(Plugin plugin) {
        return ((SkillAPI)plugin).getMessage(CommandNodes.DESCRIPTION + CommandNodes.INFO_PLAYER, true);
    }

    /**
     * @return required sender type for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}
