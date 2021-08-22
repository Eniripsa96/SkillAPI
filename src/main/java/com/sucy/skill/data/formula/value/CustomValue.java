/**
 * SkillAPI
 * com.sucy.skill.data.formula.value.CustomValue
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
package com.sucy.skill.data.formula.value;

import com.sucy.skill.data.formula.IValue;

/**
 * A custom defined value for a formula
 */
public class CustomValue implements IValue
{
    private String token;
    private int    index;

    /**
     * A defined value used in formulas
     *
     * @param token equation token
     */
    public CustomValue(String token)
    {
        this.token = token;
    }

    /**
     * Sets the argument index for the value.
     * This is handled by formulas so you shouldn't
     * need to use this.
     *
     * @param index argument index
     */
    public void setIndex(int index)
    {
        this.index = index;
    }

    /**
     * @return defining token
     */
    public String getToken()
    {
        return token;
    }

    /**
     * Gets the value using the inputs
     *
     * @param input the input data
     *
     * @return result value
     */
    @Override
    public double compute(double... input)
    {
        return input[index];
    }
}
