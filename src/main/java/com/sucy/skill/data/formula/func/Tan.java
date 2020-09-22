/**
 * SkillAPI
 * com.sucy.skill.data.formula.func.Tan
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
package com.sucy.skill.data.formula.func;

import com.sucy.skill.data.formula.Formula;
import com.sucy.skill.data.formula.IValue;

/**
 * Tangent function
 */
public class Tan implements IValue
{
    private IValue formula;

    /**
     * @param formula wrapped formula
     */
    public Tan(IValue formula)
    {
        this.formula = formula;
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
        return Math.tan(formula.compute(input) * Formula.DEG_TO_RAD);
    }
}
