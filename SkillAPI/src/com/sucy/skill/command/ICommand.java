package com.sucy.skill.command;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * <p>Interface for handlers of commands</p>
 */
public interface ICommand {

    /**
     * Executes a command
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args);

    /**
     * @return permission needed for this command
     */
    public String getPermissionNode();

    /**
     * @return args string (e.g. <playerName> <message>)
     */
    public String getArgsString(Plugin plugin);

    /**
     * @return command description (be very brief)
     */
    public String getDescription(Plugin plugin);

    /**
     * @return type of sender required by the command
     */
    public SenderType getSenderType();
}
