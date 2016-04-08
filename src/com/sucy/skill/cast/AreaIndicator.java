/**
 * SkillAPI
 * com.sucy.skill.cast.AreaIndicator
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

import com.sucy.skill.api.util.Particle;
import org.bukkit.Location;

import java.util.List;

public class AreaIndicator implements IIndicator
{
    private double radius;
    private Location loc;

    public AreaIndicator(Location loc, double radius)
    {
        this.loc = loc;
        this.radius = radius;
    }

    @Override
    public void makePackets(List<Object> packets, CastIndicatorParticle particle, int step)
        throws Exception
    {
        // Data for particles and angle between them
        int particles = (int)(IndicatorSettings.density * radius * 2 * Math.PI);
        double angle = Math.PI / (2 * particles);
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        // Offset angle for animation
        double startAngle = step * IndicatorSettings.animation / (20 * radius);
        double ii = Math.sin(startAngle) * radius;
        double jj = Math.cos(startAngle) * radius;

        // Grab base location numbers
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        // Make the packets
        for (int i = 0; i < particles; i++)
        {
            packets.add(particle.instance(x + ii, y, z + jj));

            double temp = ii * cos - jj * sin;
            jj = ii * sin + jj * cos;
            ii = temp;
        }
    }
}
