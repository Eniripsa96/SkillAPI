package com.sucy.skill.command.basic;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.language.CommandNodes;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Command to bind a skill to an item
 */
public class CmdUnbind implements IFunction {

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

        // No held item
        Material mat = ((Player)sender).getItemInHand().getType();
        if (mat == Material.AIR) {
            String error = api.getMessage(CommandNodes.NO_HELD_ITEM, true);
            sender.sendMessage(error);
        }

        // No bound skill
        else if (player.getBound(mat) == null) {
            String error = api.getMessage(CommandNodes.NO_BOUND_SKILL, true);
            error = error.replace("{item}", TextFormatter.format(mat.name()));
            sender.sendMessage(error);
        }

        // Bind the skill to the held item
        else {
            player.unbind(mat);
            List<String> messages = api.getMessages(CommandNodes.COMPLETE + CommandNodes.UNBIND, true);
            for (String message : messages) {
                message = message.replace("{item}", TextFormatter.format(mat.name()));
                sender.sendMessage(message);
            }
        }
    }
}
