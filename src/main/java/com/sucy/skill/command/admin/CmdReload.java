package com.sucy.skill.command.admin;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.language.CommandNodes;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Command to reload the plugin data
 */
public class CmdReload implements IFunction {

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

        plugin.getServer().getPluginManager().disablePlugin(plugin);
        plugin.getServer().getPluginManager().enablePlugin(plugin);
        sender.sendMessage(((SkillAPI) plugin).getMessage(CommandNodes.COMPLETE + CommandNodes.RELOAD, true));
    }
}
