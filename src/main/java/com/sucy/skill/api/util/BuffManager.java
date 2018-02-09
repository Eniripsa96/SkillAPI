/**
 * SkillAPI
 * com.sucy.skill.api.util.BuffManager
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

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.UUID;

/**
 * The manager for temporary entity buff data
 */
public class BuffManager
{
    private static final HashMap<UUID, BuffData> data = new HashMap<UUID, BuffData>();

    /**
     * Retrieves the buff data for an entity. This returns null if
     * no existing data is found.
     *
     * @param entity entity to retrieve the data for
     *
     * @return the buff data for the entity
     */
    public static BuffData getBuffData(final LivingEntity entity) {
        return getBuffData(entity, true);
    }

    /**
     * Retrieves the buff data for an entity, optionally creating new data
     * if none currently exists. If set to false, this will return null
     * if no data currently exists.
     *
     * @param entity entity to get the buff data for
     * @param create whether or not to create new data if it doesn't exist
     *
     * @return the buff data for an enemy
     */
    public static BuffData getBuffData(final LivingEntity entity, final boolean create) {
        if (entity == null) return null;

        if (!data.containsKey(entity.getUniqueId()) && create) {
            data.put(entity.getUniqueId(), new BuffData(entity));
        }
        return data.get(entity.getUniqueId());
    }

    /**
     * Clears the buffs for an entity
     *
     * @param entity entity to clear the buffs for
     */
    public static void clearData(final LivingEntity entity) {
        if (entity == null) return;

        final BuffData result = data.remove(entity.getUniqueId());
        if (result != null) {
            result.clear();
        }
    }

    /**
     * Adds an offensive buff to the entity
     *
     * @param entity entity to give the buff to
     * @param type   type of buff to add
     * @param buff   buff to add
     * @param ticks  ticks to apply the buff for
     */
    public static void addBuff(final LivingEntity entity, final BuffType type, final Buff buff, final int ticks) {
        if (entity == null) return;
        getBuffData(entity, true).addBuff(type, buff, ticks);
    }

    /** @deprecated use {@link BuffManager#addBuff(LivingEntity, BuffType, Buff, int)} instead */
    @Deprecated
    public static void addDamageBuff(final LivingEntity entity, final Buff buff, final int ticks) {
        addBuff(entity, BuffType.DAMAGE, buff, ticks);
    }

    /** @deprecated use {@link BuffManager#addBuff(LivingEntity, BuffType, Buff, int)} instead */
    @Deprecated
    public static void addDefenseBuff(final LivingEntity entity, final Buff buff, final int ticks) {
        addBuff(entity, BuffType.DEFENSE, buff, ticks);
    }

    /** @deprecated use {@link BuffManager#addBuff(LivingEntity, BuffType, Buff, int)} instead */
    @Deprecated
    public static void addSkillDamageBuff(final LivingEntity entity, final Buff buff, final int ticks) {
        addBuff(entity, BuffType.SKILL_DAMAGE, buff, ticks);
    }

    /** @deprecated use {@link BuffManager#addBuff(LivingEntity, BuffType, Buff, int)} instead */
    @Deprecated
    public static void addSkillDefenseBuff(final LivingEntity entity, final Buff buff, final int ticks) {
        addBuff(entity, BuffType.SKILL_DEFENSE, buff, ticks);
    }

    /**
     * Modifies the amount of dealt damage using damage buff
     * multipliers and bonuses.
     *
     * @param entity entity to use the data of
     * @param type   type of buffs to apply
     * @param amount base amount to modify
     *
     * @return modified number
     */
    public static double apply(final LivingEntity entity, final BuffType type, final double amount)
    {
        final BuffData data = getBuffData(entity, false);
        return data == null ? amount : data.apply(type, amount);
    }

    /** @deprecated use {@link BuffManager#apply(LivingEntity, BuffType, double)} instead */
    @Deprecated
    public static double modifyDealtDamage(final LivingEntity entity, final double damage) {
        return apply(entity, BuffType.DAMAGE, damage);
    }

    /** @deprecated use {@link BuffManager#apply(LivingEntity, BuffType, double)} instead */
    @Deprecated
    public static double modifyTakenDefense(final LivingEntity entity, final double damage) {
        return apply(entity, BuffType.DEFENSE, damage);
    }

    /** @deprecated use {@link BuffManager#apply(LivingEntity, BuffType, double)} instead */
    @Deprecated
    public static double modifySkillDealtDamage(LivingEntity entity, double damage) {
        return apply(entity, BuffType.SKILL_DAMAGE, damage);
    }

    /** @deprecated use {@link BuffManager#apply(LivingEntity, BuffType, double)} instead */
    @Deprecated
    public static double modifySkillTakenDefense(LivingEntity entity, double damage) {
        return apply(entity, BuffType.SKILL_DEFENSE, damage);
    }
}
