/**
 * SkillAPI
 * com.sucy.skill.api.particle.target.EntityTarget
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
import org.bukkit.entity.Entity;

import java.util.Objects;

/**
 * Causes an effect to follow the target entity
 */
public class EntityTarget implements EffectTarget
{
    private Entity   entity;
    private Location loc;

    /**
     * @param target entity to follow
     */
    public EntityTarget(Entity target)
    {
        this.entity = target;
        this.loc = target.getLocation();
    }

    /**
     * Gets the location to center the effect around
     *
     * @return effect location
     */
    public Location getLocation()
    {
        return entity.getLocation(loc);
    }

    public Entity getEntity() {
        return entity;
    }

    /**
     * @return tue if target is still valid, false otherwise
     */
    public boolean isValid()
    {
        return entity.isValid() && !entity.isDead();
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity, loc);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EntityTarget)) return false;
        final EntityTarget target = (EntityTarget)o;
        return target.entity == entity && target.loc.equals(loc);
    }
}
