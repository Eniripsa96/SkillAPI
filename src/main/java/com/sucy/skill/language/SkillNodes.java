/**
 * SkillAPI
 * com.sucy.skill.language.SkillNodes
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
package com.sucy.skill.language;

/**
 * <p>Nodes for the language file pertaining to the skill tree</p>
 * <p>This is primarily for the API retrieving config messages.
 * You shouldn't need to use these values at all.</p>
 */
public class SkillNodes
{

    public static final String

        /**
         * Base node of the skill tree nodes
         */
        BASE = "Skill Tree.",

    /**
     * Title for a skill display
     */
    TITLE = BASE + "title",

    /**
     * Type of the skill
     */
    TYPE = BASE + "type",

    /**
     * Requirement of a skill
     */
    REQUIREMENT_BASE = BASE + "requirement.",

    /**
     * When a requirement is met
     */
    REQUIREMENT_MET = REQUIREMENT_BASE + "met",

    /**
     * When a requirement is not met
     */
    REQUIREMENT_NOT_MET = REQUIREMENT_BASE + "not-met",

    /**
     * Attribute of a skill
     */
    ATTRIBUTE_BASE = BASE + "attribute.",

    /**
     * When an attribute is increasing
     */
    ATTRIBUTE_CHANGING = ATTRIBUTE_BASE + "changing",

    /**
     * When an attribute is decreasing
     */
    ATTRIBUTE_NOT_CHANGING = ATTRIBUTE_BASE + "not-changing",

    /**
     * Layout for the skill display
     */
    LAYOUT = BASE + "layout";
}
