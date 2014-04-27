package com.sucy.skill.task;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.CustomClass;
import com.sucy.skill.api.PlayerSkills;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * <p>Restores mana to all players over time</p>
 * <p>This task is run by the API and you should not
 * use this task yourself</p>
 */
public class ManaTask extends BukkitRunnable {

    final SkillAPI plugin;
    final int amount;

    /**
     * Constructor
     *
     * @param plugin      plugin reference
     * @param freqSeconds interval in seconds between restoring mana
     * @param amount      amount of mana to restore
     */
    public ManaTask (SkillAPI plugin, int freqSeconds, int amount) {
        this.plugin = plugin;
        this.amount = amount;
        runTaskTimer(plugin, freqSeconds * 20, freqSeconds * 20);
    }

    /**
     * Gives mana to all players
     */
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            PlayerSkills data = plugin.getPlayer(player);
            if (data != null) {
                if (data.hasClass()) {
                    CustomClass c = plugin.getClass(data.getClassName());
                    if (c.gainsMana()) {
                        plugin.getPlayer(player).gainMana(amount);
                    }
                }
            }
        }
    }
}
