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

import org.bukkit.entity.LivingEntity;

public class ParticleEffect
{
    private FormulaSettings  stepFormula;
    private FormulaSettings  animation;
    private ParticleSettings particle;
    private LivingEntity     target;

    private double cos      = 1;
    private double sin      = 0;
    private int    step     = 0;
    private int    duration = 0;

    public ParticleEffect(FormulaSettings stepFormula, FormulaSettings animation, ParticleSettings particle, LivingEntity target)
    {
        this.stepFormula = stepFormula;
        this.animation = animation;
        this.particle = particle;
        this.target = target;
    }

    public void setDuration(int duration)
    {
        this.duration = Math.max(this.duration, duration);
    }

    public boolean step()
    {
        this.duration--;


        return this.duration <= 0;
    }
}
