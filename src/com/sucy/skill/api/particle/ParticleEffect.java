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

import com.sucy.skill.api.particle.target.EffectTarget;

public class ParticleEffect
{
    private PolarSettings    shape;
    private PolarSettings    animation;
    private ParticleSettings particle;
    private EffectTarget     target;

    private int frame;

    /**
     * @param shape     shape formula details
     * @param animation animation formula details
     * @param particle  settings of the particle to use
     * @param target    target to follow
     */
    public ParticleEffect(PolarSettings shape, PolarSettings animation, ParticleSettings particle, EffectTarget target)
    {
        this.shape = shape;
        this.animation = animation;
        this.particle = particle;
        this.target = target;
        this.frame = 0;
    }
}
