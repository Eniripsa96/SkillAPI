package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * A command that allows a player to profess through classes
 */
public class CmdReload implements IFunction
{
    private static final String DONE = "done";

    /**
     * Runs the command
     *
     * @param cmd    command that was executed
     * @param plugin plugin reference
     * @param sender sender of the command
     * @param args   argument list
     */
    @Override
    public void execute(ConfigurableCommand cmd, Plugin plugin, CommandSender sender, String[] args)
    {
        plugin.getServer().getPluginManager().disablePlugin(plugin);
        plugin.getServer().getPluginManager().enablePlugin(plugin);
        cmd.sendMessage(sender, DONE, "&2The plugin has been reloaded");
    }
}
