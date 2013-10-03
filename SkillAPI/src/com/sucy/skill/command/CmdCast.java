package com.sucy.skill.command;

import com.sucy.skill.PermissionNodes;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skill.*;
import com.sucy.skill.api.util.AttributeHelper;
import com.sucy.skill.language.CommandNodes;
import com.sucy.skill.skills.PlayerSkills;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Command to bind a skill to an item
 */
public class CmdCast implements ICommand {

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

        // Requires at least one argument
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

            // Cast the skill
            else {
                ClassSkill classSkill = api.getRegisteredSkill(skill);

                // Cannot cast a passive ability
                if (classSkill instanceof PassiveSkill || classSkill instanceof TargetSkill) {
                    String error = api.getMessage(CommandNodes.CANNOT_BE_CAST, true);
                    error = error.replace("{skill}", classSkill.getName());
                    sender.sendMessage(error);
                }

                // Cast the skill shot
                else {
                    ((SkillShot) classSkill).cast(plugin.getServer().getPlayer(sender.getName()),
                            player.getSkillLevel(classSkill.getName()));

                    List<String> messages = api.getMessages(CommandNodes.COMPLETE + CommandNodes.CAST, true);
                    for (String message : messages) {
                        message = message.replace("{skill}", classSkill.getName())
                                         .replace("{mana}", AttributeHelper.calculate(classSkill, DefaultAttribute.MANA, player.getSkillLevel(classSkill.getName())) + "")
                                         .replace("{cooldown}", AttributeHelper.calculate(classSkill, DefaultAttribute.COOLDOWN, player.getSkillLevel(classSkill.getName())) + "");
                        sender.sendMessage(message);
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
        return ((SkillAPI)plugin).getMessage(CommandNodes.ARGUMENTS + CommandNodes.CAST, true);
    }

    /**
     * @return the description of this command
     */
    @Override
    public String getDescription(Plugin plugin) {
        return ((SkillAPI)plugin).getMessage(CommandNodes.DESCRIPTION + CommandNodes.CAST, true);
    }

    /**
     * @return required sender type for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}
