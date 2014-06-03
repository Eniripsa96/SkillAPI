package com.sucy.skill.command.basic;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.skill.PermissionNodes;
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
public class CmdInfo implements IFunction {

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
        PlayerSkills player;

        // 0 arguments for own info
        if (args.length == 0) {
            player = api.getPlayer((Player)sender);
        }

        // Not having the permission is not allowed
        else if (!sender.hasPermission(PermissionNodes.STATS)) {
            String error = api.getMessage(CommandNodes.CANNOT_SEE_STATS, true);
            sender.sendMessage(error);
            return;
        }

        // Otherwise get the target player
        else {
            UUID id = api.getPlayerUUID(args[0]);
            player = id == null ? null : api.getPlayer(id);
        }


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
            String base = CommandNodes.COMPLETE + CommandNodes.INFO;
            if (!player.hasClass()) messages = api.getMessages(base + CommandNodes.NO_CLASS, true);
            else messages = api.getMessages(base + CommandNodes.HAS_CLASS, true);

            // Filter and send the messages
            for (String string : messages) {
                string = string.replace("{player}", player.getPlayer().getName());
                if (player.hasClass())
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
}
