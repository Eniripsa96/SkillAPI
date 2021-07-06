/**
 * SkillAPI
 * com.sucy.skill.task.RemoveTask
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
package com.sucy.skill.task;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.util.BuffManager;
import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.dynamic.mechanic.WolfMechanic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * A simple task for removing an entity after a duration
 */
public class RemoveTask extends BukkitRunnable {
    private List<? extends Entity> entities;

    /**
     * Initializes a new task to remove the entity after the
     * given number of ticks.
     *
     * @param entities entities to remove
     * @param ticks    ticks to wait before removing the entity
     */
    public RemoveTask(List<? extends Entity> entities, int ticks) {
        this.entities = entities;
        SkillAPI.schedule(this, ticks);
    }

    /**
     * Removes the entity once the time is up
     */
    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        // Clear skill setup
        for (Entity entity : entities) {
            if (entity.hasMetadata(WolfMechanic.SKILL_META)) {
                final List<String> skills = (List<String>) SkillAPI.getMeta(entity, WolfMechanic.SKILL_META);
                final int level = SkillAPI.getMetaInt(entity, WolfMechanic.LEVEL);
                for (final String skillName : skills) {
                    final Skill skill = SkillAPI.getSkill(skillName);
                    if (skill instanceof PassiveSkill) {
                        ((PassiveSkill) skill).stopEffects((LivingEntity) entity, level);
                    }
                }

                DynamicSkill.clearCastData((LivingEntity) entity);
                FlagManager.clearFlags((LivingEntity) entity);
                BuffManager.clearData((LivingEntity) entity);
            }

            // Remove entity
            if (entity.isValid()) {
                entity.remove();
            }
        }
    }
}
