package com.sucy.skill.listener;

import com.sucy.skill.packet.PacketInjector;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * SkillAPI © 2018
 * com.sucy.skill.listener.PacketListener
 */
public class PacketListener extends SkillAPIListener {
    private PacketInjector injector;

    public PacketListener(final PacketInjector injector) {
        this.injector = injector;
    }

    @Override
    public void init() {
        Bukkit.getServer().getOnlinePlayers().forEach(injector::addPlayer);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        injector.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        injector.removePlayer(event.getPlayer());
    }
}
