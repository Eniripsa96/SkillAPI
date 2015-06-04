package com.sucy.skill.manager;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.SenderType;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.cmd.*;
import com.sucy.skill.data.Permissions;

/**
 * Sets up commands for the plugin
 */
public class CmdManager
{
    private SkillAPI api;

    /**
     * Initializes a new command manager. This is handled by the API and
     * shouldn't be used by other plugins.
     *
     * @param api SkillAPI reference
     */
    public CmdManager(SkillAPI api)
    {
        this.api = api;
        this.initialize();
    }

    /**
     * Initializes commands with MCCore's CommandManager
     */
    public void initialize()
    {
        ConfigurableCommand root = new ConfigurableCommand(api, "class", SenderType.ANYONE);
        root.addSubCommands(
                new ConfigurableCommand(api, "acc", SenderType.PLAYER_ONLY, new CmdAccount(), "Changes account", "<accountId>", Permissions.BASIC),
                new ConfigurableCommand(api, "bind", SenderType.PLAYER_ONLY, new CmdBind(), "Binds a skill", "<skill>", Permissions.BASIC),
                new ConfigurableCommand(api, "cast", SenderType.PLAYER_ONLY, new CmdCast(), "Casts a skill", "<skill>", Permissions.BASIC),
                new ConfigurableCommand(api, "clearbind", SenderType.PLAYER_ONLY, new CmdClearBinds(), "Clears skill binds", "", Permissions.BASIC),
                new ConfigurableCommand(api, "exp", SenderType.ANYONE, new CmdExp(), "Gives players exp", "[player] <amount>", Permissions.LVL),
                new ConfigurableCommand(api, "info", SenderType.ANYONE, new CmdInfo(), "Shows class info", "[player]", Permissions.BASIC),
                new ConfigurableCommand(api, "level", SenderType.ANYONE, new CmdLevel(), "Gives players levels", "[player] <amount>", Permissions.LVL),
                new ConfigurableCommand(api, "list", SenderType.ANYONE, new CmdList(), "Displays accounts", "[player]", Permissions.BASIC),
                new ConfigurableCommand(api, "lore", SenderType.PLAYER_ONLY, new CmdLore(), "Adds lore to item", "<lore>", Permissions.LORE),
                new ConfigurableCommand(api, "mana", SenderType.ANYONE, new CmdMana(), "Gives player mana", "[player] <amount>", Permissions.MANA),
                new ConfigurableCommand(api, "options", SenderType.PLAYER_ONLY, new CmdOptions(), "Views profess options", "", Permissions.BASIC),
                new ConfigurableCommand(api, "points", SenderType.ANYONE, new CmdPoints(), "Gives player points", "[player] <amount>", Permissions.POINTS),
                new ConfigurableCommand(api, "profess", SenderType.PLAYER_ONLY, new CmdProfess(), "Professes classes", "<class>", Permissions.BASIC),
                new ConfigurableCommand(api, "reload", SenderType.ANYONE, new CmdReload(), "Reloads the plugin", "", Permissions.RELOAD),
                new ConfigurableCommand(api, "reset", SenderType.PLAYER_ONLY, new CmdReset(), "Resets account data", "", Permissions.BASIC),
                new ConfigurableCommand(api, "skill", SenderType.PLAYER_ONLY, new CmdSkill(), "Shows player skills", "", Permissions.BASIC),
                new ConfigurableCommand(api, "unbind", SenderType.PLAYER_ONLY, new CmdUnbind(), "Unbinds held item", "", Permissions.BASIC)
        );
        root.addSubCommands(
                new ConfigurableCommand(api, "forceaccount", SenderType.CONSOLE_ONLY, new CmdForceAccount(), "Changes player's account", "<player> <accountId>", Permissions.FORCE),
                new ConfigurableCommand(api, "forceattr", SenderType.CONSOLE_ONLY, new CmdForceAttr(), "Refunds attributes", "<player>", Permissions.FORCE),
                new ConfigurableCommand(api, "forcecast", SenderType.CONSOLE_ONLY, new CmdForceCast(), "Player casts the skill", "<player> <skill> [level]", Permissions.FORCE),
                new ConfigurableCommand(api, "forceprofess", SenderType.CONSOLE_ONLY, new CmdForceProfess(), "Professes a player", "<player> <class>", Permissions.FORCE)
        );
        if (SkillAPI.getSettings().isSkillBarEnabled())
        {
            root.addSubCommand(new ConfigurableCommand(api, "bar", SenderType.PLAYER_ONLY, new CmdBar(), "Toggles skill bar", "", Permissions.BASIC));
        }
        if (SkillAPI.getSettings().isCombosEnabled())
        {
            root.addSubCommand(new ConfigurableCommand(api, "combo", SenderType.PLAYER_ONLY, new CmdCombo(), "Sets skill combo", "<skill> <combo>", Permissions.BASIC));
        }
        if (SkillAPI.getSettings().isMapTreeEnabled())
        {
            root.addSubCommand(new ConfigurableCommand(api, "scheme", SenderType.PLAYER_ONLY, new CmdScheme(), "Views/sets map schemes", "[scheme]", Permissions.BASIC));
        }
        if (SkillAPI.getSettings().isAttributesEnabled())
        {
            root.addSubCommand(new ConfigurableCommand(api, "ap", SenderType.ANYONE, new CmdAP(), "Gives attrib points", "[player] <amount>", Permissions.ATTRIB));
            root.addSubCommand(new ConfigurableCommand(api, "attr", SenderType.PLAYER_ONLY, new CmdAttribute(), "Opens attribute menu", "", Permissions.BASIC));
        }
        CommandManager.registerCommand(root);
    }

    /**
     * Unregisters all commands for SkillAPI from the server
     */
    public void clear()
    {
        CommandManager.unregisterCommands(api);
    }
}
