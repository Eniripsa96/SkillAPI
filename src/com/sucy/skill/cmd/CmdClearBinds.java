package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command to clear all bound skills
 */
public class CmdClearBinds implements IFunction
{
    private static final String NOT_PLAYER = "not-player";
    private static final String UNBOUND    = "skills-unbound";

    /**
     * Executes the command
     *
     * @param command owning command
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    arguments
     */
    @Override
    public void execute(ConfigurableCommand command, Plugin plugin, CommandSender sender, String[] args)
    {
        if (!(sender instanceof Player))
        {
            command.sendMessage(sender, NOT_PLAYER, "&4Only players can use this command");
        }

        else
        {
            PlayerData player = SkillAPI.getPlayerData((Player) sender);

            player.clearAllBinds();
            command.sendMessage(sender, UNBOUND, "&2All skill bindings have been cleared");
        }
    }
}