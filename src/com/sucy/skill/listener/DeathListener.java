/**
 * SkillAPI
 * com.sucy.skill.listener.DeathListener
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
package com.sucy.skill.listener;

import com.rit.sucy.reflect.Reflection;
import com.sucy.skill.api.event.SkillDamageEvent;
import com.sucy.skill.api.event.TrueDamageEvent;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.lang.reflect.Method;

public class DeathListener extends SkillAPIListener
{
    private Method handle;
    private Method playerAttack;
    private Method mobAttack;
    private Method die;

    public DeathListener()
    {
        try
        {
            Class<?> entityLiving = Reflection.getNMSClass("EntityLiving");
            Class<?> source = Reflection.getNMSClass("DamageSource");
            playerAttack = source.getDeclaredMethod("playerAttack", Reflection.getNMSClass("EntityHuman"));
            mobAttack = source.getDeclaredMethod("mobAttack", entityLiving);
            die = entityLiving.getDeclaredMethod("die", source);
            handle = Reflection.getCraftClass("entity.CraftEntity")
                .getDeclaredMethod("getHandle");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Launches our own death event for when entities are killed via skills
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpell(SkillDamageEvent event)
    {
        handle(event.getTarget(), event.getDamager(), event.getDamage());
    }

    /**
     * Launches our own death event for when entities are killed via skills
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTrue(TrueDamageEvent event)
    {
        handle(event.getTarget(), event.getDamager(), event.getDamage());
    }

    private void handle(LivingEntity entity, LivingEntity damager, double damage)
    {
        if (entity.getHealth() <= damage)
        {
            try
            {
                Object nmsEntity = handle.invoke(entity);
                Object nmsAttacker = handle.invoke(damager);
                Object damageSource;
                if (damager instanceof HumanEntity)
                    damageSource = playerAttack.invoke(null, nmsAttacker);
                else
                    damageSource = mobAttack.invoke(null, nmsAttacker);
                die.invoke(nmsEntity, damageSource);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
