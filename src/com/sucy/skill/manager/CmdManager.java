package com.sucy.skill.manager;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.SenderType;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.cmd.*;
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
                new ConfigurableCommand(api, "exp", SenderType.ANYONE, new CmdExp(), "Gives players exp", "[player] <amount>", Permissions.LVL),
                new ConfigurableCommand(api, "info", SenderType.ANYONE, new CmdInfo(), "Shows class info", "[player]", Permissions.BASIC),
                new ConfigurableCommand(api, "level", SenderType.ANYONE, new CmdLevel(), "Gives players levels", "[player] <amount>", Permissions.LVL),
                new ConfigurableCommand(api, "options", SenderType.PLAYER_ONLY, new CmdOptions(), "Views profess options", "", Permissions.BASIC),
                new ConfigurableCommand(api, "profess", SenderType.PLAYER_ONLY, new CmdProfess(), "Professes classes", "<class>", Permissions.BASIC),
                new ConfigurableCommand(api, "skill", SenderType.PLAYER_ONLY, new CmdSkill(), "Shows player skills", Permissions.BASIC)
        );
        CommandManager.registerCommand(root);
    }

    public void clear()
    {
        CommandManager.unregisterCommands(api);
    }
}
