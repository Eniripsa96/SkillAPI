package com.sucy.skill.command.basic;

import com.sucy.skill.PermissionNodes;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.command.CommandHandler;
import com.sucy.skill.command.ICommand;
import com.sucy.skill.command.SenderType;
import com.sucy.skill.language.CommandNodes;
import com.sucy.skill.skillbar.PlayerSkillBar;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Command to bind a skill to an item
 */
public class CmdToggleBar implements ICommand {

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

        // Player must have a class
        if (!player.hasClass()) {
            sender.sendMessage(api.getMessage(CommandNodes.NO_CHOSEN_CLASS, true));
        }

        // Cannot be in creative mode
        else if (player.getPlayer().getGameMode() == GameMode.CREATIVE) {
            sender.sendMessage(api.getMessage(CommandNodes.NO_CREATIVE, true));
        }
        else {
            PlayerSkillBar bar = api.getSkillBar(player.getPlayer());

            // Not enough space
            if (!bar.isEnabled() && bar.countOpenSlots() < bar.getItemsInSkillSlots()) {
                sender.sendMessage(api.getMessage(CommandNodes.NO_SPACE, true));
                return;
            }

            bar.toggleEnabled();
            String base = CommandNodes.COMPLETE + CommandNodes.TOGGLE_BAR;
            if (bar.isEnabled()) {
                List<String> messages = api.getMessages(base + CommandNodes.ON, true);
                for (String message : messages) {
                    sender.sendMessage(message);
                }
            }
            else {
                List<String> messages = api.getMessages(base + CommandNodes.OFF, true);
                for (String message : messages) {
                    sender.sendMessage(message);
                }
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
        return ((SkillAPI)plugin).getMessage(CommandNodes.ARGUMENTS + CommandNodes.TOGGLE_BAR, true);
    }

    /**
     * @return the description of this command
     */
    @Override
    public String getDescription(Plugin plugin) {
        return ((SkillAPI)plugin).getMessage(CommandNodes.DESCRIPTION + CommandNodes.TOGGLE_BAR, true);
    }

    /**
     * @return required sender type for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}
