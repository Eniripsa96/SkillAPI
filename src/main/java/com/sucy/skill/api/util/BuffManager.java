/**
 * SkillAPI
 * com.sucy.skill.api.util.BuffManager
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
package com.sucy.skill.api.util;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.UUID;

/**
 * The manager for temporary entity buff data
 */
public class BuffManager {
    private static final HashMap<UUID, BuffData> data = new HashMap<UUID, BuffData>();

    /**
     * Retrieves the buff data for an entity. This returns null if
     * no existing data is found.
     *
     * @param entity entity to retrieve the data for
     * @return the buff data for the entity
     */
    public static BuffData getBuffData(LivingEntity entity) {
        return getBuffData(entity, true);
    }

    /**
     * Retrieves the buff data for an entity, optionally creating new data
     * if none currently exists. If set to false, this will return null
     * if no data currently exists.
     *
     * @param entity entity to get the buff data for
     * @param create whether or not to create new data if it doesn't exist
     * @return the buff data for an enemy
     */
    public static BuffData getBuffData(LivingEntity entity, boolean create) {
        if (entity == null) {
            return null;
        }
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
    public static void clearData(LivingEntity entity) {
        if (entity == null) {
            return;
        }
        BuffData result = data.remove(entity.getUniqueId());
        if (result != null) {
            result.clear();
        }
    }

    /**
     * Adds an offensive buff to the entity
     *
     * @param entity entity to give the buff to
     * @param buff   buff to add
     * @param ticks  ticks to apply the buff for
     */
    public static void addDamageBuff(LivingEntity entity, Buff buff, int ticks) {
        if (entity == null) {
            return;
        }
        getBuffData(entity, true).addDamageBuff(buff, ticks);
    }

    /**
     * Adds a defensive buff to the entity
     *
     * @param entity entity to give the buff to
     * @param buff   buff to add
     * @param ticks  ticks to apply the buff for
     */
    public static void addDefenseBuff(LivingEntity entity, Buff buff, int ticks) {
        if (entity == null) {
            return;
        }
        getBuffData(entity, true).addDefenseBuff(buff, ticks);
    }

    /**
     * Adds an offensive buff to the entity
     *
     * @param entity entity to give the buff to
     * @param buff   buff to add
     * @param ticks  ticks to apply the buff for
     */
    public static void addSkillDamageBuff(LivingEntity entity, Buff buff, int ticks) {
        if (entity == null) {
            return;
        }
        getBuffData(entity, true).addSkillDamageBuff(buff, ticks);
    }

    /**
     * Adds a defensive buff to the entity
     *
     * @param entity entity to give the buff to
     * @param buff   buff to add
     * @param ticks  ticks to apply the buff for
     */
    public static void addSkillDefenseBuff(LivingEntity entity, Buff buff, int ticks) {
        if (entity == null) {
            return;
        }
        getBuffData(entity, true).addSkillDefenseBuff(buff, ticks);
    }

    /**
     * Modifies the amount of dealt damage using damage buff
     * multipliers and bonuses.
     *
     * @param entity entity to use the data of
     * @param damage base damage amount to modify
     * @return modified damage amount
     */
    public static double modifyDealtDamage(LivingEntity entity, double damage) {
        BuffData data = getBuffData(entity, false);
        if (data == null) {
            return damage;
        } else {
            return data.modifyDealtDamage(damage);
        }
    }

    /**
     * Modifies the amount of taken damage using defense buff
     * multipliers and bonuses.
     *
     * @param entity entity to use the data of
     * @param damage base damage amount to modify
     * @return modified damage amount
     */
    public static double modifyTakenDefense(LivingEntity entity, double damage) {
        BuffData data = getBuffData(entity, false);
        if (data == null) {
            return damage;
        } else {
            return data.modifyTakenDamage(damage);
        }
    }

    /**
     * Modifies the amount of dealt damage using damage buff
     * multipliers and bonuses.
     *
     * @param entity entity to use the data of
     * @param damage base damage amount to modify
     * @return modified damage amount
     */
    public static double modifySkillDealtDamage(LivingEntity entity, double damage) {
        BuffData data = getBuffData(entity, false);
        if (data == null) {
            return damage;
        } else {
            return data.modifySkillDealtDamage(damage);
        }
    }

    /**
     * Modifies the amount of taken damage using defense buff
     * multipliers and bonuses.
     *
     * @param entity entity to use the data of
     * @param damage base damage amount to modify
     * @return modified damage amount
     */
    public static double modifySkillTakenDefense(LivingEntity entity, double damage) {
        BuffData data = getBuffData(entity, false);
        if (data == null) {
            return damage;
        } else {
            return data.modifySkillTakenDamage(damage);
        }
    }
}
