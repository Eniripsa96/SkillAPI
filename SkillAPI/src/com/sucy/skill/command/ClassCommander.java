package com.sucy.skill.command;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.command.admin.*;
import com.sucy.skill.command.basic.*;
import com.sucy.skill.language.CommandNodes;

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
        super(plugin, "SkillAPI - By Steven Sucy", "class");
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

        // Admin commands
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.EXP_CONSOLE, false), new CmdExpConsole());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.EXP_PLAYER, false), new CmdExpPlayer());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.LEVEL_CONSOLE, false), new CmdLevelConsole());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.LEVEL_PLAYER, false), new CmdLevelPlayer());
        registerCommand(api.getMessage(CommandNodes.NAME + CommandNodes.RELOAD, false), new CmdReload());
    }
}
