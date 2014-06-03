package com.sucy.skill.api.dynamic;

import com.sucy.skill.api.PlayerSkills;
import org.bukkit.entity.Player;

/**
 * Skill embedded data that expires after a certain amount of time
 */
public class TimedEmbedData extends EmbedData {

    private long expiration;

    /**
     * Constructor
     *
     * @param player     player casting the skill
     * @param data       data of the caster
     * @param skill      skill that was cast
     * @param expiration expiration time of the effect
     */
    public TimedEmbedData(Player player, PlayerSkills data, DynamicSkill skill, long expiration) {
        super(player, data, skill);
        this.expiration = expiration;
    }

    /**
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expiration;
    }
}
