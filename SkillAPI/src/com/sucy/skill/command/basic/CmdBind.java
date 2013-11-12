package com.sucy.skill.command.basic;

import com.sucy.skill.PermissionNodes;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.command.CommandHandler;
import com.sucy.skill.command.ICommand;
import com.sucy.skill.command.SenderType;
import com.sucy.skill.language.CommandNodes;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Command to bind a skill to an item
 */
public class CmdBind implements ICommand {

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

            // Get the skill name
            String skill = args[0];
            for (int i = 1; i < args.length; i++) skill += " " + args[i];

            // Invalid skill
            if (!api.isSkillRegistered(skill)) {
                String error = api.getMessage(CommandNodes.NOT_A_SKILL, true);
                error = error.replace("{skill}", skill);
                sender.sendMessage(error);
            }

            // Player doesn't have the skill
            else if (!player.hasSkill(skill) || player.getSkillLevel(skill) == 0) {
                String error = api.getMessage(CommandNodes.SKILL_NOT_OWNED, true);
                error = error.replace("{skill}", skill);
                sender.sendMessage(error);
            }

            // No held item
            else if (((Player) sender).getItemInHand().getType() == Material.AIR) {
                String error = api.getMessage(CommandNodes.NO_HELD_ITEM, true);
                sender.sendMessage(error);
            }

            // Bind the skill to the held item
            else {
                player.bind(((Player) sender).getItemInHand().getType(), skill);
                List<String> messages = api.getMessages(CommandNodes.COMPLETE + CommandNodes.BIND, true);
                for (String message : messages) {
                    message = message.replace("{skill}", skill)
                                     .replace("{item}", ((Player) sender).getItemInHand().getType().name());
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
        return PermissionNodes.BASIC;
    }

    /**
     * @return arguments used by this command
     */
    @Override
    public String getArgsString(Plugin plugin) {
        return ((SkillAPI)plugin).getMessage(CommandNodes.ARGUMENTS + CommandNodes.BIND, true);
    }

    /**
     * @return the description of this command
     */
    @Override
    public String getDescription(Plugin plugin) {
        return ((SkillAPI)plugin).getMessage(CommandNodes.DESCRIPTION + CommandNodes.BIND, true);
    }

    /**
     * @return required sender type for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}
