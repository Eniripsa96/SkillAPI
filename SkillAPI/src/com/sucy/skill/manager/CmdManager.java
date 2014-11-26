package com.sucy.skill.manager;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.SenderType;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.cmd.CmdOptions;
import com.sucy.skill.data.Permissions;

public class CmdManager
{
    private SkillAPI api;

    public void CmdManager(SkillAPI api)
    {
        this.api = api;
        this.initialize();
    }

    public void initialize()
    {
        ConfigurableCommand root = new ConfigurableCommand(api, "class", SenderType.ANYONE);
        root.addSubCommands(
                new ConfigurableCommand(api, "options", SenderType.PLAYER_ONLY, new CmdOptions(), "Views profess options", "", Permissions.BASIC)
        );
        CommandManager.registerCommand(root);
    }
}
