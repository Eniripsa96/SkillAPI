package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class MainListener implements Listener
{
    private SkillAPI plugin;

    public MainListener(SkillAPI plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(AsyncPlayerPreLoginEvent event)
    {
        SkillAPI.loadPlayerData(event.getName());
    }
}
