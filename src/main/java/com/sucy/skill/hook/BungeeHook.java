/**
 * SkillAPI
 * com.sucy.skill.hook.BungeeHook
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
package com.sucy.skill.hook;

import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.listener.MainListener;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Handles getting the server kick event over to the
 * quit event functions to save player data
 */
public class BungeeHook implements Listener
{
    /**
     * Initializes the hook into Bungee
     *
     * @param api api reference
     */
    public static void init(SkillAPI api)
    {
        api.getServer().getPluginManager().registerEvents(new BungeeHook(), api);
    }

    @EventHandler
    public void onKick(ServerKickEvent event)
    {
        MainListener.unload(VersionManager.getPlayer(event.getPlayer().getName()));
    }

    @EventHandler
    public void serverConnect(ServerConnectEvent event) {
        MainListener.unload(VersionManager.getPlayer(event.getPlayer().getName()));
    }
}
