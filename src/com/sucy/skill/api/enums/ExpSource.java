/**
 * SkillAPI
 * com.sucy.skill.api.enums.ExpSource
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
 * <p>A collection of reasons why a player would gain experience</p>
 * <p>This is used when gaining experience to determine where it came
 * from so some effects can act differently accordingly.</p>
 */
public enum ExpSource
{
    /**
     * Experience resulted from defeating a monster
     */
    MOB(0x1),

    /**
     * Experience resulted from breaking a block
     */
    BLOCK_BREAK(0x2),

    /**
     * Experience resulted from placing a block
     */
    BLOCK_PLACE(0x4),

    /**
     * Experience resulted from crafting an item
     */
    CRAFT(0x8),

    /**
     * Experience resulted from an issued command
     */
    COMMAND(0x10),

    /**
     * Experience resulted from an unspecified reason
     */
    SPECIAL(0x20),

    /**
     * Experience from Bottles o' Enchanting
     */
    EXP_BOTTLE(0x40),

    /**
     * Experience from smelting ore
     */
    SMELT(0x80),

    /**
     * Experience from a quest
     */
    QUEST(0x100);

    /**
     * The ID of the experience source which should be a unique power of 2 (or bit value)
     */
    private int id;

    /**
     * Enum constructor
     *
     * @param id ID of the experience source (should use a unique bit)
     */
    ExpSource(int id)
    {
        this.id = id;
    }

    /**
     * <p>Retrieves the ID of the experience source.</p>
     *
     * @return ID of the experience source
     */
    public int getId()
    {
        return id;
    }
}
