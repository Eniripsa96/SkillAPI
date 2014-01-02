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
    public Map<String, Integer> getStats() {
        SkillAPI plugin = player.getAPI();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        double health = plugin.getServer().getPlayer(player.getName()).getHealth();
        map.put(plugin.getMessage(StatNodes.HEALTH, true), (int)health);
        if (plugin.isManaEnabled()) {
            if (player.hasClass()) {
                CustomClass c = player.getAPI().getClass(player.getClassName());
                map.put(TextFormatter.colorString(c.getManaName()), player.getMana());
            }
        }
        map.put(plugin.getMessage(StatNodes.POINTS, true), player.getPoints());
        map.put(plugin.getMessage(StatNodes.LEVEL, true), player.getLevel());
        map.put(plugin.getMessage(StatNodes.EXP, true), player.getExp());
        return map;
    }
}
