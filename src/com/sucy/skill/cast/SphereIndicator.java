/**
 * SkillAPI
 * com.sucy.skill.cast.SphereIndicator
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
package com.sucy.skill.cast;

import com.sucy.skill.api.particle.ParticleSettings;
import org.bukkit.Location;

import java.util.List;

/**
 * A fancier sphere indicator
 */
public class SphereIndicator implements IIndicator
{
    private static final double COS_45 = Math.cos(Math.PI / 4);

    private double x, y, z;
    private double radius;
    private double sin, cos;
    private double angleStep;
    private int    particles;

    /**
     * @param radius radius of the circle
     */
    public SphereIndicator(double radius)
    {
        if (radius == 0)
            throw new IllegalArgumentException("Invalid radius - cannot be 0");

        this.radius = Math.abs(radius);
        particles = (int) (IndicatorSettings.density * radius * 2 * Math.PI);
        angleStep = IndicatorSettings.animation * IndicatorSettings.interval / (20 * this.radius);

        double angle = Math.PI * 2 / particles;
        sin = Math.sin(angle);
        cos = Math.cos(angle);
    }

    /**
     * Updates the position of the indicator to be centered
     * at the given coordinates
     *
     * @param loc location to move to
     */
    @Override
    public void moveTo(Location loc)
    {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
    }

    /**
     * Updates the position of the indicator to be centered
     * at the given coordinates
     *
     * @param x X-axis coordinate
     * @param y Y-axis coordinate
     * @param z Z-axis coordinate
     */
    @Override
    public void moveTo(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates the packets for the indicator, adding them to the list
     *
     * @param packets  packet list to add to
     * @param particle particle type to use
     * @param step     animation step
     *
     * @throws Exception
     */
    @Override
    public void makePackets(List<Object> packets, ParticleSettings particle, int step)
        throws Exception
    {
        // Offset angle for animation
        double startAngle = step * angleStep;

        double urs = Math.sin(startAngle);
        double urc = Math.cos(startAngle);

        double rs = urs * radius;
        double rc = urc * radius;

        // Flat circle packets
        for (int i = 0; i < particles; i++)
        {
            packets.add(particle.instance(x + rs, y, z + rc));
            packets.add(particle.instance(x + rs * urc, y + rc, z + rs * urs));
            packets.add(particle.instance(x + (rc - rs * urs) * COS_45, y + rs * urc, z + (rc + rs * urs) * COS_45));

            double temp = rs * cos - rc * sin;
            rc = rs * sin + rc * cos;
            rs = temp;
        }
    }
}
