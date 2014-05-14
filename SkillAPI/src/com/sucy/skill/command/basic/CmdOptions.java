package com.sucy.skill.command.basic;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.language.CommandNodes;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Command to bind a skill to an item
 */
public class CmdOptions implements IFunction {

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
        int level = player.getProfessionLevel();

        // Get the messages
        List<String> messages;
        String base = CommandNodes.COMPLETE + CommandNodes.OPTIONS;
        if (level < 1) messages = api.getMessages(base + CommandNodes.NO_OPTIONS, true);
        else messages = api.getMessages(base + CommandNodes.HAS_OPTIONS, true);

        // Filter and send the messages
        for (String string : messages) {
            string = string.replace("{level}", level + "");

            // Option filters display all options
            if (string.contains("{option}")) {
                for (String tree : api.getChildren(player.getClassName(), player.getPlayer())) {
                    String copy = string.replace("{option}", tree);
                    sender.sendMessage(copy);
                }
            }

            // Without the option filter, display the message normally
            else sender.sendMessage(string);
        }
    }
}
