package com.sucy.skill.api.skills;

import org.bukkit.entity.LivingEntity;

/**
 * <p>Interface for skills that cannot be cast
 * but instead apply effects continuously such
 * as buffs or increased stats.</p>
 */
public interface PassiveSkill
{
    /**
     * <p>Applies the skill effects when a player upgrades the skill
     * in their skill tree</p>
     * <p>The skill may or not be already unlocked so include the
     * proper checks if you are going to be removing previous
     * effects.</p>
     *
     * @param user      user to refresh the effect for
     * @param prevLevel previous skill level
     * @param newLevel  new skill level
     */
    public void update(LivingEntity user, int prevLevel, int newLevel);

    /**
     * <p>Applies effects when the API starts up or when
     * the player logs in. There will never be effects
     * already applied before this (unless you start it
     * prematurely) so you can just apply them without
     * checking to remove previous effects.</p>
     *
     * @param user  user to initialize the effects for
     * @param level skill level
     */
    public void initialize(LivingEntity user, int level);

    /**
     * <p>Stops the effects when the player goes offline
     * or loses the skill</p>
     * <p>This could entail stopping tasks you use for
     * the skill, resetting health or other stats, or
     * other lasting effects you use.</p>
     *
     * @param user  user to stop the effects for
     * @param level skill level
     */
    public void stopEffects(LivingEntity user, int level);
}
