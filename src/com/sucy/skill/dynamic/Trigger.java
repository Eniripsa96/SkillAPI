/**
 * SkillAPI
 * com.sucy.skill.dynamic.Trigger
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
package com.sucy.skill.dynamic;

import com.sucy.skill.api.event.PhysicalDamageEvent;
import com.sucy.skill.api.event.PlayerLandEvent;
import com.sucy.skill.api.event.SkillDamageEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;

/**
 * Possible triggers for dynamic skill effects
 */
public enum Trigger
{
    BLOCK_BREAK(BlockBreakEvent.class),
    BLOCK_PLACE(BlockPlaceEvent.class),
    CAST(null),
    CLEANUP(null),
    CROUCH(PlayerToggleSneakEvent.class),
    DEATH(EntityDeathEvent.class),
    ENVIRONMENT_DAMAGE(EntityDamageEvent.class),
    //HEALTH,
    INITIALIZE(null),
    KILL(EntityDeathEvent.class),
    LAND(PlayerLandEvent.class),
    LAUNCH(ProjectileLaunchEvent.class),
    PHYSICAL_DAMAGE(PhysicalDamageEvent.class),
    SKILL_DAMAGE(SkillDamageEvent.class),
    TOOK_PHYSICAL_DAMAGE(PhysicalDamageEvent.class),
    TOOK_SKILL_DAMAGE(SkillDamageEvent.class);

    private Class<? extends Event> event;

    Trigger(Class<? extends Event> event) {
        this.event = event;
    }

    public Class<? extends Event> getEvent() {
        return event;
    }

    /**
     * Retrieves a new component for the trigger
     *
     * @return the component for the trigger
     */
    public TriggerComponent getComponent()
    {
        return new TriggerComponent();
    }

    /**
     * Component for triggers that can contain needed data and child components
     */
    public class TriggerComponent extends EffectComponent
    {
        @Override
        public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets)
        {
            return executeChildren(caster, level, targets);
        }
    }
}
