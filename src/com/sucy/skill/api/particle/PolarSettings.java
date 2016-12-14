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
import com.sucy.skill.api.particle.direction.DirectionHandler;
import com.sucy.skill.data.Point2D;
import com.sucy.skill.data.Point3D;
import com.sucy.skill.data.formula.Formula;
import com.sucy.skill.data.formula.IValue;
import com.sucy.skill.data.formula.value.CustomValue;

import java.util.HashMap;

/**
 * Settings for a particle effect
 */
public class PolarSettings
{
    private HashMap<DirectionHandler, Point3D[]> points = new HashMap<DirectionHandler, Point3D[]>();

    private Point2D[][] trig;

    private IValue formula;
    private int    copies;
    private int    steps;
    private double domain;
    private double xOff, yOff, zOff;

    /**
     * Sets up a formula for particle effects
     *
     * @param formula formula to use
     * @param steps   the number of steps to apply
     */
    public PolarSettings(IValue formula, int steps)
    {
        this(formula, steps, 1, 1);
    }

    /**
     * Sets up a formula for particle effects
     *
     * @param formula formula to use
     * @param steps   the number of steps to apply
     * @param copies  number of copies to use rotated about the origin
     * @param domain  domain of the input values
     */
    public PolarSettings(IValue formula, int steps, int copies, double domain)
    {
        this.formula = formula;
        this.copies = Math.max(1, copies);
        this.domain = Math.max(0, domain / steps);
        this.steps = Math.max(1, steps);

        double angle = Math.PI * 2 * this.domain;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double copyCos = Math.cos(Math.PI * 2 / copies);
        double copySin = Math.sin(Math.PI * 2 / copies);

        this.trig = new Point2D[steps][copies];
        Point2D rot = new Point2D(1, 0);
        Point2D copy = new Point2D();
        for (int i = 0; i < steps; i++)
        {
            copy.x = rot.x;
            copy.y = rot.y;
            for (int j = 0; j < copies; j++)
            {
                trig[i][j] = new Point2D(copy.x, copy.y);
                copy.rotate(copyCos, copySin);
            }
            rot.rotate(cos, sin);
        }
    }

    /**
     * Loads settings from config data
     *
     * @param settings settings to load from
     */
    public PolarSettings(DataSection settings)
    {
        this(
            new Formula(
                settings.getString("formula"),
                new CustomValue("t"),
                new CustomValue("p"),
                new CustomValue("c"),
                new CustomValue("s")
            ),
            settings.getInt("steps"),
            settings.getInt("copies"),
            settings.getDouble("domain")
        );
        setOffset(
            settings.getDouble("x"),
            settings.getDouble("y"),
            settings.getDouble("z")
        );
    }

    /**
     * Fetches the trig values for the animation
     *
     * @param step animation step
     *
     * @return trig values
     */
    public Point2D[] getTrig(int step)
    {
        return trig[step];
    }

    /**
     * @return number of copies
     */
    public int getCopies()
    {
        return copies;
    }

    /**
     * @return number of steps in the formula
     */
    public int getSteps()
    {
        return steps;
    }

    /**
     * Sets the offset for the effect
     *
     * @param x X-Axis offset
     * @param y Y-Axis offset
     * @param z Z-Axis offset
     *
     * @return this
     */
    public PolarSettings setOffset(double x, double y, double z)
    {
        this.xOff = x;
        this.yOff = y;
        this.zOff = z;
        return this;
    }

    /**
     * Grabs the base points that make up the shape of the effect
     *
     * @return shape points
     */
    public Point3D[] getPoints(DirectionHandler direction)
    {
        if (!points.containsKey(direction))
            calculate(direction);
        return points.get(direction);
    }

    /**
     * Performs the calculations for the points making up the shape
     */
    private void calculate(DirectionHandler direction)
    {
        Point3D[] points = new Point3D[copies * steps];
        this.points.put(direction, points);
        int k = 0;
        for (int i = 0; i < steps; i++)
        {
            Point2D rot = trig[i][0];
            double t = domain * i;
            double r = formula.compute(t, (double) i / steps, rot.x, rot.y);
            for (int j = 0; j < copies; j++)
            {
                Point2D copy = trig[i][j];
                Point3D p = new Point3D();
                direction.apply(p, r * copy.x, r * copy.y);
                p.x += xOff;
                p.y += yOff;
                p.z += zOff;
                points[k++] = p;
            }
        }
    }

    /**
     * Gets the time step value for the animation step
     *
     * @param step animation step
     *
     * @return time step
     */
    public double getT(int step)
    {
        return domain * step;
    }
}
