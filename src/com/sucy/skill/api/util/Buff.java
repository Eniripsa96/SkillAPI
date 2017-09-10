/**
 * SkillAPI
 * com.sucy.skill.api.util.Buff
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

import org.bukkit.scheduler.BukkitTask;

/**
 * Represents a buff given to an entity
 */
public class Buff
{
    private double  value;
    private boolean percent;
    private String  key;

    BukkitTask task;

    /**
     * Constructs a new buff
     *
     * @param value      value of the buff
     * @param multiplier whether the value is a multiplier or a flat bonus
     */
    @Deprecated
    public Buff(double value, boolean multiplier)
    {
        this("Default", value, multiplier);
    }

    /**
     * Constructs a new buff
     *
     * @param key        unique identifier for the buff to prevent overlap
     * @param value      value of the buff
     * @param multiplier whether the value is a multiplier or a flat bonus
     */
    public Buff(String key, double value, boolean multiplier)
    {
        this.key = key;
        this.value = value;
        this.percent = multiplier;
    }

    public String getKey()
    {
        return key;
    }

    public double getValue()
    {
        return value;
    }

    public boolean isPercent()
    {
        return percent;
    }
}
