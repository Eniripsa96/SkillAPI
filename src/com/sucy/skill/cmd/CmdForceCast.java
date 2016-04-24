/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdForceCast
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
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

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.regex.Pattern;

/**
 * A command that makes a player cast a skill regardless
 * of them owning it or not and also ignores cooldown/mana costs.
 */
public class CmdForceCast implements IFunction
{
    private static final Pattern INTEGER = Pattern.compile("-?[0-9]+");

    private static final String NOT_PLAYER    = "not-player";
    private static final String WRONG_SKILL   = "wrong-skill";
    private static final String INVALID_SKILL = "invalid-skill";

    /**
     * Runs the command
     *
     * @param cmd    command that was executed
     * @param plugin plugin reference
     * @param sender sender of the command
     * @param args   argument list
     */
    @Override
    public void execute(ConfigurableCommand cmd, Plugin plugin, CommandSender sender, String[] args)
    {
        // Only players have profession options
        if (args.length < 2)
        {
            CommandManager.displayUsage(cmd, sender);
        }
        else
        {
            Player player = VersionManager.getPlayer(args[0]);
            if (player == null)
            {
                cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "That is not a valid player name");
                return;
            }

            String name = args[1];
            int level = 1;
            for (int i = 2; i < args.length; i++)
            {
                if (i == args.length - 1 && SkillAPI.getSkill(name) != null && INTEGER.matcher(args[i]).matches())
                {
                    level = Integer.parseInt(args[i]);
                }
                else name += ' ' + args[i];
            }

            Skill skill = SkillAPI.getSkill(name);

            // Invalid class
            if (skill == null)
            {
                cmd.sendMessage(sender, INVALID_SKILL, ChatColor.RED + "That is not a valid skill");
            }

            // Castable skill
            else if (skill instanceof SkillShot)
            {
                ((SkillShot) skill).cast(player, level);
            }

            // Not castable
            else
            {
                cmd.sendMessage(sender, WRONG_SKILL, ChatColor.RED + "Skills must be skill shot skills or dynamic skills to be cast this way.");
            }
        }
    }
}
