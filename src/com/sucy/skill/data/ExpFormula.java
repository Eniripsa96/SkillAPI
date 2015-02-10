package com.sucy.skill.data;

/**
 * Represents an experience formula from the settings
 */
public class ExpFormula
{
    private int x, y, z;

    /**
     * Creates a new formula
     *
     * @param x quadratic coefficient
     * @param y linear coefficient
     * @param z intercept
     */
    public ExpFormula(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Calculates the required experience at a given level
     *
     * @param level level to calculate for
     *
     * @return required experience at the level
     */
    public int calculate(int level)
    {
        return x * level * level + y * level + z;
    }
}
