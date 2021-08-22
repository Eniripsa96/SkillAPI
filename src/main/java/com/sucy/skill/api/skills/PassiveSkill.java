/**
 * SkillAPI
 * com.sucy.skill.api.skills.PassiveSkill
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
    void update(LivingEntity user, int prevLevel, int newLevel);

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
    void initialize(LivingEntity user, int level);

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
    void stopEffects(LivingEntity user, int level);
}
