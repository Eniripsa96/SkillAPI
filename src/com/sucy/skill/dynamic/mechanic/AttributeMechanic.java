/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.FlagMechanic
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
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Applies a flag to each target
 */
public class AttributeMechanic extends MechanicComponent {
    private static final String KEY       = "key";
    private static final String AMOUNT    = "amount";
    private static final String SECONDS   = "seconds";
    private static final String STACKABLE = "stackable";

    private final Map<Integer, Map<String, AttribTask>> tasks = new HashMap<>();

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
        String key = settings.getString(KEY, "");
        if (targets.size() == 0 || SkillAPI.getAttributeManager().getAttribute(key) == null) {
            return false;
        }

        final Map<String, AttribTask> casterTasks = tasks.computeIfAbsent(caster.getEntityId(), HashMap::new);
        final boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        final int amount = (int) parseValues(caster, AMOUNT, level, 5);
        final double seconds = parseValues(caster, SECONDS, level, 3.0);
        final boolean stackable = settings.getString(STACKABLE, "false").equalsIgnoreCase("true");
        final int ticks = (int) (seconds * 20);

        boolean worked = false;
        for (LivingEntity target : targets) {
            if (target instanceof Player) {
                worked = true;
                final PlayerData data = SkillAPI.getPlayerData((Player) target);

                if (casterTasks.containsKey(data.getPlayerName()) && !stackable) {
                    final AttribTask old = casterTasks.remove(data.getPlayerName());
                    if (amount != old.amount) { data.addBonusAttributes(key, amount - old.amount); }
                    old.cancel();
                } else { data.addBonusAttributes(key, amount); }

                final AttribTask task = new AttribTask(caster.getEntityId(), data, key, amount);
                casterTasks.put(data.getPlayerName(), task);
                if (ticks >= 0) {
                    SkillAPI.schedule(task, ticks);
                }
            }
        }
        return worked;
    }

    @Override
    public String getKey() {
        return "attribute";
    }

    @Override
    protected void doCleanUp(final LivingEntity user) {
        final Map<String, AttribTask> casterTasks = tasks.remove(user.getEntityId());
        if (casterTasks != null) {
            casterTasks.values().forEach(AttribTask::stop);
        }
    }

    private class AttribTask extends BukkitRunnable {
        private PlayerData data;
        private String     attrib;
        private int        amount;
        private int        id;
        private boolean running = false;
        private boolean stopped = false;

        AttribTask(int id, PlayerData data, String attrib, int amount) {
            this.id = id;
            this.data = data;
            this.attrib = attrib;
            this.amount = amount;
        }

        public void stop() {
            if (!stopped) {
                stopped = true;
                run();
                if (running) {
                    cancel();
                }
            }
        }

        @Override
        public BukkitTask runTaskLater(final Plugin plugin, final long delay) {
            running = true;
            return super.runTaskLater(plugin, delay);
        }

        @Override
        public void run() {
            data.addBonusAttributes(attrib, -amount);
            if (tasks.containsKey(id)) {
                tasks.get(id).remove(data.getPlayerName());
            }
            running = false;
        }
    }
}
