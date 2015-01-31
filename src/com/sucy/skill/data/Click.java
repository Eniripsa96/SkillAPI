package com.sucy.skill.data;

import com.rit.sucy.text.TextFormatter;

/**
 * Represents a single click in a click combination
 */
public enum Click
{
    LEFT(0),
    RIGHT(1),
    SHIFT(2),;

    public static final int BITS           = 2;
    public static final int BIT_MASK       = (1 << BITS) - 1;
    public static final int MAX_COMBO_SIZE = 32 / BITS;

    private static final Click[] CLICKS = new Click[] { LEFT, RIGHT, SHIFT };

    private String name;
    private int    id;

    private Click(int id)
    {
        this.id = id;
        this.name = TextFormatter.format(this.toString());
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public static Click getById(int id)
    {
        return CLICKS[id];
    }
}
