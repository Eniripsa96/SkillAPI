/**
 * SkillAPI
 * com.sucy.skill.api.util.Nearby
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
package com.sucy.skill.api.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Fetches nearby entities by going through possible chunks
 * instead of all entities in a world
 */
public class Nearby
{
    /**
     * Gets entities nearby a location using a given radius
     *
     * @param loc    location centered around
     * @param radius radius to get within
     *
     * @return nearby entities
     */
    public static List<Entity> getNearby(Location loc, double radius)
    {
        List<Entity> result = new ArrayList<Entity>();

        int minX = (int) (loc.getX() - radius) >> 4;
        int maxX = (int) (loc.getX() + radius) >> 4;
        int minZ = (int) (loc.getZ() - radius) >> 4;
        int maxZ = (int) (loc.getZ() + radius) >> 4;

        radius *= radius;

        for (int i = minX; i <= maxX; i++)
            for (int j = minZ; j <= maxZ; j++)
                for (Entity entity : loc.getWorld().getChunkAt(i, j).getEntities())
                    if (entity.getLocation().distanceSquared(loc) < radius)
                        result.add(entity);

        return result;
    }

    /**
     * Fetches entities nearby a location using a given radius
     *
     * @param loc    location centered around
     * @param radius radius to get within
     *
     * @return nearby entities
     */
    public static List<LivingEntity> getLivingNearby(Location loc, double radius)
    {
        List<LivingEntity> result = new ArrayList<LivingEntity>();

        int minX = (int) (loc.getX() - radius) >> 4;
        int maxX = (int) (loc.getX() + radius) >> 4;
        int minZ = (int) (loc.getZ() - radius) >> 4;
        int maxZ = (int) (loc.getZ() + radius) >> 4;

        radius *= radius;

        for (int i = minX; i <= maxX; i++)
            for (int j = minZ; j <= maxZ; j++)
                for (Entity entity : loc.getWorld().getChunkAt(i, j).getEntities())
                    if (entity instanceof LivingEntity && entity.getWorld() == loc.getWorld() && entity.getLocation().distanceSquared(loc) < radius)
                        result.add((LivingEntity) entity);

        return result;
    }

    /**
     * Gets entities nearby a location using a given radius
     *
     * @param entity entity to get nearby ones for
     * @param radius radius to get within
     *
     * @return nearby entities
     */
    public static List<Entity> getNearby(Entity entity, double radius)
    {
        return getNearby(entity.getLocation(), radius);
    }

    /**
     * Fetches entities nearby a location using a given radius
     *
     * @param entity entity to get nearby ones for
     * @param radius radius to get within
     *
     * @return nearby entities
     */
    public static List<LivingEntity> getLivingNearby(Entity entity, double radius)
    {
        return getLivingNearby(entity.getLocation(), radius);
    }

    public static List<Entity> getNearbyBox(Location loc, double radius)
    {
        List<Entity> result = new ArrayList<Entity>();

        int minX = (int) (loc.getX() - radius) >> 4;
        int maxX = (int) (loc.getX() + radius) >> 4;
        int minZ = (int) (loc.getZ() - radius) >> 4;
        int maxZ = (int) (loc.getZ() + radius) >> 4;

        for (int i = minX; i <= maxX; i++)
            for (int j = minZ; j <= maxZ; j++)
                for (Entity entity : loc.getWorld().getChunkAt(i, j).getEntities())
                    if (boxDistance(entity.getLocation(), loc) < radius)
                        result.add(entity);

        return result;
    }

    public static List<LivingEntity> getLivingNearbyBox(Location loc, double radius)
    {
        List<LivingEntity> result = new ArrayList<LivingEntity>();

        int minX = (int) (loc.getX() - radius) >> 4;
        int maxX = (int) (loc.getX() + radius) >> 4;
        int minZ = (int) (loc.getZ() - radius) >> 4;
        int maxZ = (int) (loc.getZ() + radius) >> 4;

        for (int i = minX; i <= maxX; i++)
            for (int j = minZ; j <= maxZ; j++)
                for (Entity entity : loc.getWorld().getChunkAt(i, j).getEntities())
                    if (entity instanceof LivingEntity && boxDistance(entity.getLocation(), loc) < radius)
                        result.add((LivingEntity) entity);

        return result;
    }

    private static double boxDistance(Location loc1, Location loc2)
    {
        return Math.max(Math.max(Math.abs(loc1.getX() - loc2.getX()), Math.abs(loc1.getY() - loc2.getY())), Math.abs(loc1.getZ() - loc2.getZ()));
    }
}
