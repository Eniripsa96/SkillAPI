package com.sucy.skill.command.console;

import com.sucy.skill.PermissionNodes;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.command.CommandHandler;
import com.sucy.skill.command.ICommand;
import com.sucy.skill.command.SenderType;
import com.sucy.skill.language.CommandNodes;
import com.sucy.skill.version.VersionPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

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
        return ((SkillAPI)plugin).getMessage(CommandNodes.ARGUMENTS + CommandNodes.ADMIN_PROFESS, true);
    }

    /**
     * @return the description of this command
     */
    @Override
    public String getDescription(Plugin plugin) {
        return ((SkillAPI)plugin).getMessage(CommandNodes.DESCRIPTION + CommandNodes.ADMIN_PROFESS, true);
    }

    /**
     * @return required sender type for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.CONSOLE_ONLY;
    }
}
