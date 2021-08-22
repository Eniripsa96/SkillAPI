/**
 * SkillAPI
 * com.sucy.skill.api.particle.direction.YZHandler
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
 * Handles the YZ direction
 */
public class YZHandler implements DirectionHandler
{
    public static YZHandler instance = new YZHandler();

    /**
     * Applies the two results from the polar calculation to a point
     *
     * @param point the point to apply it to
     * @param n1    first value
     * @param n2    second value
     */
    public void apply(Point3D point, double n1, double n2)
    {
        point.x = 0;
        point.y = n1;
        point.z = n2;
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
        return p.x;
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
        return p.y * trig.x - p.z * trig.y;
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
        return p.y * trig.y + p.z * trig.x;
    }
}
