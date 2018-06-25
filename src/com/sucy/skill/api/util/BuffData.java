/**
 * SkillAPI
 * com.sucy.skill.api.util.BuffData
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
package com.sucy.skill.api.util;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.log.LogType;
import com.sucy.skill.log.Logger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents buffs set on an entity
 */
public class BuffData
{
    private final Map<BuffType, Map<String, Buff>> buffs = new HashMap<BuffType, Map<String, Buff>>();

    private LivingEntity entity;
    private SkillAPI     plugin;

    /**
     * Initializes new buff data for the entity
     *
     * @param entity entity to initialize for
     */
    public BuffData(LivingEntity entity)
    {
        this.plugin = SkillAPI.getPlugin(SkillAPI.class);
        this.entity = entity;
    }

    public double getMultiplier(final BuffType buffType, final String category) {
        return getMultiplier(buffType.name());
    }

    public double getFlatBonus(final BuffType buffType, final String category) {
        return getFlatBonus(buffType.name());
    }

    /**
     * Adds a buff to the buff collection. If a buff already exists with the same
     * key, it will be overwritten.
     *
     * @param type type of buff to add
     * @param buff buff details
     * @param ticks how long to apply the buff for
     */
    public void addBuff(final BuffType type, final Buff buff, final int ticks) {
        if (!buffs.containsKey(type)) {
            buffs.put(type, new HashMap<String, Buff>());
        }

        final Map<String, Buff> typeBuffs = buffs.get(type);
        final Buff conflict = typeBuffs.remove(buff.getKey());
        if (conflict != null)
            conflict.task.cancel();

        typeBuffs.put(buff.getKey(), buff);
        buff.task = new BuffTask(type, buff.getKey()).runTaskLater(plugin, ticks);
    }

    /** @deprecated use {@link BuffData#addBuff(BuffType, Buff, int)} instead */
    @Deprecated
    public void addDamageBuff(Buff buff, int ticks) {
        addBuff(BuffType.DAMAGE, buff, ticks);
    }

    /** @deprecated use {@link BuffData#addBuff(BuffType, Buff, int)} instead */
    @Deprecated
    public void addDefenseBuff(Buff buff, int ticks) {
        addBuff(BuffType.DEFENSE, buff, ticks);
    }

    /** @deprecated use {@link BuffData#addBuff(BuffType, Buff, int)} instead */
    @Deprecated
    public void addSkillDamageBuff(Buff buff, int ticks) {
        addBuff(BuffType.SKILL_DAMAGE, buff, ticks);
    }

    /** @deprecated use {@link BuffData#addBuff(BuffType, Buff, int)} instead */
    @Deprecated
    public void addSkillDefenseBuff(Buff buff, int ticks) {
        addBuff(BuffType.SKILL_DEFENSE, buff, ticks);
    }

    /**
     * Applies all buffs of the given type to the specified value
     *
     * @param type type of buff to apply
     * @param value value to modify
     * @return value after all buff applications
     */
    public double apply(final BuffType type, final double value) {
        // Ignore zeroed out values that shouldn't get buffs
        if (value <= 0) return value;

        final Map<String, Buff> typeBuffs = buffs.get(type);
        if (typeBuffs == null)
            return value;

        double multiplier = 1;
        double bonus = 0;
        Logger.log(LogType.BUFF, 1, "Buffs:");
        for (final Buff buff : typeBuffs.values()) {
            if (buff.isPercent()) {
                Logger.log(LogType.BUFF, 1, "  - x" + buff.getValue());
                multiplier *= buff.getValue();
            } else {
                Logger.log(LogType.BUFF, 1, "  - +" + buff.getValue());
                bonus += buff.getValue();
            }
        }
        Logger.log(LogType.BUFF, 1, "Result: x" + multiplier + ", +" + bonus + ", " + value + " -> " + Math.max(0, value * multiplier + bonus));

        // Negatives aren't well received by bukkit, so return 0 instead
        if (multiplier <= 0) return 0;

        return Math.max(0, value * multiplier + bonus);
    }

    private double getFlatBonus(final String... types) {
        double bonus = 0;
        for (final String type : types) {
            for (final Buff buff : buffs.getOrDefault(type, Collections.emptyMap()).values()) {
                if (!buff.isPercent()) {
                    bonus += buff.getValue();
                }
            }
        }
        // Negatives aren't well received by bukkit, so return 0 instead
        return bonus;
    }

    private double getMultiplier(final String... types) {
        double multiplier = 1;
        for (final String type : types) {
            for (final Buff buff : buffs.getOrDefault(type, Collections.emptyMap()).values()) {
                if (buff.isPercent()) {
                    multiplier *= buff.getValue();
                }
            }
        }
        // Negatives aren't well received by bukkit, so return 0 instead
        return Math.max(0, multiplier);
    }

    /** @deprecated use {@link BuffData#apply(BuffType, double)} instead */
    @Deprecated
    public double modifyDealtDamage(double damage)
    {
        return apply(BuffType.DAMAGE, damage);
    }

    /** @deprecated use {@link BuffData#apply(BuffType, double)} instead */
    @Deprecated
    public double modifyTakenDamage(double damage)
    {
        return apply(BuffType.DEFENSE, damage);
    }

    /** @deprecated use {@link BuffData#apply(BuffType, double)} instead */
    @Deprecated
    public double modifySkillDealtDamage(double damage)
    {
        return apply(BuffType.SKILL_DAMAGE, damage);
    }

    /** @deprecated use {@link BuffData#apply(BuffType, double)} instead */
    @Deprecated
    public double modifySkillTakenDamage(double damage)
    {
        return apply(BuffType.SKILL_DEFENSE, damage);
    }

    /**
     * Clears all buffs on the entity and stops associated tasks.
     */
    public void clear() {
        for (final Map<String, Buff> typeBuffs : buffs.values()) {
            for (final Buff buff : typeBuffs.values()) {
                buff.task.cancel();
            }
        }
        buffs.clear();
        BuffManager.clearData(entity);
    }

    private class BuffTask extends BukkitRunnable
    {
        private final BuffType type;
        private final String key;

        BuffTask(final BuffType type, final String key)
        {
            this.type = type;
            this.key = key;
        }

        @Override
        public void run()
        {
            if (!entity.isValid() || entity.isDead())
            {
                BuffManager.clearData(entity);
                return;
            }

            final Map<String, Buff> typeBuffs = buffs.get(type);
            typeBuffs.remove(key);

            // Clean up buff data if the entity doesn't hold onto any buffs
            if (typeBuffs.size() == 0) {
                buffs.remove(type);
                if (buffs.size() == 0) {
                    BuffManager.clearData(entity);
                }
            }
        }
    }
}
