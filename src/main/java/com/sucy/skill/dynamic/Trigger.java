/**
 * SkillAPI
 * com.sucy.skill.dynamic.Trigger
 * <p/>
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2014 Steven Sucy
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.dynamic;

import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Possible triggers for dynamic skill effects
 */
public enum Trigger {
    /**
     * Trigger effects when a player casts the skill
     */
    CAST,

    /**
     * Trigger effects when the player crouches
     */
    CROUCH,

    /**
     * Trigger effects when the player takes environmental damage
     */
    ENVIRONMENT_DAMAGE,

    /**
     * Trigger effects when the player inflicts non-skill damage
     */
    PHYSICAL_DAMAGE,

    /**
     * Trigger effects when the player inflicts skill damage
     */
    SKILL_DAMAGE,

    /**
     * Trigger effects when the player dies
     */
    DEATH,

    /**
     * Trigger effects when the player falls to a certain health percentage
     */
    //HEALTH,

    /**
     * Trigger effects when launching a projectile
     */
    LAUNCH,

    /**
     * Trigger effects when the skill is available
     */
    INITIALIZE,

    /**
     * Trigger effects upon killing something
     */
    KILL,

    /**
     * Trigger effects upon hitting the ground
     */
    LAND,

    /**
     * Trigger effects when taking non-skill damage
     */
    TOOK_PHYSICAL_DAMAGE,

    /**
     * Trigger effects when taking skill damage
     */
    TOOK_SKILL_DAMAGE,

    /**
     * Trigger effects when the player quits or unlearns the skill
     */
    CLEANUP;

    /**
     * Retrieves a new component for the trigger
     *
     * @return the component for the trigger
     */
    public TriggerComponent getComponent() {
        return new TriggerComponent();
    }

    /**
     * Component for triggers that can contain needed data and child components
     */
    public class TriggerComponent extends EffectComponent {
        @Override
        public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets) {
            return executeChildren(caster, level, targets);
        }
    }
}
