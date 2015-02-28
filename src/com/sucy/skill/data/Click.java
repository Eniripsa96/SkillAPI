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
    public static final int MAX_COMBO_SIZE = 30 / BITS;

    private static final Click[] CLICKS = new Click[] { LEFT, RIGHT, SHIFT };

    private String name;
    private int    id;

    private Click(int id)
    {
        this.id = id;
        this.name = TextFormatter.format(this.toString());
    }

    /**
     * Gets the ID of the click type used in compiling combos
     *
     * @return ID of the click type
     */
    public int getId()
    {
        return id;
    }

    /**
     * Retrieves the formatted name of the click type
     *
     * @return formatted click type name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Retrieves a Click by ID. If an invalid ID is provided,
     * this will instead return null.
     *
     * @param id click ID
     *
     * @return Click enum value or null if not found
     */
    public static Click getById(int id)
    {
        if (id < 0 || id >= CLICKS.length) return null;
        return CLICKS[id];
    }

    /**
     * Retrieves a Click by name. If an invalid name is provided,
     * this will return null instead.
     *
     * @param name click name
     *
     * @return Click enum value or null if not found
     */
    public static Click getByName(String name)
    {
        if (name == null) return null;
        name = name.toLowerCase();
        if (name.equals("left"))
        {
            return LEFT;
        }
        else if (name.equals("right"))
        {
            return RIGHT;
        }
        else if (name.equals("shift"))
        {
            return SHIFT;
        }
        return null;
    }
}
