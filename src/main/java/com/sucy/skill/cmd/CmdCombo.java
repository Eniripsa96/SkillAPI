/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdCombo
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
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.data.Click;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command to bind a skill to an item
 */
public class CmdCombo implements IFunction
{
    private static final String NOT_PLAYER   = "not-player";
    private static final String NOT_SKILL    = "not-skill";
    private static final String NOT_CASTABLE = "not-unlocked";
    private static final String NOT_CLICK    = "not-click";
    private static final String NOT_COMBO    = "not-combo";
    private static final String COMBO_SET    = "skill-bound";
    private static final String DISABLED     = "world-disabled";

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
        if (!(sender instanceof Player))
        {
            command.sendMessage(sender, NOT_PLAYER, "&4Only players can use this command");
        }

        // Disabled world
        else if (!SkillAPI.getSettings().isWorldEnabled(((Player) sender).getWorld()))
        {
            command.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
        }

        else if (args.length >= SkillAPI.getComboManager().getComboSize() + 1)
        {
            PlayerData player = SkillAPI.getPlayerData((Player) sender);

            String name = args[0];
            int comboSize = SkillAPI.getComboManager().getComboSize();
            for (int i = 1; i < args.length - comboSize; i++)
            {
                name += ' ' + args[i];
            }
            PlayerSkill skill = player.getSkill(name);

            if (skill == null)
            {
                command.sendMessage(sender, NOT_SKILL, "&4You do not have that skill");
            }
            else if (!skill.getData().canCast())
            {
                command.sendMessage(sender, NOT_CASTABLE, "&4That skill cannot be cast");
            }
            else
            {

                Click[] clicks = new Click[comboSize];
                for (int i = args.length - comboSize; i < args.length; i++)
                {
                    Click click = Click.getByName(args[i]);
                    if (click == null)
                    {
                        command.sendMessage(sender, NOT_CLICK, "&6{name} &4is not a valid click type. Use Left, Right, or Shift instead", RPGFilter.NAME);
                        return;
                    }
                    clicks[i - args.length + comboSize] = click;
                }
                int id = SkillAPI.getComboManager().convertCombo(clicks);
                if (player.getComboData().setSkill(skill.getData(), id))
                {
                    if (SkillAPI.getSettings().isSkillBarEnabled() && player.getSkillBar().isSetup())
                    {
                        player.getSkillBar().update(player.getPlayer());
                    }
                    command.sendMessage(sender, COMBO_SET, "&2The combo for &6{skill} &2has been updated", RPGFilter.SKILL.setReplacement(skill.getData().getName()));
                }
                else
                {
                    command.sendMessage(sender, NOT_COMBO, "&4That combo cannot be used");
                }
            }
        }
        else
        {
            CommandManager.displayUsage(command, sender);
        }
    }
}