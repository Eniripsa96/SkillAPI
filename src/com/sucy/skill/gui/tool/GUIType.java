/**
 * SkillAPI
 * com.sucy.skill.gui.tool.GUIType
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
package com.sucy.skill.gui.tool;

import com.sucy.skill.SkillAPI;

public enum GUIType
{
    CLASS_SELECTION(0, "cs"),
    CLASS_DETAILS(1, "cd"),
    SKILL_TREE(2, "st"),
    ATTRIBUTES(3, "a");

    private int    id;
    private String prefix;

    GUIType(int id, String prefix)
    {
        this.id = id;
        this.prefix = prefix;
    }

    public GUIType next()
    {
        return cycle(1);
    }

    public GUIType prev()
    {
        return cycle(-1);
    }

    private GUIType cycle(int direction)
    {
        GUIType type = ORDERED[(id + ORDERED.length + direction) % ORDERED.length];
        if (type == ATTRIBUTES && !SkillAPI.getSettings().isAttributesEnabled())
            return ORDERED[(id + ORDERED.length + 2 * direction) % ORDERED.length];
        return type;
    }

    public String getPrefix()
    {
        return prefix;
    }

    private static final GUIType[] ORDERED = new GUIType[]
        {
            CLASS_SELECTION,
            CLASS_DETAILS,
            SKILL_TREE,
            ATTRIBUTES
        };
}
