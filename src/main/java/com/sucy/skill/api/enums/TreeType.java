/**
 * SkillAPI
 * com.sucy.skill.api.enums.TreeType
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
package com.sucy.skill.api.enums;

/**
 * <p>Types of skill tree arrangements that can be used in the configuration.</p>
 */
public enum TreeType
{
    /**
     * A basic arrangement that puts base skills on the left, and the skills
     * that require those directly to the right of them.
     */
    BASIC_HORIZONTAL("BasicHorizontal"),

    /**
     * A basic arrangement that puts base skills at the top, and the
     * skills that require those directly below them.
     */
    BASIC_VERTICAL("BasicVertical"),

    /**
     * Arranges skills by their initial level requirement, putting
     * lowest level skills to the left and higher level skills to the right.
     */
    LEVEL_HORIZONTAL("LevelHorizontal"),

    /**
     * Arranges skills by their initial level requirement, putting
     * lowest level skills at the top and higher level skills at the bottom.
     */
    LEVEL_VERTICAL("LevelVertical"),

    /**
     * Arranges basic skills not needed by other skills on the left hand side,
     * other basic skills at the top right, and the skills that require those
     * below them.
     */
    REQUIREMENT("Requirement"),;

    /**
     * Key for the skill tree arrangement used in the configuration
     */
    private String key;

    /**
     * Enum constructor
     *
     * @param key skill tree arrangement configuration key
     */
    TreeType(String key)
    {
        this.key = key;
    }

    /**
     * Retrieves the configuration key for the skill tree arrangement
     *
     * @return configuration key for the skill tree arrangement
     */
    public String getKey()
    {
        return key;
    }
}
