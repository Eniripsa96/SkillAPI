package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.config.Filter;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * SkillAPI © 2018
 * com.sucy.skill.cmd.CmdChangeClass
 */
public class CmdChangeClass implements IFunction {
    private static final String INVALID_GROUP = "invalid-group";
    private static final String INVALID_PLAYER = "invalid-player";
    private static final String INVALID_TARGET = "invalid-class";
    private static final String SUCCESS = "success";
    private static final String NOTIFICATION = "notification";

    /**
     * Executes the command
     *
     * @param cmd owning command
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    arguments
     */
    @Override
    public void execute(ConfigurableCommand cmd, Plugin plugin, CommandSender sender, String[] args) {
        if (args.length >= 3) {
            final String playerName = args[0];
            final String groupName = args[1];
            String className = args[2];
            for (int i = 3; i < args.length; i++) className += ' ' + args[i];

            final Player player = VersionManager.getPlayer(playerName);
            if (player == null) {
                cmd.sendMessage(sender, INVALID_PLAYER, ChatColor.DARK_RED + "{player} is not online",
                        Filter.PLAYER.setReplacement(playerName));
                return;
            }

            final PlayerClass data = SkillAPI.getPlayerData(player).getClass(groupName);
            if (data == null) {
                cmd.sendMessage(sender, INVALID_GROUP, "{player} does not have a {group}",
                        Filter.PLAYER.setReplacement(player.getName()),
                        RPGFilter.CLASS.setReplacement(groupName));
                return;
            }

            final String original = data.getData().getName();
            final RPGClass target = SkillAPI.getClass(className);
            if (target == null) {
                cmd.sendMessage(sender, INVALID_TARGET, "{class} is not a valid class to change to",
                        RPGFilter.CLASS.setReplacement(className));
                return;
            }

            data.setClassData(target);
            cmd.sendMessage(sender, SUCCESS, "You have changed {player} from a {name} to a {group}",
                    Filter.PLAYER.setReplacement(player.getName()),
                    RPGFilter.CLASS.setReplacement(className),
                    RPGFilter.NAME.setReplacement(original));

            if (sender != player) {
                cmd.sendMessage(player, NOTIFICATION, "You have changed from a {name} to a {group}",
                        RPGFilter.CLASS,
                        RPGFilter.NAME);
            }
        } else {
            cmd.displayHelp(sender);
        }
    }
}
