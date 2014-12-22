package com.sucy.skill.api.skills;

import org.bukkit.entity.LivingEntity;

/**
 * <p>Interface for skills that require a specific target to cast</p>
 */
public interface TargetSkill
{

    /**
     * Casts the skill
     *
     * @param user   user of the skill
     * @param target target of the skill
     * @param level  skill level
     * @param ally   whether or not the target is an ally
     *
     * @return true if could cast, false otherwise
     */
    public boolean cast(LivingEntity user, LivingEntity target, int level, boolean ally);
}
