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

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.SkillDamageEvent;
import com.sucy.skill.api.event.TrueDamageEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathListener extends SkillAPIListener
{
    private final String KILLER = "sapiKiller";

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

    private void handle(final LivingEntity entity, final LivingEntity damager, final double damage)
    {
        SkillAPI.setMeta(entity, KILLER, damager);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(EntityDeathEvent event) {
        Object killer = SkillAPI.getMeta(event.getEntity(), KILLER);
        if (killer != null && event.getEntity().getKiller() == null) {
            applyDeath(event.getEntity(), (LivingEntity)killer, event.getDroppedExp());
        }
    }

    private void applyDeath(LivingEntity entity, LivingEntity damager, int exp) {
        if (!entity.isDead() || entity.getKiller() != null || !(damager instanceof Player))
            return;

        KillListener.giveExp(entity, (Player)damager, exp);
    }
}
