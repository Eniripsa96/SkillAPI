package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
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
    private static final String DISABLED   = "world-disabled";

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

        // Disabled world
        else if (!SkillAPI.getSettings().isWorldEnabled(((Player) sender).getWorld()))
        {
            command.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
        }

        else
        {
            PlayerData player = SkillAPI.getPlayerData((Player) sender);

            player.clearAllBinds();
            command.sendMessage(sender, UNBOUND, "&2All skill bindings have been cleared");
        }
    }
}