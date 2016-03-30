/**
 * SkillAPI
 * com.sucy.skill.data.Formula
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
package com.sucy.skill.data;

import com.sucy.skill.api.util.NumberParser;
import com.sucy.skill.log.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a basic math equation read from left to right, ignoring
 * order of operations. Currently this only supports addition, subtraction,
 * multiplication, and division.
 */
public class Formula
{
    private static final List<Character> OPS = Arrays.asList('+', '-', '*', '/', '^');

    private Object[]    values;
    private Character[] operations;
    private boolean     valid;
    private boolean     negative;
    private String      equation;

    /**
     * Creates a new formula from a config string
     *
     * @param equation equation string
     */
    public Formula(String equation)
    {
        negative = false;

        // Empty formulas
        if (equation == null || equation.length() == 0)
        {
            values = new Object[] { 'v' };
            operations = new Character[0];
            this.equation = "v";
            return;
        }

        // Clear out unused tokens
        equation = equation.replaceAll("[ '\"]", "");
        this.equation = equation;

        // Parse the formula
        ArrayList<Object> vals = new ArrayList<Object>();
        ArrayList<Character> ops = new ArrayList<Character>();
        int parens = 0, l = equation.length(), valStart = 0, lastOp = -1;
        for (int i = 0; i < l; i++)
        {
            char c = equation.charAt(i);

            // Open parenthesis
            if (c == '(')
            {
                if (parens == 0)
                {
                    if (valStart != i)
                    {
                        vals.add(makeVal(equation.substring(valStart, i)));
                        ops.add('*');
                    }
                    valStart = i + 1;
                }
                parens++;
            }

            // Close parenthesis
            else if (c == ')')
            {
                parens--;
                if (parens == 0)
                {
                    vals.add(makeVal(new Formula(equation.substring(valStart, i))));
                    valStart = i + 1;
                }
            }

            // Operators
            else if (parens == 0 && OPS.contains(c))
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
                        vals.add(makeVal(equation.substring(valStart, i)));
                    }
                    ops.add(c);
                    lastOp = i;
                    valStart = i + 1;
                }
            }
        }

        // End any lingering values
        if (valStart != l)
        {
            vals.add(makeVal(equation.substring(valStart, equation.length())));
        }

        negative = false;

        // Convert to arrays
        values = vals.toArray();
        operations = ops.toArray(new Character[ops.size()]);

        if (!validate())
        {
            Logger.invalid("Invalid equation: " + equation);
            values = new Object[] { 'v' };
            operations = new Character[0];
            valid = false;
        }
        else
        {
            valid = true;
        }
    }

    private Object makeVal(String val)
    {
        if (negative)
        {
            negative = false;
            return new Formula(val).negate();
        }
        else return val;
    }

    private Object makeVal(Formula val)
    {
        if (negative)
        {
            val.negate();
            negative = false;
        }
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

        // Parse each value to make sure it's a valid equation
        for (int i = 0; i < values.length; i++)
        {
            // Sub-equations
            if (values[i] instanceof Formula)
            {
                if (!((Formula) values[i]).validate())
                    return false;
            }

            // Numbers
            else if (!values[i].toString().equals("v") && !values[i].toString().equals("a"))
            {
                // Parse the number
                try
                {
                    values[i] = NumberParser.parseDouble(values[i].toString());
                }
                catch (NumberFormatException ex)
                {
                    return false;
                }
            }
        }

        // Nothing went wrong
        return true;
    }

    /**
     * Calculates the formula using the given base value and attribute.
     * If the formula is invalid, this returns the value.
     *
     * @param value base value
     * @param attr  attribute
     *
     * @return computed value
     */
    public double compute(double value, double attr)
    {
        double result = getValue(0, value, attr);
        int i;
        for (i = 1; i < values.length; i++)
        {
            double val = getValue(i, value, attr);
            switch (operations[i - 1])
            {
                case '+':
                    result += val;
                    break;
                case '-':
                    result -= val;
                    break;
                case '*':
                    result *= val;
                    break;
                case '/':
                    result /= val;
                    break;
                case '^':
                    result = Math.pow(result, val);
                    break;
            }
        }

        if (negative) result = -result;
        return result;
    }

    private double getValue(int index, double value, double attr)
    {
        if (values[index] instanceof Formula) return ((Formula) values[index]).compute(value, attr);
        if (values[index].toString().equals("v")) return value;
        if (values[index].toString().equals("a")) return attr;
        return (Double) values[index];
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
