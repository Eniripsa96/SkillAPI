/**
 * SkillAPI
 * com.sucy.skill.data.Click
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
package com.sucy.skill.data;

import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.SkillAPI;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single click in a click combination
 */
public enum Click
{
    LEFT(1, "L"),
    RIGHT(2, "R"),
    SHIFT(3, "S"),
    LEFT_SHIFT(4, "LS"),
    RIGHT_SHIFT(5, "RS"),
    SPACE(6, "P"),
    Q(7, "Q");

    public static final int BITS           = 3;
    public static final int BIT_MASK       = (1 << BITS) - 1;
    public static final int MAX_COMBO_SIZE = 30 / BITS;

    private static final Click[] CLICKS = new Click[] { null, LEFT, RIGHT, SHIFT, LEFT_SHIFT, RIGHT_SHIFT, SPACE, Q };

    private int id;
    private String key;

    Click(int id, String key)
    {
        this.id = id;
        this.key = key;
    }

    /**
     * @return numeric ID of the click type
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return config key for the click
     */
    public String getKey() {
        return key;
    }

    /**
     * Retrieves the formatted name of the click type
     *
     * @return formatted click type name
     */
    public String getName()
    {
        return TextFormatter.colorString(SkillAPI.getLanguage().getMessage("Combo." + name().toLowerCase()).get(0));
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
        return CLICK_MAP.get(name);
    }

    private static final Map<String, Click> CLICK_MAP = new HashMap<String, Click>() {{
        for (final Click click : Click.values()) {
            put(click.name().toLowerCase(), click);
            put(click.key.toLowerCase(), click);
        }
    }};
}
