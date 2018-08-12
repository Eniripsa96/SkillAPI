/**
 * SkillAPI
 * com.sucy.skill.cast.ArcIndicator
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
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Represents a preview indicator for showing the direction of projectiles to fire
 */
public class ProjectileIndicator implements IIndicator
{
    private double x, y, z;
    private double velX, velY, velZ;
    private double speed;
    private double gravity;
    private double tBase;

    /**
     * @param speed   speed of the projectile
     * @param gravity gravity of the projectile
     */
    public ProjectileIndicator(double speed, double gravity)
    {
        this.speed = speed;
        this.gravity = gravity;
        this.tBase = 3 / this.speed;
    }

    /**
     * Sets the direction of the projectile
     *
     * @param direction direction of the indicator
     */
    public void setDirection(Vector direction)
    {
        velX = direction.getX() * speed;
        velY = direction.getY() * speed;
        velZ = direction.getZ() * speed;
    }

    /**
     * Sets the direction of the projectile
     *
     * @param x X-Axis value
     * @param y Y-Axis value
     * @param z Z-Axis value
     */
    public void setDirection(double x, double y, double z)
    {
        velX = x * speed;
        velY = y * speed;
        velZ = z * speed;
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
        double px = x + velX * tBase;
        double py = y + velY * tBase - gravity * tBase * tBase;
        double pz = z + velZ * tBase;

        packets.add(particle.instance(px, py, pz));
    }
}
