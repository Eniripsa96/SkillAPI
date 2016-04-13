/**
 * SkillAPI
 * com.sucy.skill.cast.CastIndicatorParticle
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

import com.rit.sucy.config.parse.DataSection;
import com.sucy.skill.api.util.Particle;
import org.bukkit.Location;

public class CastIndicatorParticle
{
    private String type;
    private float dx, dy, dz;
    private float speed;

    public CastIndicatorParticle(String type, float dx, float dy, float dz, float speed)
    {
        this.type = type;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.speed = speed;
    }

    public CastIndicatorParticle(DataSection data)
    {
        type = data.getString("type");
        dx = data.getFloat("dx");
        dy = data.getFloat("dy");
        dz = data.getFloat("dz");
        speed = data.getFloat("speed");
    }

    public Object instance(Location loc)
        throws Exception
    {
        return Particle.make(type, loc, dx, dy, dz, speed);
    }

    public Object instance(double x, double y, double z)
        throws Exception
    {
        return Particle.make(type, x, y, z, dx, dy, dz, speed);
    }
}
