/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.ParticleAnimationMechanic
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
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
package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.util.ParticleHelper;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Plays a particle effect
 */
public class ParticleAnimationMechanic extends MechanicComponent
{
    private static final String FORWARD  = "forward";
    private static final String UPWARD   = "upward";
    private static final String RIGHT    = "right";
    private static final String STEPS    = "steps";
    private static final String FREQ     = "frequency";
    private static final String ANGLE    = "angle";
    private static final String START    = "start";
    private static final String DURATION = "duration";
    private static final String H_TRANS  = "h-translation";
    private static final String V_TRANS  = "v-translation";
    private static final String H_CYCLES = "h-cycles";
    private static final String V_CYCLES = "v-cycles";

    @Override
    public String getKey() {
        return "particle animation";
    }

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     *
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets)
    {
        if (targets.size() == 0)
        {
            return false;
        }

        final Settings copy = new Settings(settings);
        copy.set(ParticleHelper.PARTICLES_KEY, parseValues(caster, ParticleHelper.PARTICLES_KEY, level, 1), 0);
        copy.set(ParticleHelper.RADIUS_KEY, parseValues(caster, ParticleHelper.RADIUS_KEY, level, 0), 0);
        copy.set("level", level);
        new ParticleTask(caster, targets, level, copy);
        return targets.size() > 0;
    }

    private class ParticleTask extends BukkitRunnable
    {

        private List<LivingEntity> targets;
        private double[]           rots;
        private Vector             offset;
        private Vector             dir;

        private double forward;
        private double right;
        private double upward;

        private int    steps;
        private int    freq;
        private int    angle;
        private int    startAngle;
        private int    duration;
        private int    life;
        private int    hc;
        private int    vc;
        private int    hl;
        private int    vl;
        private double ht;
        private double vt;
        private double cos;
        private double sin;

        private Settings settings;

        ParticleTask(LivingEntity caster, List<LivingEntity> targets, int level, Settings settings)
        {
            this.targets = targets;
            this.settings = settings;

            this.forward = settings.getDouble(FORWARD, 0);
            this.upward = settings.getDouble(UPWARD, 0);
            this.right = settings.getDouble(RIGHT, 0);

            this.steps = settings.getInt(STEPS, 1);
            this.freq = (int) (settings.getDouble(FREQ, 1.0) * 20);
            this.angle = settings.getInt(ANGLE, 0);
            this.startAngle = settings.getInt(START, 0);
            this.duration = steps * (int) (20 * parseValues(caster, DURATION, level, 3.0));
            this.life = 0;
            this.ht = parseValues(caster, H_TRANS, level, 0);
            this.vt = parseValues(caster, V_TRANS, level, 0);
            this.hc = settings.getInt(H_CYCLES, 1);
            this.vc = settings.getInt(V_CYCLES, 1);
            this.hl = duration / hc;
            this.vl = duration / vc;

            this.cos = Math.cos(angle * Math.PI / (180 * duration));
            this.sin = Math.sin(angle * Math.PI / (180 * duration));

            rots = new double[targets.size() * 2];
            for (int i = 0; i < targets.size(); i++)
            {
                Vector dir = targets.get(i).getLocation().getDirection().setY(0).normalize();
                rots[i * 2] = dir.getX();
                rots[i * 2 + 1] = dir.getZ();
            }
            this.dir = new Vector(1, 0, 0);
            this.offset = new Vector(forward, upward, right);

            double sc = Math.cos(startAngle * Math.PI / 180);
            double ss = Math.sin(startAngle * Math.PI / 180);
            rotate(offset, sc, ss);
            rotate(dir, sc, ss);

            SkillAPI.schedule(this, 0, freq);
        }

        @Override
        public void run()
        {
            for (int i = 0; i < steps; i++)
            {
                // Play the effect
                int j = 0;
                for (LivingEntity target : targets)
                {
                    Location loc = target.getLocation();

                    rotate(offset, rots[j], rots[j + 1]);
                    loc.add(offset);
                    ParticleHelper.play(loc, settings);
                    loc.subtract(offset);
                    rotate(offset, rots[j++], -rots[j++]);
                }

                // Update the lifespan of the animation
                this.life++;

                // Apply transformations
                rotate(offset, cos, sin);
                rotate(dir, cos, sin);

                double dx = radAt(this.life) - radAt(this.life - 1);
                offset.setX(offset.getX() + dx * dir.getX());
                offset.setZ(offset.getZ() + dx * dir.getZ());
                offset.setY(upward + heightAt(this.life));
            }

            if (this.life >= this.duration)
            {
                cancel();
            }
        }

        private double heightAt(int step)
        {
            return vt * (vl - Math.abs(vl - step % (2 * vl))) / vl;
        }

        private double radAt(int step)
        {
            return ht * (hl - Math.abs(hl - step % (2 * hl))) / hl;
        }

        private void rotate(Vector vec, double cos, double sin)
        {
            double x = vec.getX() * cos - vec.getZ() * sin;
            vec.setZ(vec.getX() * sin + vec.getZ() * cos);
            vec.setX(x);
        }
    }
}
