/**
 * SkillAPI
 * com.sucy.skill.api.particle.direction.XYHandler
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
package com.sucy.skill.api.particle.direction;

import com.sucy.skill.data.Point2D;
import com.sucy.skill.data.Point3D;

/**
 * Handles the XY direction
 */
public class XYHandler implements DirectionHandler
{
    public static XYHandler instance = new XYHandler();

    /**
     * Applies the two results from the polar calculation to a point
     *
     * @param point the point to apply it to
     * @param n1    first value
     * @param n2    second value
     */
    public void apply(Point3D point, double n1, double n2)
    {
        point.x = n1;
        point.y = n2;
        point.z = 0;
    }

    /**
     * Calculates the X value of a point after rotation
     *
     * @param p    original point
     * @param trig trig data
     *
     * @return rotation
     */
    public double rotateX(Point3D p, Point2D trig)
    {
        return p.x * trig.x - p.y * trig.y;
    }

    /**
     * Calculates the Y value of a point after rotation
     *
     * @param p    original point
     * @param trig trig data
     *
     * @return rotation
     */
    public double rotateY(Point3D p, Point2D trig)
    {
        return p.x * trig.y + p.y * trig.x;
    }

    /**
     * Calculates the Z value of a point after rotation
     *
     * @param p    original point
     * @param trig trig data
     *
     * @return rotation
     */
    public double rotateZ(Point3D p, Point2D trig)
    {
        return p.z;
    }
}
