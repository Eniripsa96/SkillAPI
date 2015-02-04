package com.sucy.skill.task;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Handles updating cooldown values on skill bars for players
 */
public class CooldownTask extends BukkitRunnable
{
    /**
     * Initializes a new cooldown task. This shouldn't be used by
     * other plugins as it is already set up by the API and additional
     * copies would create extra processing for no real gain.
     *
     * @param api SkillAPI reference
     */
    public CooldownTask(SkillAPI api)
    {
        runTaskTimer(api, 20, 20);
    }

    /**
     * Updates the cooldowns on skill bars each second
     */
    @Override
    public void run()
    {
        for (Player player : Bukkit.getServer().getOnlinePlayers())
        {
            PlayerData data = SkillAPI.getPlayerData(player);
            if (data.hasClass())
            {
                data.getSkillBar().updateCooldowns();
            }
        }
    }
}
