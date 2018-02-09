/**
 * SkillAPI
 * com.sucy.skill.api.util.NumberParser
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
package com.sucy.skill.api.util;

/**
 * Handles number parsing for various locales
 */
public class NumberParser
{
    /**
     * Parses an integer value from a string
     *
     * @param value string to parse
     *
     * @return integer value
     */
    public static int parseInt(String value)
    {
        int n = 0;
        int val;
        boolean negative = false;

        int i = 0;
        char c = value.charAt(i);

        // Negative sign in front
        if (c == '-')
        {
            negative = true;
            i++;
        }

        // Positive sign in front
        else if (c == '+')
        {
            i++;
        }

        // Read in digits
        for (; i < value.length(); i++)
        {
            c = value.charAt(i);
            val = (int) c - 48;
            if (val < 0 || val > 9)
                error(value);
            n *= 10;
            n += val;
        }
        return negative ? -n : n;
    }

    /**
     * Parses a double value from a string
     *
     * @param value string to parse
     *
     * @return double value
     */
    public static double parseDouble(String value)
    {
        double n = 0;
        double d = 0.1;
        int val;
        boolean decimal = false;
        boolean negative = false;
        int i = 0;
        char c = value.charAt(i);

        // Negative sign in front
        if (c == '-')
        {
            negative = true;
            i++;
        }

        // Positive sign in front
        else if (c == '+')
        {
            i++;
        }

        for (; i < value.length(); i++)
        {
            c = value.charAt(i);
            switch (c)
            {
                // Start the decimal portion
                case '.':
                case ',':
                    if (decimal)
                        error(value);
                    decimal = true;
                    break;

                // Add digits
                default:
                    val = (int) c - 48;
                    if (val < 0 || val > 9)
                        error(value);
                    if (decimal)
                    {
                        n += d * val;
                        d *= 0.1;
                    }
                    else
                    {
                        n *= 10;
                        n += val;
                    }
                    break;
            }
        }
        return negative ? -n : n;
    }

    /**
     * Throws an error when parsing numbers
     *
     * @param value string being parsed
     */
    private static void error(String value)
    {
        throw new NumberFormatException("Invalid Number: " + value);
    }
}
