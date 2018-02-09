/**
 * SkillAPI
 * com.sucy.skill.manager.ComboManager
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
package com.sucy.skill.manager;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.data.Click;
import com.sucy.skill.log.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Manages click combos with what combos are active and
 * what skills result from various combinations
 */
public class ComboManager
{
    private int       comboSize;
    private boolean[] clicks;

    /**
     * Initializes the combo manager, grabbing settings from
     * the configuration to prepare data
     */
    public ComboManager()
    {
        comboSize = Math.min(SkillAPI.getSettings().getComboSize(), Click.MAX_COMBO_SIZE);
        clicks = new boolean[] {
            false,
            SkillAPI.getSettings().isComboLeft(),
            SkillAPI.getSettings().isComboRight(),
            SkillAPI.getSettings().isComboShift()
        };
    }

    /**
     * Retrieves the accepted size of combos
     *
     * @return the accepted size of combos
     */
    public int getComboSize()
    {
        return comboSize;
    }

    /**
     * Checks whether or not the click with the given ID is enabled
     *
     * @param id ID of the click
     *
     * @return true if enabled, false otherwise
     */
    public boolean isClickEnabled(int id)
    {
        return id < clicks.length && id >= 0 && clicks[id];
    }

    /**
     * Checks whether or not the combo is a valid one
     *
     * @param id ID of the combo
     *
     * @return true if valid, false otherwise
     */
    public boolean isValidCombo(int id)
    {
        if (id <= 0) return false;
        while (id > 0)
        {
            if (!isClickEnabled(Click.BIT_MASK & id))
            {
                return false;
            }
            id >>= Click.BITS;
        }
        return true;
    }

    /**
     * Checks whether or not the combo is a valid one
     *
     * @param id ID of the combo
     *
     * @return true if valid, false otherwise
     */
    public boolean isValidDefaultCombo(int id)
    {
        return isValidCombo(id) && id < (1 << (Click.BITS * comboSize)) && id >= (1 << (Click.BITS * (comboSize - 1)));
    }

    /**
     * Converts a combo ID to clicks
     *
     * @param id combo ID
     *
     * @return click combination or null if invalid
     */
    public List<Click> convertId(int id)
    {
        ArrayList<Click> clicks = new ArrayList<Click>();
        while (id > 0)
        {
            Click click = Click.getById(id & Click.BIT_MASK);
            if (click == null) return null;
            clicks.add(click);
            id >>= Click.BITS;
        }
        Collections.reverse(clicks);
        return clicks;
    }

    /**
     * Compares two combo IDs to see if they conflict
     *
     * @param c1 first combo ID
     * @param c2 second combo ID
     *
     * @return true if conflicts, false otherwise
     */
    public boolean conflicts(int c1, int c2)
    {
        c1 = reverse(c1);
        c2 = reverse(c2);
        while (c1 > 0 && c2 > 0)
        {
            if ((c1 & Click.BIT_MASK) != (c2 & Click.BIT_MASK))
            {
                return false;
            }
            c1 >>= Click.BITS;
            c2 >>= Click.BITS;
        }
        return true;
    }

    /**
     * Reverses a combo order
     *
     * @param id combo ID
     *
     * @return reversed combo ID
     */
    public int reverse(int id)
    {
        int result = 0;
        while (id > 0)
        {
            result <<= Click.BITS;
            result += id & Click.BIT_MASK;
            id >>= Click.BITS;
        }
        return result;
    }

    /**
     * Converts a click combination to an ID
     *
     * @param clicks clicks to convert
     * @param amount number of clicks to convert
     *
     * @return combo ID
     */
    public int convertCombo(Click[] clicks, int amount)
    {
        int id = 0;
        for (int i = 0; i < clicks.length && i < amount; i++)
        {
            id <<= Click.BITS;
            id |= clicks[i].getId();
        }
        return id;
    }

    /**
     * Converts a click combination to an ID
     *
     * @param clicks clicks to convert
     *
     * @return combo ID
     */
    public int convertCombo(Click[] clicks)
    {
        int id = 0;
        for (Click click : clicks)
        {
            id <<= Click.BITS;
            id |= click.getId();
        }
        return id;
    }

    /**
     * Converts a click combination to an ID
     *
     * @param clicks clicks to convert
     *
     * @return combo ID
     */
    public int convertCombo(Collection<Click> clicks)
    {
        return convertCombo(clicks.toArray(new Click[clicks.size()]));
    }

    /**
     * Retrieves a formatted display of the combo
     * based on the language config
     *
     * @param combo the ID of the combo
     *
     * @return formatted string for the combo
     */
    public String getComboString(int combo)
    {
        if (combo == -1) return "";
        return getComboString(convertId(combo));
    }

    /**
     * Retrieves a formatted display of the combo
     * based on the language config
     *
     * @param clicks clicks of the combo
     *
     * @return formatted string for the combo
     */
    public String getComboString(List<Click> clicks)
    {
        if (clicks == null)
            return "";

        String result = "";
        for (Click click : clicks)
        {
            if (result.length() > 0) result += ", ";
            result += click.getName();
        }
        return result;
    }

    /**
     * Retrieves a formatted display of the combo
     * used to save the combo to disk
     *
     * @param combo the ID of the combo
     *
     * @return formatted string for the combo
     */
    public String getSaveString(int combo)
    {
        return getSaveString(convertId(combo));
    }

    /**
     * Retrieves a formatted display of the combo
     * used to save the combo to disk
     *
     * @param clicks clicks of the combo
     *
     * @return formatted string for the combo
     */
    public String getSaveString(List<Click> clicks)
    {
        String result = "";
        for (Click click : clicks)
        {
            if (result.length() > 0) result += ' ';
            result += click.name().charAt(0);
        }
        return result;
    }

    /**
     * Parses a combo from a string using spaces as breaks
     *
     * @param combo combo string
     *
     * @return ID of the combo or -1 if invalid
     */
    public int parseCombo(String combo)
    {
        if (combo == null)
            return -1;

        String[] parts = combo.toLowerCase().split(" ");
        Click[] clicks = new Click[parts.length];
        int i = 0;
        for (String part : parts)
        {
            if (part.equals("l"))
                clicks[i++] = Click.LEFT;
            else if (part.equals("r"))
                clicks[i++] = Click.RIGHT;
            else if (part.equals("s"))
                clicks[i++] = Click.SHIFT;
            else
            {
                Logger.invalid("Invalid combo click type: " + part);
                return -1;
            }
        }

        return convertCombo(clicks);
    }
}
