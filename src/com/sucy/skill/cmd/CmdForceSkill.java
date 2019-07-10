/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdForceSkill
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Command to forcefully modify a skill's level
 */
public class CmdForceSkill implements IFunction
{
    private static final String NOT_PLAYER   = "not-player";
    private static final String NOT_SKILL    = "not-skill";
    private static final String NOT_FUNCTION = "not-function";
    private static final String UPGRADED     = "skill-upped";
    private static final String DOWNGRADED   = "skill-downed";
    private static final String RESET        = "skill-reset";

    /**
     * Executes the command
     *
     * @param command owning command
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    arguments
     */
    @Override
    public void execute(ConfigurableCommand command, Plugin plugin, CommandSender sender, String[] args)
    {
        // Needs two arguments
        if (args.length < 3)
        {
            command.displayHelp(sender);
        }

        // Switch accounts if valid number
        else
        {
            OfflinePlayer player = VersionManager.getOfflinePlayer(args[0], false);

            if (player == null)
            {
                command.sendMessage(sender, NOT_PLAYER, "&4That is not a valid player name");
                return;
            }

            PlayerData playerData = SkillAPI.getPlayerData(player);
            StringBuilder skillName = new StringBuilder(args[2]);
            for (int i = 3; i < args.length; i++) skillName.append(args[i]);
            PlayerSkill skill = playerData.getSkill(skillName.toString());

            if (skill == null)
            {
                command.sendMessage(sender, NOT_SKILL, "&4The player does not have access to that skill");
                return;
            }

            if (args[1].equals("up"))
            {
                playerData.forceUpSkill(skill);
                command.sendMessage(sender, UPGRADED, "&6" + skill.getData().getName() + "&2 was upgraded for &6" + player.getName());
            }
            else if (args[1].equals("down"))
            {
                playerData.forceDownSkill(skill);
                command.sendMessage(sender, DOWNGRADED, "&6" + skill.getData().getName() + "&2 was downgraded for &6" + player.getName());
            }
            else if (args[1].equals("reset"))
            {
                playerData.refundSkill(skill);
                command.sendMessage(sender, RESET, "&6" + skill.getData().getName() + "&2 was reset for &6" + player.getName());
            }
            else
                command.sendMessage(sender, NOT_FUNCTION, "&4That is not a valid function. Use up, down, or reset.");
        }
    }
}
