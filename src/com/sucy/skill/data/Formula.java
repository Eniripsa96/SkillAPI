package com.sucy.skill.data;

import org.bukkit.Bukkit;

/**
 * Represents a basic math equation read from left to right, ignoring
 * order of operations. Currently this only supports addition, subtraction,
 * multiplication, and division.
 */
public class Formula
{
    private Object[] values;
    private String[] operations;

    /**
     * Creates a new formula from a config string
     *
     * @param equation equation string
     */
    public Formula(String equation) {
        equation = equation.replaceAll(" ", "");
        String[] split = equation.split("[+*/-]");
        operations = equation.split("[^+*/-]+");

        // When no values are provided, just return the original value
        if (split.length == 0) {
            values = new Object[] { 'v' };
            return;
        }
        else
        {
            values = new Object[split.length];
            System.arraycopy(split, 0, values, 0, split.length);
        }

        // Parse each value to make sure it's a valid equation
        for (int i = 0; i < values.length; i++)
        {
            // Numbers
            if (!values[i].toString().equals("v") && !values[i].toString().equals("a"))
            {
                // Negative notation
                int m = 1;
                if (values[i].toString().indexOf(0) == 'n')
                {
                    m = -1;
                    values[i] = values[i].toString().substring(1);
                }

                // Parse the number
                try
                {
                    values[i] = Double.parseDouble(values[i].toString());
                }
                catch (NumberFormatException ex)
                {
                    Bukkit.getLogger().severe("Invalid equation: " + equation);
                    values = new Object[] { 'v' };
                    return;
                }
            }
        }
    }

    /**
     * Calculates the formula using the given base value and attribute
     *
     * @param value base value
     * @param attr attribute
     * @return x - y
     */
    public double compute(double value, double attr)
    {
        double result = getValue(0, value, attr);
        int i;
        for (i = 1; i < values.length && i < operations.length; i++)
        {
            if      (operations[i].equals("+")) result += getValue(i, value, attr);
            else if (operations[i].equals("-")) result -= getValue(i, value, attr);
            else if (operations[i].equals("*")) result *= getValue(i, value, attr);
            else if (operations[i].equals("/")) result /= getValue(i, value, attr);
        }
        return result;
    }

    private double getValue(int index, double value, double attr)
    {
        if (values[index].toString().equals("v")) return value;
        if (values[index].toString().equals("a")) return attr;
        return (Double)values[index];
    }
}
