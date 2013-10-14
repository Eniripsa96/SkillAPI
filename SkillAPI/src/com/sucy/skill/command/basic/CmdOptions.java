package com.sucy.skill.command.basic;

import com.sucy.skill.PermissionNodes;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.command.CommandHandler;
import com.sucy.skill.command.ICommand;
import com.sucy.skill.command.SenderType;
import com.sucy.skill.language.CommandNodes;
import com.sucy.skill.skills.PlayerSkills;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Command to bind a skill to an item
 */
public class CmdOptions implements ICommand {

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
                for (String tree : api.getChildren(player.getClassName(), plugin.getServer().getPlayer(player.getName()))) {
                    String copy = string.replace("{option}", tree);
                    sender.sendMessage(copy);
                }
            }

            // Without the option filter, display the message normally
            else sender.sendMessage(string);
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
        return ((SkillAPI)plugin).getMessage(CommandNodes.ARGUMENTS + CommandNodes.OPTIONS, true);
    }

    /**
     * @return the description of this command
     */
    @Override
    public String getDescription(Plugin plugin) {
        return ((SkillAPI)plugin).getMessage(CommandNodes.DESCRIPTION + CommandNodes.OPTIONS, true);
    }

    /**
     * @return required sender type for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}
