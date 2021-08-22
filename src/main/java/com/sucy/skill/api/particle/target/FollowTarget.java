/**
 * SkillAPI
 * com.sucy.skill.api.particle.target.FollowTarget
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
package com.sucy.skill.api.particle.target;

import org.bukkit.Location;

/**
 * Tracks a followable target to play an effect around
 */
public class FollowTarget implements EffectTarget
{
    private Followable entity;

    /**
     * @param target object to follow
     */
    public FollowTarget(Followable target)
    {
        this.entity = target;
    }

    /**
     * Gets the location to center the effect around
     *
     * @return effect location
     */
    public Location getLocation()
    {
        return entity.getLocation();
    }

    /**
     * @return tue if target is still valid, false otherwise
     */
    public boolean isValid()
    {
        return entity.isValid();
    }

    @Override
    public int hashCode() {
        return entity.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof FollowTarget) && (((FollowTarget) o).entity.equals(entity));
    }
}
