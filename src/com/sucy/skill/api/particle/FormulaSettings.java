/**
 * SkillAPI
 * com.sucy.skill.api.particle.FormulaSettings
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
package com.sucy.skill.api.particle;

import com.rit.sucy.config.parse.DataSection;
import com.sucy.skill.data.formula.Formula;
import com.sucy.skill.data.formula.IValue;

public class FormulaSettings
{
    public final IValue formulaX;
    public final IValue formulaY;
    public final IValue formulaZ;

    public final int     frequency;
    public final int     duration;
    public final double  cos;
    public final double  sin;
    public final boolean polar;

    public FormulaSettings(
        IValue formulaX,
        IValue formulaY,
        IValue formulaZ,
        int frequency,
        int duration,
        boolean polar
    )
    {
        this.formulaX = formulaX;
        this.formulaY = formulaY;
        this.formulaZ = formulaZ;
        this.frequency = frequency;
        this.polar = polar;
        this.duration = duration;

        int steps = duration / frequency;
        double angle = Math.PI * 2 / steps;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
    }

    public FormulaSettings(DataSection settings)
    {
        this(
            new Formula(settings.getString("formula-x")),
            new Formula(settings.getString("formula-y")),
            new Formula(settings.getString("formula-z")),
            (int) (settings.getDouble("frequency") * 20),
            (int) (settings.getDouble("duration") * 20),
            settings.getBoolean("polar")
        );
    }
}
