/**
 * SkillAPI
 * com.sucy.skill.cast.ConeIndicator
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

public class ConeIndicator implements IIndicator
{
    private double x, y, z;
    private double fx, fz;
    private double arc, radius;
    private double sin, cos;
    private double rSin, rCos;
    private double offset;
    private double yaw;
    private double angleOffset;

    /**
     * @param arc    angle of the cone in degrees
     * @param radius radius of the cone
     */
    public ConeIndicator(double arc, double radius)
    {
        if (radius == 0)
            throw new IllegalArgumentException("Invalid radius - cannot be 0");

        this.arc = arc * Math.PI / 180;
        this.radius = Math.abs(radius);
        double perimeter = radius * arc + 2 * radius;
        int particles = (int) (IndicatorSettings.density * perimeter);

        offset = perimeter / particles;
        angleOffset = offset / radius;
        sin = Math.sin(angleOffset);
        cos = Math.cos(angleOffset);
        rSin = Math.sin(arc / 2);
        rCos = Math.cos(arc / 2);
    }

    /**
     * Sets the direction of the projectile
     *
     * @param yaw the directional yaw
     */
    public void setDirection(float yaw)
    {
        yaw *= -Math.PI / 180;
        fx = Math.sin(yaw);
        fz = Math.cos(yaw);
        this.yaw = yaw + Math.PI / 4;
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
        double base = (IndicatorSettings.animation * 0.05 * step) % offset;

        // Offset angle for animation
        double startAngle = ((radius - base) % offset) / radius;
        double ii = Math.sin(startAngle + yaw) * radius;
        double jj = Math.cos(startAngle + yaw) * radius;

        // Packets along the edges
        make(packets, particle, base, fx * rCos + fz * rSin, fz * rCos - fx * rSin);
        make(packets, particle, offset - base, fx * rCos - fz * rSin, fx * rSin + fz * rCos);

        // Packets around the curve
        while (startAngle < arc)
        {
            packets.add(particle.instance(x + ii, y, z + jj));

            double temp = ii * cos - jj * sin;
            jj = ii * sin + jj * cos;
            ii = temp;

            startAngle += angleOffset;
        }
    }

    private void make(List<Object> packets, ParticleSettings particle, double pos, double rfx, double rfz)
        throws Exception
    {
        while (pos <= radius)
        {
            packets.add(particle.instance(x + pos * rfx, y, z + pos * rfz));
            pos += offset;
        }
    }
}
