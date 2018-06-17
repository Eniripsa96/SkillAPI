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
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

/**
 * Applies a flag to each target
 */
public class AttributeMechanic extends EffectComponent
{
    private HashMap<String, AttribTask> tasks = new HashMap<String, AttribTask>();

    private static final String KEY     = "key";
    private static final String AMOUNT  = "amount";
    private static final String SECONDS = "seconds";

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
        String key = settings.getString(KEY, "");
        if (targets.size() == 0 || SkillAPI.getAttributeManager().getAttribute(key) == null)
        {
            return false;
        }

        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        int amount = (int) attr(caster, AMOUNT, level, 5, isSelf);
        double seconds = attr(caster, SECONDS, level, 3.0, isSelf);
        int ticks = (int) (seconds * 20);
        boolean worked = false;
        for (LivingEntity target : targets)
        {
            if (target instanceof Player)
            {
                worked = true;
                PlayerData data = SkillAPI.getPlayerData((Player) target);

                if (tasks.containsKey(data.getPlayerName()))
                {
                    AttribTask old = tasks.remove(data.getPlayerName());
                    if (amount != old.amount)
                        data.addBonusAttributes(key, amount - old.amount);
                    old.cancel();
                }
                else
                    data.addBonusAttributes(key, amount);

                AttribTask task = new AttribTask(data, key, amount);
                tasks.put(data.getPlayerName(), task);
                SkillAPI.schedule(task, ticks);
            }
        }
        return worked;
    }

    private class AttribTask extends BukkitRunnable
    {
        private PlayerData data;
        private String     attrib;
        private int        amount;

        public AttribTask(PlayerData data, String attrib, int amount)
        {
            this.data = data;
            this.attrib = attrib;
            this.amount = amount;
        }

        @Override
        public void run()
        {
            data.addBonusAttributes(attrib, -amount);
            tasks.remove(data.getPlayerName());
        }
    }
}
