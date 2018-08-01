/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.PassiveMechanic
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
package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Executes child components continuously
 */
public class PassiveMechanic extends EffectComponent
{
    private static final String PERIOD = "seconds";

    private static final HashMap<TaskKey, PassiveTask> TASKS = new HashMap<TaskKey, PassiveTask>();

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     *
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets)
    {
        final TaskKey key = new TaskKey(skill.getName(), caster.getUniqueId(), getKey());
        if (TASKS.containsKey(key))
            return false;

        if (targets.size() > 0)
        {
            final boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
            final int period = (int) (attr(caster, PERIOD, level, 1.0, isSelf) * 20);
            final PassiveTask task = new PassiveTask(caster, level, targets, period, key);
            TASKS.put(key, task);

            return true;
        }
        return false;
    }

    /**
     * Stops all passive tasks for the player
     *
     * @param player player to cancel tasks for
     * @param skill  skill to cancel
     */
    public static void stopTasks(LivingEntity player, String skill)
    {
        final TaskKey key = new TaskKey(skill, player.getUniqueId(), null);
        PassiveTask task;
        while ((task = TASKS.remove(key)) != null) {
            task.cancel();
        }
    }

    /**
     * Stops all passive tasks
     */
    public static void stopAll()
    {
        for (PassiveTask task : TASKS.values())
            task.cancel();
        TASKS.clear();
    }

    private class PassiveTask extends BukkitRunnable
    {
        private List<LivingEntity> targets;
        private LivingEntity       caster;
        private int                level;
        private TaskKey            key;

        PassiveTask(LivingEntity caster, int level, List<LivingEntity> targets, int period, TaskKey key)
        {
            this.targets = targets;
            this.caster = caster;
            this.level = level;
            this.key = key;

            SkillAPI.schedule(this, 0, period);
        }

        @Override
        public void run()
        {
            for (int i = 0; i < targets.size(); i++)
            {
                if (targets.get(i).isDead() || !targets.get(i).isValid())
                {
                    targets.remove(i);
                }
            }
            if (!skill.isActive(caster) || targets.size() == 0)
            {
                cancel();
                TASKS.remove(key);
                return;
            }
            else if (caster instanceof Player)
            {
                PlayerSkill data = getSkillData(caster);
                if (data == null || !data.isUnlocked() || !((Player) caster).isOnline())
                {
                    cancel();
                    TASKS.remove(key);
                    return;
                }
            }
            level = skill.getActiveLevel(caster);
            executeChildren(caster, level, targets);

            if (skill.checkCancelled()) {
                TASKS.remove(key);
                cancel();
            }
        }
    }

    /**
     * Task key that allows multiple components to store tasks, but not providing
     * a component matches all for when the tasks are being stopped.
     */
    private static class TaskKey {
        private final String skillName;
        private final UUID uuid;
        private final String componentKey;

        public TaskKey(String skillName, UUID uuid, String componentKey) {
            this.skillName = skillName;
            this.uuid = uuid;
            this.componentKey = componentKey;
        }

        @Override
        public boolean equals(Object other) {
            final TaskKey taskKey = (TaskKey) other;
            return taskKey.skillName.equals(skillName)
                    && taskKey.uuid.equals(uuid)
                    && (taskKey.componentKey == null || componentKey == null || taskKey.componentKey.equals(componentKey));
        }

        @Override
        public int hashCode() {
            return skillName.hashCode() + uuid.hashCode();
        }
    }
}
