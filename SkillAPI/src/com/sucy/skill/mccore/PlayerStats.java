package com.sucy.skill.mccore;

import com.rit.sucy.scoreboard.StatHolder;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.CustomClass;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.util.TextFormatter;
import com.sucy.skill.language.StatNodes;

import java.util.HashMap;
import java.util.Map;

/**
 * Stat provider for the MCCore stat scoreboard
 */
public class PlayerStats implements StatHolder {

    private PlayerSkills player;
    private HashMap<String, Integer> stats;

    /**
     * Constructor
     *
     * @param player player to show stats for
     */
    public PlayerStats(PlayerSkills player) {
        this.player = player;
        this.stats = new HashMap<String, Integer>();
        SkillAPI plugin = player.getAPI();
        double health = player.getPlayer().getHealth();
        stats.put(plugin.getMessage(StatNodes.HEALTH, true), (int)health);
        if (plugin.isManaEnabled()) {
            if (player.hasClass()) {
                CustomClass c = player.getAPI().getClass(player.getClassName());
                stats.put(TextFormatter.colorString(c.getManaName()), player.getMana());
            }
        }
        stats.put(plugin.getMessage(StatNodes.POINTS, true), player.getPoints());
        stats.put(plugin.getMessage(StatNodes.LEVEL, true), player.getLevel());
        stats.put(plugin.getMessage(StatNodes.EXP, true), player.getExp());
    }

    /**
     * @return map of stats for the scoreboard
     */
    @Override
    public Map<String, Integer> getStats() {
        SkillAPI plugin = player.getAPI();
        double health = player.getPlayer().getHealth();
        stats.put(plugin.getMessage(StatNodes.HEALTH, true), (int)health);
        if (plugin.isManaEnabled()) {
            if (player.hasClass()) {
                CustomClass c = player.getAPI().getClass(player.getClassName());
                stats.put(TextFormatter.colorString(c.getManaName()), player.getMana());
            }
        }
        stats.put(plugin.getMessage(StatNodes.POINTS, true), player.getPoints());
        stats.put(plugin.getMessage(StatNodes.LEVEL, true), player.getLevel());
        stats.put(plugin.getMessage(StatNodes.EXP, true), player.getExp());
        return stats;
    }
}
