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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Executes child components continuously
 */
public class PassiveMechanic extends MechanicComponent {
    private static final String PERIOD = "seconds";

    private final Map<Integer, PassiveTask> tasks = new HashMap<>();

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
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets) {
        if (tasks.containsKey(caster.getEntityId())) { return false; }

        if (targets.size() > 0) {
            final int period = (int) (parseValues(caster, PERIOD, level, 1.0) * 20);
            final PassiveTask task = new PassiveTask(caster, level, targets, period);
            tasks.put(caster.getEntityId(), task);

            return true;
        }
        return false;
    }

    @Override
    public String getKey() {
        return "passive";
    }

    @Override
    protected void doCleanUp(final LivingEntity caster) {
        final PassiveTask task = tasks.remove(caster.getEntityId());
        if (task != null) {
            task.cancel();
        }
    }

    private class PassiveTask extends BukkitRunnable {
        private List<LivingEntity> targets;
        private LivingEntity       caster;
        private int                level;

        PassiveTask(LivingEntity caster, int level, List<LivingEntity> targets, int period) {
            this.targets = new ArrayList<>(targets);
            this.caster = caster;
            this.level = level;

            SkillAPI.schedule(this, 0, period);
        }

        @Override
        public void cancel() {
            super.cancel();
            tasks.remove(caster.getEntityId());
        }

        @Override
        public void run() {
            for (int i = 0; i < targets.size(); i++) {
                if (targets.get(i).isDead() || !targets.get(i).isValid()) {
                    targets.remove(i);
                }
            }
            if (!skill.isActive(caster) || targets.size() == 0) {
                cancel();
                return;
            } else if (caster instanceof Player) {
                PlayerSkill data = getSkillData(caster);
                if (data == null || !data.isUnlocked() || !((Player) caster).isOnline()) {
                    cancel();
                    return;
                }
            }
            level = skill.getActiveLevel(caster);
            executeChildren(caster, level, targets);

            if (skill.checkCancelled()) {
                cancel();
            }
        }
    }
}
