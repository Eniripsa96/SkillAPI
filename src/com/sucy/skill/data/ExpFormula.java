package com.sucy.skill.data;

public class ExpFormula
{

    private int x, y, z, w;

    public ExpFormula(int x, int y, int z, int w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public int calculate(int level)
    {
        int plusY = level + y;
        return x * plusY * plusY + level * z + w;
    }
}
