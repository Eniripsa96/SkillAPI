/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdSwitch
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Steven Sucy
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
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class CmdSwitch implements IFunction {

    private static final String NOT_PLAYER = "not-player";
    private static final String NOT_CLASS  = "not-class";
    private static final String CHANGED    = "account-changed";
    private static final String DISABLED   = "world-disabled";

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
        // Must be a player
        if (!(sender instanceof Player))
        {
            command.sendMessage(sender, NOT_PLAYER, "&4Only players can use this command");
        }

        // Disabled world
        else if (!SkillAPI.getSettings().isWorldEnabled(((Player) sender).getWorld()))
        {
            command.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
        }

        // Needs an argument
        else if (args.length == 0)
        {
            command.displayHelp(sender);
        }

        // Switch accounts if valid number
        else
        {
            PlayerAccounts player = SkillAPI.getPlayerAccountData((Player) sender);

            RPGClass rpgClass = getRoot(SkillAPI.getClass(args[0]));
            if (rpgClass != null)
            {
                boolean done = false;
                for (Map.Entry<Integer, PlayerData> entry : player.getAllData().entrySet()) {
                    PlayerClass accountClass = entry.getValue().getMainClass();
                    if (accountClass != null && getRoot(accountClass.getData()) == rpgClass) {
                        player.setAccount(entry.getKey());
                        done = true;
                        break;
                    }
                }
                if (!done) {
                    int i = 1;
                    if (player.getActiveData().getMainClass() != null)
                        while (player.getData(i) != null) i++;
                    player.setAccount(i);
                    player.getActiveData().profess(rpgClass);
                }
                command.sendMessage(sender, CHANGED, ChatColor.DARK_GREEN + "You have changed classes");
                return;
            }

            command.sendMessage(sender, NOT_CLASS, ChatColor.RED + "That is not a valid class");
        }
    }

    private RPGClass getRoot(RPGClass rpgClass) {
        if (rpgClass == null)
            return null;

        while (rpgClass.getParent() != null)
            rpgClass = rpgClass.getParent();
        return rpgClass;
    }
}
