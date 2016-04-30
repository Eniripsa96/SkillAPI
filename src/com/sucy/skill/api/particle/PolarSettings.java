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
import com.sucy.skill.api.particle.direction.XYHandler;
import com.sucy.skill.api.particle.direction.XZHandler;
import com.sucy.skill.api.particle.direction.YZHandler;
import com.sucy.skill.data.Point2D;
import com.sucy.skill.data.Point3D;
import com.sucy.skill.data.formula.Formula;
import com.sucy.skill.data.formula.IValue;
import com.sucy.skill.data.formula.value.CustomValue;
import org.bukkit.Location;

import java.awt.*;

/**
 * Settings for a particle effect
 */
public class PolarSettings
{
    private Point3D[] points;

    private final DirectionHandler handler;

    private IValue formula;
    private int    copies;
    private int    steps;
    private double domain;
    private double cos, sin, copyCos, copySin;
    private double xOff, yOff, zOff;

    private boolean calculated;

    /**
     * Sets up a formula for particle effects
     *
     * @param formula   formula to use
     * @param steps     the number of steps to apply
     * @param handler   the direction for the formula to apply
     */
    public PolarSettings(IValue formula, int steps, DirectionHandler handler)
    {
        this(formula, steps, handler, 1, 1);
    }

    /**
     * Sets up a formula for particle effects
     *
     * @param formula   formula to use
     * @param steps     the number of steps to apply
     * @param handler   the direction for the formula to apply
     * @param copies    number of copies to use rotated about the origin
     * @param domain    domain of the input values
     */
    public PolarSettings(IValue formula, int steps, DirectionHandler handler, int copies, double domain)
    {
        this.formula = formula;
        this.copies = Math.max(1, copies);
        this.domain = domain;
        this.handler = handler;
        this.steps = steps;

        double angle = Math.PI * 2 / steps;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        copyCos = Math.cos(Math.PI * 2 / copies);
        copySin = Math.sin(Math.PI * 2 / copies);

        for (int i = 0; i < points.length; i++)
            points[i] = new Point3D();
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
                new CustomValue("t", 0),
                new CustomValue("a", 1),
                new CustomValue("c", 2),
                new CustomValue("s", 3)
            ),
            settings.getInt("steps"),
            getDirection(settings.getString("direction")),
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
     * Sets the offset for the effect
     *
     * @param x X-Axis offset
     * @param y Y-Axis offset
     * @param z Z-Axis offset
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
    public Point3D[] getPoints()
    {
        if (!calculated)
            calculate();
        return points;
    }

    /**
     * Performs the calculations for the points making up the shape
     */
    private void calculate()
    {
        points = new Point3D[copies * steps];
        calculated = true;
        Point2D rot = new Point2D(1, 0);
        Point2D copy = new Point2D();
        for (int i = 0; i < points.length; i += copies)
        {
            double t = domain * i / steps;
            double theta = t * Math.PI * 2;
            double r = formula.compute(t, theta, rot.x, rot.y);

            copy.x = rot.x;
            copy.y = rot.y;
            for (int j = 0; j < copies; j++)
            {
                Point3D p = points[i + j];
                handler.apply(p, r * copy.x, r * copy.y);
                p.x += xOff;
                p.y += yOff;
                p.z += zOff;
                copy.rotate(copyCos, copySin);
            }

            rot.rotate(cos, sin);
        }
    }

    /**
     * Gets a direction from a config key
     *
     * @param key config key
     * @return direction
     */
    private static DirectionHandler getDirection(String key)
    {
        if (key == null)
            return XZHandler.instance;
        else if (key.equals("XY"))
            return XYHandler.instance;
        else if (key.equals("YZ"))
            return YZHandler.instance;
        else
            return XZHandler.instance;
    }
}
