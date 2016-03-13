/**
 * SkillAPI
 * com.sucy.skill.listener.ListenerUtil
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
package com.sucy.skill.listener;

import com.rit.sucy.reflect.Reflection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Helper class for listeners
 */
public class ListenerUtil
{
    /**
     * Retrieves a damager from an entity damage event which will get the
     * shooter of projectiles if it was a projectile hitting them or
     * converts the Entity damager to a LivingEntity if applicable.
     *
     * @param event event to grab the damager from
     *
     * @return LivingEntity damager of the event or null if not found
     */
    public static LivingEntity getDamager(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof LivingEntity)
        {
            return (LivingEntity) event.getDamager();
        }
        else if (event.getDamager() instanceof Projectile)
        {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof LivingEntity)
            {
                return (LivingEntity) projectile.getShooter();
            }
        }
        return null;
    }

    /**
     * Gets a simple name of the entity
     *
     * @param entity entity to get the name of
     *
     * @return simple name of the entity
     */
    public static String getName(Entity entity)
    {
        String name = entity.getClass().getSimpleName().toLowerCase().replace("craft", "");
        if (entity.getType().name().equals("SKELETON"))
        {
            try
            {
                Object type = Reflection.getMethod(entity, "getSkeletonType").invoke(entity);
                if (Reflection.getMethod(type, "name").invoke(type).equals("WITHER"))
                {
                    name = "wither" + name;
                }
            }
            catch (Exception ex)
            { /* Wither skeletons don't exist */ }
        }
        else if (entity.getType().name().equals("GUARDIAN"))
        {
            try
            {
                if ((Boolean) Reflection.getMethod(entity, "isElder").invoke(entity))
                {
                    name = "elder" + name;
                }
            }
            catch (Exception ex)
            { /* Shouldn't error out */ }
        }
        return name;
    }
}
