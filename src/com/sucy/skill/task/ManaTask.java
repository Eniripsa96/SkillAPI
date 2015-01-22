package com.sucy.skill.task;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
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
        for (Player player : plugin.getServer().getOnlinePlayers())
        {
            PlayerData data = SkillAPI.getPlayerData(player);
            data.regenMana();
        }
    }
}
