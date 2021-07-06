/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.PurgeMechanic
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

import com.google.common.collect.ImmutableSet;
import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.api.util.StatusFlag;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Set;

/**
 * Purges a target of positive potion or status effects
 */
public class PurgeMechanic extends MechanicComponent
{
    private static final Set<String> POTIONS = ImmutableSet.of(
            "ABSORPTION", "DAMAGE_RESISTANCE", "FAST_DIGGING", "FIRE_RESISTANCE", "HEALTH_BOOST",
            "INCREASE_DAMAGE", "INVISIBILITY", "JUMP", "NIGHT_VISION", "REGENERATION",
            "SATURATION", "SPEED", "WATER_BREATHING"
    );

    private static final String STATUS = "status";
    private static final String POTION = "potion";

    @Override
    public String getKey() {
        return "purge";
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
        boolean worked = false;
        String status = settings.getString(STATUS, "None").toLowerCase();
        String potion = settings.getString(POTION).toUpperCase().replace(' ', '_');
        PotionEffectType type = null;
        try
        {
            type = PotionEffectType.getByName(potion);
        }
        catch (Exception ex)
        {
            // Invalid potion type
        }

        for (LivingEntity target : targets)
        {
            if (status.equals("all"))
            {
                for (String flag : StatusFlag.POSITIVE)
                {
                    if (FlagManager.hasFlag(target, flag))
                    {
                        FlagManager.removeFlag(target, flag);
                        worked = true;
                    }
                }
            }
            else if (FlagManager.hasFlag(target, status))
            {
                FlagManager.removeFlag(target, status);
                worked = true;
            }

            if (potion.equals("ALL"))
            {
                for (PotionEffectType p : PotionEffectType.values())
                {
                    if (target.hasPotionEffect(p) && POTIONS.contains(p.getName()))
                    {
                        target.removePotionEffect(p);
                        worked = true;
                    }
                }
            }
            else if (type != null && target.hasPotionEffect(type))
            {
                target.removePotionEffect(type);
                worked = true;
            }
        }
        return worked;
    }
}
