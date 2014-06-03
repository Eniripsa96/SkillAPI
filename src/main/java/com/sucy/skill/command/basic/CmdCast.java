package com.sucy.skill.command.basic;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.language.CommandNodes;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command to bind a skill to an item
 */
public class CmdCast implements IFunction {

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
        PlayerSkills player = api.getPlayer((Player)sender);

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
            else player.castSkill(skill);
        }

        // Invalid arguments
        else CommandManager.displayUsage(command, sender, 1);
    }
}
