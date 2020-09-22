/**
 * SkillAPI
 * com.sucy.skill.task.PreviewTask
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
package com.sucy.skill.task;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.particle.Particle;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.cast.IndicatorSettings;
import com.sucy.skill.thread.RepeatThreadTask;
import org.bukkit.entity.Player;

import java.util.List;

public class PreviewTask extends RepeatThreadTask
{
    private Player     player;
    private PlayerData data;
    private int step = 0;

    public PreviewTask(Player player)
    {
        super(0, IndicatorSettings.interval);

        this.player = player;
        this.data = SkillAPI.getPlayerData(player);
    }

    @Override
    public void run()
    {
        // Expire when not in the hover view anymore
        if (!data.getCastBars().isHovering())
        {
            expired = true;
            return;
        }

        // Update and play the effect
        try
        {
            List<Object> packets = data.getCastBars().getHoverPackets(player, step++);
            if (packets != null)
                Particle.send(player, packets);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            expired = true;
        }
    }
}
