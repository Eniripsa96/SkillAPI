package com.sucy.skill.api.util;

/**
 * Represents a buff given to an entity
 */
public class Buff
{
    private double value;
    private boolean percent;

    /**
     * Constructs a new buff
     *
     * @param value      value of the buff
     * @param multiplier whether the value is a multiplier or a flat bonus
     */
    public Buff(double value, boolean multiplier)
    {
        this.value = value;
        this.percent = multiplier;
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
