/**
 * SkillAPI
 * com.sucy.skill.tools.GUIData
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package com.sucy.skill.tools;

import com.rit.sucy.config.parse.DataSection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GUIData
{
    private static final String
        ROWS = "rows",
        SLOTS = "slots";

    public final HashMap<String, Integer> slots = new HashMap<String, Integer>();
    private int rows = 6;

    public GUIData() { }

    public GUIData(DataSection data)
    {
        rows = data.getInt(ROWS);
        DataSection slots = data.getSection(SLOTS);
        for (String key : slots.keys())
            this.slots.put(key, slots.getInt(key));
    }

    public int getSize()
    {
        return rows * 9;
    }

    public void shrink()
    {
        if (rows > 1)
            rows--;

        int size = getSize();
        Iterator<Map.Entry<String, Integer>> iterator = slots.entrySet().iterator();
        while (iterator.hasNext())
        {
            if (iterator.next().getValue() >= size)
                iterator.remove();
        }
    }

    public void grow()
    {
        if (rows < 6)
            rows++;
    }

    public boolean isValid()
    {
        return slots.size() > 0;
    }

    public void save(DataSection data)
    {
        data.set(ROWS, rows);
        data.set(SLOTS, slots);
    }
}
