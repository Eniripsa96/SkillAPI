/**
 * SkillAPI
 * com.sucy.skill.task.ManaTask
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
package com.sucy.skill.task;

import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.log.LogType;
import com.sucy.skill.log.Logger;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * <p>Restores mana to all players over time.</p>
 * <p>This task is run by the API and you should not
 * use this task yourself.</p>
 */
public class ManaTask extends BukkitRunnable
{

    final SkillAPI plugin;

    /**
     * <p>Starts a new task for regenerating mana over time. The task is
     * started automatically so don't initialize this class unless wanting to
     * start a new task.</p>
     *
     * @param plugin SkillAPI reference
     */
    public ManaTask(SkillAPI plugin)
    {
        this.plugin = plugin;
        runTaskTimer(plugin, SkillAPI.getSettings().getGainFreq(), SkillAPI.getSettings().getGainFreq());
    }

    /**
     * <p>Checks all players for mana regeneration each interval</p>
     */
    public void run()
    {
        Player[] players = VersionManager.getOnlinePlayers();
        Logger.log(LogType.MANA, 1, "Applying mana regen for " + players.length + " players");
        for (Player player : players)
        {
            PlayerData data = SkillAPI.getPlayerData(player);
            data.regenMana();
        }
    }
}
