/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdScheme
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

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.gui.MapScheme;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

/**
 * A command that allows a player to view their skill tree
 */
public class CmdScheme implements IFunction
{
    private static final String CANNOT_USE  = "cannot-use";
    private static final String SCHEME_LIST = "scheme-list";
    private static final String DISABLED    = "world-disabled";
    private static final String NOT_SCHEME  = "not-scheme";
    private static final String SCHEME_SET  = "scheme-set";

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
        // Disabled world
        if (!SkillAPI.getSettings().isMapTreeEnabled() || (sender instanceof Player && !SkillAPI.getSettings().isWorldEnabled(((Player) sender).getWorld())))
        {
            cmd.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
        }

        // Only plays have skills to view
        else if (sender instanceof Player)
        {
            Player p = (Player) sender;

            // Scheme list
            if (args.length == 0)
            {
                String list = "";
                ArrayList<MapScheme> schemes = MapScheme.list((SkillAPI) plugin);
                for (MapScheme scheme : schemes)
                {
                    if (list.length() > 0) list += ", ";
                    list += scheme.getKey();
                }
                cmd.sendMessage(sender, SCHEME_LIST, ChatColor.DARK_GREEN + "Available Schemes: " + ChatColor.GOLD + "{list}", RPGFilter.LIST.setReplacement(list));
            }

            // Choosing a scheme
            else
            {
                String name = args[0];
                for (int i = 1; i < args.length; i++)
                {
                    name += " " + args[i];
                }
                Object scheme = MapScheme.get((SkillAPI) plugin, name);
                if (scheme == null)
                {
                    cmd.sendMessage(sender, NOT_SCHEME, ChatColor.RED + "That is not a valid scheme");
                }
                else
                {
                    SkillAPI.getPlayerData(p).setScheme(name);
                    cmd.sendMessage(sender, SCHEME_SET, ChatColor.DARK_GREEN + "Your scheme has been set to " + ChatColor.GOLD + "{scheme}", RPGFilter.SCHEME.setReplacement(name));
                }
            }
        }

        // Console doesn't have profession options
        else
        {
            cmd.sendMessage(sender, CANNOT_USE, ChatColor.RED + "This cannot be used by the console");
        }
    }
}
