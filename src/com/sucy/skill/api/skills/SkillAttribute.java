/**
 * SkillAPI
 * com.sucy.skill.api.skills.SkillAttribute
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
package com.sucy.skill.api.skills;

/**
 * <p>Names of default attributes for skills</p>
 */
public final class SkillAttribute
{
    /**
     * <p>Cooldown attribute of a skill</p>
     * <p>This is not required by passive abilities</p>
     */
    public static final String COOLDOWN = "cooldown";

    /**
     * <p>Mana cost attribute of a skill</p>
     * <p>This is not required by passive abilities</p>
     */
    public static final String MANA = "mana";

    /**
     * <p>Level requirement attribute of a skill</p>
     */
    public static final String LEVEL = "level";

    /**
     * <p>Skill point cost attribute of a skill</p>
     */
    public static final String COST = "cost";

    /**
     * Range of a target skill
     */
    public static final String RANGE = "range";
}
