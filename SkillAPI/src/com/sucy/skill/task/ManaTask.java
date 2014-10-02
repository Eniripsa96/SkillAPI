package com.sucy.skill.task;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * <p>Restores mana to all players over time</p>
 * <p>This task is run by the API and you should not
 * use this task yourself</p>
 */
public class ManaTask extends BukkitRunnable
{

    final SkillAPI plugin;

    public ManaTask(SkillAPI plugin)
    {
        this.plugin = plugin;
        runTaskTimer(plugin, plugin.getSettings().getGainFreq(), plugin.getSettings().getGainFreq());
    }

    /**
     * <p>Checks all players for mana regeneration</p>
     */
    public void run()
    {
        for (Player player : plugin.getServer().getOnlinePlayers())
        {
            PlayerData data = plugin.getPlayerData(player);
            data.regenMana();
        }
    }
}
