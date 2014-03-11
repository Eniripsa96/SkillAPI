package com.sucy.skill.command;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.command.admin.*;
import com.sucy.skill.command.basic.*;
import com.sucy.skill.command.basic.CmdProfess;
import com.sucy.skill.command.basic.CmdReset;
import com.sucy.skill.command.console.*;
import com.sucy.skill.language.CommandNodes;
import org.bukkit.command.Command;

/**
 * Handler for class commands
 */
public class ClassCommander extends CommandHandler {

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public ClassCommander(SkillAPI plugin) {
        super(plugin, "SkillAPI", plugin.getMessage(CommandNodes.ROOT, false));
    }

    /**
     * Registers the commands
     */
    @Override
    protected void registerCommands() {
        SkillAPI api = (SkillAPI)plugin;

        // Basic commands
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.BIND, false), new CmdBind());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.CAST, false), new CmdCast());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.INFO_CONSOLE, false), new CmdInfoConsole());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.INFO_PLAYER, false), new CmdInfoPlayer());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.OPTIONS, false), new CmdOptions());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.PROFESS, false), new CmdProfess());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.RESET, false), new CmdReset());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.SKILLS, false), new CmdSkills());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.UNBIND, false), new CmdUnbind());
        if (api.isUsingSkillBars()) registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.TOGGLE_BAR, false), new CmdToggleBar());

        // Admin commands
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.EXP_CONSOLE, false), new CmdExpConsole());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.EXP_PLAYER, false), new CmdExpPlayer());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.LEVEL_CONSOLE, false), new CmdLevelConsole());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.LEVEL_PLAYER, false), new CmdLevelPlayer());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.POINTS_CONSOLE, false), new CmdPointsConsole());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.POINTS_PLAYER, false), new CmdPointsPlayer());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.RELOAD, false), new CmdReload());

        // Console Commands
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.ADMIN_PROFESS, false), new com.sucy.skill.command.console.CmdProfess());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.ADMIN_RESET, false), new com.sucy.skill.command.console.CmdReset());
    }
}
