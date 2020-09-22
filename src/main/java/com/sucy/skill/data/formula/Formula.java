/**
 * SkillAPI
 * com.sucy.skill.data.formula.Formula
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
package com.sucy.skill.data.formula;

import com.sucy.skill.data.formula.func.*;
import com.sucy.skill.data.formula.operator.*;
import com.sucy.skill.data.formula.value.CustomValue;
import com.sucy.skill.data.formula.value.ValueNum;
import com.sucy.skill.log.Logger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a basic math equation read from left to right, ignoring
 * order of operations. Currently this only supports addition, subtraction,
 * multiplication, and division.
 */
public class Formula implements IValue
{
    public static final double DEG_TO_RAD = Math.PI / 180;

    private static final HashMap<Character, IOperator> OPS = new HashMap<Character, IOperator>()
    {{
        put('+', new Addition());
        put('-', new Subtraction());
        put('*', new Multiplication());
        put('/', new Division());
        put('%', new Modulo());
        put('^', new Exponent());
        put('_', new Log());
    }};

    private static final HashMap<String, Class<? extends IValue>> FUNCS = new HashMap<String, Class<? extends IValue>>()
    {{
        put("abs", Abs.class);
        put("ceil", Ceil.class);
        put("cos", Cos.class);
        put("floor", Floor.class);
        put("sqrt", Root.class);
        put("sign", Sign.class);
        put("sin", Sin.class);
        put("sq", Square.class);
        put("tan", Tan.class);
    }};

    private IValue[]    values;
    private IOperator[] operations;
    private boolean     valid;
    private boolean     negative;
    private String      equation;

    /**
     * Creates a new formula from a config string
     *
     * @param equation equation string
     */
    public Formula(String equation, CustomValue... defined)
    {
        int i = 0;
        for (CustomValue value : defined)
            value.setIndex(i++);

        negative = false;

        // Empty formulas
        if (equation == null || equation.length() == 0)
        {
            invalidate(defined);
            return;
        }

        // Clear out unused tokens
        equation = equation.replaceAll("[ '\"]", "");
        this.equation = equation;

        // Parse the formula
        ArrayList<IValue> vals = new ArrayList<IValue>();
        ArrayList<IOperator> ops = new ArrayList<IOperator>();
        int parens = 0, l = equation.length(), valStart = 0, lastOp = -1;
        Class<? extends IValue> func = null;
        for (i = 0; i < l; i++)
        {
            char c = equation.charAt(i);

            // Open parenthesis
            if (c == '(')
            {
                if (parens == 0)
                {
                    if (valStart != i)
                    {
                        String val = equation.substring(valStart, i);
                        if (FUNCS.containsKey(val))
                            func = FUNCS.get(val);
                        else
                        {
                            vals.add(makeVal(val, defined));
                            ops.add(OPS.get('*'));
                        }
                    }
                    valStart = i + 1;
                    lastOp = i;
                }
                parens++;
            }

            // Close parenthesis
            else if (c == ')')
            {
                parens--;
                if (parens == 0)
                {
                    if (func == null)
                        vals.add(makeVal(new Formula(equation.substring(valStart, i), defined)));
                    else
                    {
                        try
                        {
                            vals.add(
                                func.getConstructor(IValue.class).newInstance(
                                    makeVal(new Formula(equation.substring(valStart, i), defined))
                                )
                            );
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                            invalidate(defined);
                            return;
                        }
                    }
                    valStart = i + 1;
                }
            }

            // Operators
            else if (parens == 0 && OPS.containsKey(c))
            {
                if (c == '-' && lastOp == i - 1)
                {
                    negative = !negative;
                    valStart++;
                    lastOp++;
                }
                else
                {
                    if (valStart != i)
                    {
                        vals.add(makeVal(equation.substring(valStart, i), defined));
                    }
                    ops.add(OPS.get(c));
                    lastOp = i;
                    valStart = i + 1;
                }
            }
        }

        // End any lingering values
        if (valStart != l)
        {
            vals.add(makeVal(equation.substring(valStart, equation.length()), defined));
        }

        negative = false;

        // Convert to arrays
        values = vals.toArray(new IValue[vals.size()]);
        operations = ops.toArray(new IOperator[ops.size()]);

        if (!validate())
            invalidate(defined);
        else
            valid = true;
    }

    /**
     * Invalidates the equation
     *
     * @param defined defined inputs
     */
    private void invalidate(CustomValue... defined)
    {
        Logger.invalid("Invalid equation: " + equation);
        equation = defined[0].getToken();
        values = new IValue[] { defined[0] };
        operations = new IOperator[0];
        valid = false;
    }

    private IValue makeVal(String val, CustomValue... defined)
    {
        if (negative)
        {
            negative = false;
            return new Formula(val, defined).negate();
        }
        else
        {
            for (CustomValue value : defined)
            {
                if (value.getToken().equals(val))
                    return value;
            }
            return new ValueNum(val);
        }
    }

    private IValue makeVal(Formula val)
    {
        if (negative && val.operations.length == 0 && val.values[0] instanceof ValueNum)
            return new ValueNum(-val.values[0].compute());
        else if (negative)
        {
            val.negate();
            negative = false;
            return val;
        }
        else if (val.operations.length == 0)
            return val.values[0];
        else
            return val;
    }

    /**
     * Whether or not the equation was valid when parsed.
     *
     * @return whether or not the equation was valid
     */
    public boolean isValid()
    {
        return valid;
    }

    /**
     * Negates the output of the formula for future computations
     *
     * @return the negated Formula
     */
    public Formula negate()
    {
        negative = !negative;
        return this;
    }

    /**
     * Tries to validate the equation, making sure values are all valid values
     *
     * @return true if valid, false otherwise
     */
    private boolean validate()
    {
        // Operators between values means there should
        // always be one more value than operators
        if (values.length != operations.length + 1)
        {
            return false;
        }

        // Ensure valid sub equations
        for (IValue value : values)
            if (value instanceof Formula && !((Formula) value).validate())
                return false;

        // Nothing went wrong
        return true;
    }

    /**
     * Calculates the formula using the given base value and attribute.
     * If the formula is invalid, this returns the value.
     *
     * @param input the input data
     *
     * @return computed value
     */
    public double compute(double... input)
    {
        double result = values[0].compute(input);
        int i;
        for (i = 1; i < values.length; i++)
        {
            double val = values[i].compute(input);
            result = operations[i - 1].compute(result, val);
        }

        if (negative) result = -result;
        return result;
    }

    /**
     * Returns the equation string for toString
     *
     * @return the equation string
     */
    @Override
    public String toString()
    {
        return equation;
    }
}
