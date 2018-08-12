/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdBind
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
import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Command to bind a skill to an item
 */
public class CmdBind implements IFunction
{
    private static final String NOT_PLAYER   = "not-player";
    private static final String NOT_SKILL    = "not-skill";
    private static final String NOT_UNLOCKED = "not-unlocked";
    private static final String NO_ITEM      = "no-item";
    private static final String SKILL_BOUND  = "skill-bound";
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

        else if (args.length >= 1)
        {
            ItemStack item = ((Player) sender).getItemInHand();
            if (item == null || item.getType() == Material.AIR)
            {
                command.sendMessage(sender, NO_ITEM, "&4You are not holding an item");
                return;
            }

            PlayerData player = SkillAPI.getPlayerData((Player) sender);

            String name = args[0];
            for (int i = 1; i < args.length; i++)
            {
                name += ' ' + args[i];
            }

            PlayerSkill skill = player.getSkill(name);

            if (skill == null)
            {
                command.sendMessage(sender, NOT_SKILL, "&4You do not have that skill");
            }
            else if (skill.getLevel() == 0)
            {
                command.sendMessage(sender, NOT_UNLOCKED, "&4You have not unlocked that skill");
            }
            else
            {
                player.bind(item.getType(), skill);
                command.sendMessage(sender,
                        SKILL_BOUND,
                        "&6{skill} &2has been bound to &6{item}",
                        RPGFilter.SKILL.setReplacement(skill.getData().getName()),
                        RPGFilter.ITEM.setReplacement(TextFormatter.format(item.getType().name())));
            }
        }
        else
        {
            CommandManager.displayUsage(command, sender);
        }
    }
}