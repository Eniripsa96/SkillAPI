/**
 * SkillAPI
 * com.sucy.skill.api.particle.ParticleEffect
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

import com.sucy.skill.api.particle.direction.DirectionHandler;
import com.sucy.skill.data.Point2D;
import com.sucy.skill.data.Point3D;
import com.sucy.skill.data.formula.Formula;
import com.sucy.skill.data.formula.IValue;
import com.sucy.skill.data.formula.value.CustomValue;
import org.bukkit.Location;

/**
 * A particle effect that can be played
 */
public class ParticleEffect
{
    private Object[] packets;

    private PolarSettings    shape;
    private PolarSettings    animation;
    private ParticleSettings particle;
    private IValue           size;
    private IValue           animSize;

    private DirectionHandler shapeDir;
    private DirectionHandler animDir;

    private String name;

    private int interval;
    private int view;

    /**
     * @param shape     shape formula details
     * @param animation motion animation formula details
     * @param particle  settings of the particle to use
     * @param shapeDir  shape orientation
     * @param animDir   animation orientation
     * @param size      formula string for shape size
     * @param animSize  formula string for animation size
     * @param interval  time between animation frames in ticks
     * @param viewRange range in blocks players can see the effect from
     */
    public ParticleEffect(
        String name,
        PolarSettings shape,
        PolarSettings animation,
        ParticleSettings particle,
        DirectionHandler shapeDir,
        DirectionHandler animDir,
        String size,
        String animSize,
        int interval,
        int viewRange)
    {
        this.name = name;
        this.shape = shape;
        this.animation = animation;
        this.size = new Formula(
            size,
            new CustomValue("t"),
            new CustomValue("p"),
            new CustomValue("c"),
            new CustomValue("s"),
            new CustomValue("x"),
            new CustomValue("y"),
            new CustomValue("z"),
            new CustomValue("v")
        );
        this.particle = particle;
        this.shapeDir = shapeDir;
        this.animDir = animDir;
        this.animSize = new Formula(
            animSize,
            new CustomValue("t"),
            new CustomValue("p"),
            new CustomValue("c"),
            new CustomValue("s"),
            new CustomValue("x"),
            new CustomValue("y"),
            new CustomValue("z"),
            new CustomValue("v")
        );
        this.interval = interval;
        this.view = viewRange;

        int points = shape.getPoints(shapeDir).length;
        animation.getPoints(animDir);
        packets = new Object[animation.getCopies() * points];
    }

    /**
     * @return name of the effect
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return time between each frame in ticks
     */
    public int getInterval()
    {
        return interval;
    }

    /**
     * @return number of animation frames
     */
    public int getFrames()
    {
        return animation.getSteps();
    }

    /**
     * Plays the effect
     *
     * @param loc   location to play at
     * @param frame frame of the animation to play
     * @param level level of the effect
     */
    public void play(Location loc, int frame, int level)
    {
        frame = frame % animation.getSteps();
        try
        {
            int next = (frame + 1) * animation.getCopies();
            Point3D[] animPoints = animation.getPoints(animDir);
            Point3D[] shapePoints = shape.getPoints(shapeDir);
            Point2D[] trig = animation.getTrig(frame);

            Point2D cs = trig[0];
            double t = animation.getT(frame);
            double p = (double) frame / animation.getSteps();

            int j = 0, k = 0;
            for (int i = frame * animation.getCopies(); i < next; i++)
            {
                Point3D p1 = animPoints[i];
                double animSize = this.animSize.compute(t, p, cs.x, cs.y, p1.x, p1.y, p1.z, level);
                for (Point3D p2 : shapePoints)
                {
                    double size = this.size.compute(t, p, cs.x, cs.y, p2.x, p2.y, p2.z, level);
                    packets[k++] = particle.instance(
                        p1.x * animSize + animDir.rotateX(p2, trig[j]) * size + loc.getX(),
                        p1.y * animSize + animDir.rotateY(p2, trig[j]) * size + loc.getY(),
                        p1.z * animSize + animDir.rotateZ(p2, trig[j]) * size + loc.getZ()
                    );
                }
                j++;
            }
            Particle.send(loc, packets, view);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
