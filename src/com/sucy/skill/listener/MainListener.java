package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.manager.ClassBoardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class MainListener implements Listener
{
    private SkillAPI plugin;

    public MainListener(SkillAPI plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLogin(AsyncPlayerPreLoginEvent event)
    {
        SkillAPI.loadPlayerData(event.getName());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        PlayerData data = SkillAPI.getPlayerData(event.getPlayer());

        // Apply player data as long as they have a class
        if (data.hasClass())
        {
            data.updateHealthAndMana(event.getPlayer());
            data.updateLevelBar();
            data.startPassives(event.getPlayer());
            RPGClass classData = data.getMainClass().getData();
            ClassBoardManager.update(data, classData.getPrefix(), classData.getPrefixColor());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
        PlayerData data = SkillAPI.getPlayerData(event.getEntity());
        if (data.hasClass())
        {
            data.stopPassives(event.getEntity());
            if (SkillAPI.getSettings().getDeathPenalty() > 0)
            {
                data.loseExp(SkillAPI.getSettings().getDeathPenalty());
            }
        }
    }

    @EventHandler
    public void onSpawn(PlayerRespawnEvent event)
    {
        PlayerData data = SkillAPI.getPlayerData(event.getPlayer());
        if (data.hasClass())
        {
            data.startPassives(event.getPlayer());
        }
    }
}
