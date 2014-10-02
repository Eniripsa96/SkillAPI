package com.sucy.skill.api.skills;

import org.bukkit.entity.Player;

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
     * @param player   player unlocking the skill
     * @param newLevel the new level of the skill
     */
    public void onUpgrade(Player player, int newLevel);

    /**
     * <p>Applies effects when the API starts up or when
     * the player logs in. There will never be effects
     * already applied before this (unless you start it
     * prematurely) so you can just apply them without
     * checking to remove previous effects.</p>
     *
     * @param player player logging in
     * @param level  skill level
     */
    public void onInitialize(Player player, int level);

    /**
     * <p>Stops the effects when the player goes offline
     * or loses the skill</p>
     * <p>This could entail stopping tasks you use for
     * the skill, resetting health or other stats, or
     * other lasting effects you use.</p>
     *
     * @param player player to stop the effects for
     * @param level  active level of the effect
     */
    public void stopEffects(Player player, int level);
}
