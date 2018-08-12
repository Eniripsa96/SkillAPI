/**
 * SkillAPI
 * com.sucy.skill.cast.IndicatorType
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
package com.sucy.skill.cast;

/**
 * The type of indicator display to use
 */
public enum IndicatorType
{
    // 3-dimensional effect (sphere, cuboid, etc.)
    DIM_3("2D"),

    // 2-dimensional effect (circle, rectangle, etc.)
    DIM_2("3D"),

    // No effect
    NONE("None");

    private String key;

    IndicatorType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    /**
     * Gets the indicator type by key
     *
     * @param key key
     *
     * @return indicator type
     */
    public static IndicatorType getByKey(String key)
    {
        if (key == null)
            return NONE;
        else if (key.equalsIgnoreCase("3D"))
            return DIM_3;
        else if (key.equalsIgnoreCase("2D"))
            return DIM_2;
        else
            return NONE;
    }
}
