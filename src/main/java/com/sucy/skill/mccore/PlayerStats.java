package com.sucy.skill.mccore;

import com.rit.sucy.scoreboard.StatHolder;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.CustomClass;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.language.StatNodes;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;

/**
 * Stat provider for the MCCore stat scoreboard
 */
public class PlayerStats implements StatHolder {

    private PlayerSkills player;

    /**
     * Constructor
     *
     * @param player player to show stats for
     */
    public PlayerStats(PlayerSkills player) {
        this.player = player;
    }

    /**
     * @return map of stats for the scoreboard
     */
    @Override
    public ArrayList<OfflinePlayer> getStats() {
        SkillAPI plugin = player.getAPI();
        ArrayList<OfflinePlayer> stats = new ArrayList<OfflinePlayer>();
        stats.add(plugin.getScoreboardStat(StatNodes.HEALTH_KEY));
        if (plugin.isManaEnabled()) {
            if (player.hasClass()) {
                CustomClass c = player.getAPI().getClass(player.getClassName());
                stats.add(plugin.getScoreboardStat(StatNodes.MANA_KEY));
            }
        }
        stats.add(plugin.getScoreboardStat(StatNodes.POINTS_KEY));
        stats.add(plugin.getScoreboardStat(StatNodes.LEVEL_KEY));
        stats.add(plugin.getScoreboardStat(StatNodes.EXP_KEY));

        return stats;
    }

    @Override
    public ArrayList<Integer> getValues() {
        SkillAPI plugin = player.getAPI();
        double health = player.getPlayer().getHealth();
        ArrayList<Integer> values = new ArrayList<Integer>();
        values.add((int)health);
        if (plugin.isManaEnabled()) {
            if (player.hasClass()) {
                CustomClass c = player.getAPI().getClass(player.getClassName());
                values.add(player.getMana());
            }
        }
        values.add(player.getPoints());
        values.add(player.getLevel());
        values.add(player.getExp());

        return values;
    }
}
