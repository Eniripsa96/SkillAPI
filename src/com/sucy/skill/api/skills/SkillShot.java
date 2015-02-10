package com.sucy.skill.api.skills;

import org.bukkit.entity.LivingEntity;

/**
 * <p>Interface for skills that can be cast without a direct target</p>
 * <p>Common applications would include firing projectiles, self-targeting
 * skills, and AOE abilities around yourself or where you are looking</p>
 */
public interface SkillShot
{

    /**
     * Casts the skill
     *
     * @param user  user of the skill
     * @param level skill level
     *
     * @return true if could cast, false otherwise
     */
    public boolean cast(LivingEntity user, int level);
}
