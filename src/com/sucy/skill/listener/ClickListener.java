/**
 * SkillAPI
 * com.sucy.skill.listener.ClickListener
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
package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.KeyPressEvent;
import com.sucy.skill.api.player.PlayerCombos;
import com.sucy.skill.data.Click;
import com.sucy.skill.packet.PacketInjector;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * Handles transferring click actions by the player to
 * combos that cast skills.
 */
public class ClickListener extends SkillAPIListener {
    private HashMap<UUID, Long> lastClick = new HashMap<>();

    private PacketInjector injector;

    public ClickListener(final PacketInjector injector) {
        this.injector = injector;
    }

    @Override
    public void init() {
        Bukkit.getServer().getOnlinePlayers().forEach(injector::addPlayer);
    }

    @Override
    public void cleanup() {
        lastClick.clear();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        injector.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        lastClick.remove(event.getPlayer().getUniqueId());
        injector.removePlayer(event.getPlayer());
    }

    /**
     * Registers clicks as they happen
     *
     * @param event event details
     */
    @EventHandler
    public void onClick(final KeyPressEvent event) {
        final Long time = lastClick.get(event.getPlayer().getUniqueId());
        if (time != null && time > System.currentTimeMillis()) {
            return;
        }

        // Get the history
        PlayerCombos combo = SkillAPI.getPlayerData(event.getPlayer()).getComboData();

        switch (event.getKey()) {
            case LEFT:
                combo.applyClick(Click.LEFT);
                break;
            case RIGHT:
                combo.applyClick(Click.RIGHT);
                break;
            default:
                return;
        }

        lastClick.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + 40);
    }

    /**
     * Registers shift clicks as they happen
     *
     * @param event event details
     */
    @EventHandler
    public void onShiftClick(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            SkillAPI.getPlayerData(event.getPlayer()).getComboData().applyClick(Click.SHIFT);
        }
    }
}
