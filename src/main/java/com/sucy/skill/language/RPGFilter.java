/**
 * SkillAPI
 * com.sucy.skill.language.RPGFilter
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

import com.rit.sucy.config.CustomFilter;

/**
 * Various text filters used by SkillAPI
 */
public class RPGFilter
{
    public static final CustomFilter NAME       = new CustomFilter("{name}", "");
    public static final CustomFilter MAX        = new CustomFilter("{max}", "");
    public static final CustomFilter TYPE       = new CustomFilter("{type}", "");
    public static final CustomFilter LEVEL      = new CustomFilter("{level}", "");
    public static final CustomFilter GROUP      = new CustomFilter("{group}", "");
    public static final CustomFilter CLASS      = new CustomFilter("{class}", "");
    public static final CustomFilter POINTS     = new CustomFilter("{points}", "");
    public static final CustomFilter COOLDOWN   = new CustomFilter("{cooldown}", "");
    public static final CustomFilter SKILL      = new CustomFilter("{skill}", "");
    public static final CustomFilter MANA       = new CustomFilter("{mana}", "");
    public static final CustomFilter COST       = new CustomFilter("{cost}", "");
    public static final CustomFilter MISSING    = new CustomFilter("{missing}", "");
    public static final CustomFilter DURATION   = new CustomFilter("{duration}", "");
    public static final CustomFilter PROFESSION = new CustomFilter("{profession}", "");
    public static final CustomFilter EXP        = new CustomFilter("{exp}", "");
    public static final CustomFilter ITEM       = new CustomFilter("{item}", "");
    public static final CustomFilter ACCOUNT    = new CustomFilter("{account}", "");
    public static final CustomFilter LIST       = new CustomFilter("{list}", "");
    public static final CustomFilter SCHEME     = new CustomFilter("{scheme}", "");
}
