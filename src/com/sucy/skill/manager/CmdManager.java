package com.sucy.skill.manager;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.SenderType;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.cmd.CmdInfo;
import com.sucy.skill.cmd.CmdOptions;
import com.sucy.skill.cmd.CmdProfess;
import com.sucy.skill.data.Permissions;

public class CmdManager
{
    private SkillAPI api;

    public CmdManager(SkillAPI api)
    {
        this.api = api;
        this.initialize();
    }

    public void initialize()
    {
        ConfigurableCommand root = new ConfigurableCommand(api, "class", SenderType.ANYONE);
        root.addSubCommands(
                new ConfigurableCommand(api, "info", SenderType.ANYONE, new CmdInfo(), "Shows class info", "[player]", Permissions.BASIC),
                new ConfigurableCommand(api, "options", SenderType.PLAYER_ONLY, new CmdOptions(), "Views profess options", "", Permissions.BASIC),
                new ConfigurableCommand(api, "profess", SenderType.PLAYER_ONLY, new CmdProfess(), "Professes classes", "<class>", Permissions.BASIC)
        );
        CommandManager.registerCommand(root);
    }

    public void clear()
    {
        CommandManager.unregisterCommands(api);
    }
}
