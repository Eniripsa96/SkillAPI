package com.sucy.skill.api.skill;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * <p>Interface for skills that require a specific target to cast</p>
 */
public interface TargetSkill {

    /**
     * Casts the skill
     *
     * @param player player who is casting the skill
     * @param target entity that was right clicked
     * @param level  current level of the skill
     * @param ally   whether or not the target is an ally
     * @return       true if could cast, false otherwise
     */
    public boolean cast(Player player, LivingEntity target, int level, boolean ally);
}
