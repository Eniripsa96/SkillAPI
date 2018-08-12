/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.PotionCondition
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
package com.sucy.skill.dynamic.condition;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

/**
 * A condition for dynamic skills that requires the target to have a specified potion effect
 */
public class PotionCondition extends ConditionComponent {
    private static final String TYPE     = "type";
    private static final String POTION   = "potion";
    private static final String MIN_RANK = "min-rank";
    private static final String MAX_RANK = "max-rank";

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        final boolean active = !settings.getString(TYPE, "active").toLowerCase().equals("not active");
        final Collection<PotionEffect> effects = target.getActivePotionEffects();
        if (effects.isEmpty()) return !active;

        final String potion = settings.getString(POTION, "").toUpperCase().replace(' ', '_');
        final int minRank = (int) parseValues(caster, MIN_RANK, level, 0);
        final int maxRank = (int) parseValues(caster, MAX_RANK, level, 999);
        try {
            final PotionEffectType type = PotionEffectType.getByName(potion);
            return has(target, type, minRank, maxRank) == active;
        } catch (Exception ex) {
            for (final PotionEffect check : effects) {
                if (check.getAmplifier() >= minRank && check.getAmplifier() <= maxRank) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean has(LivingEntity target, PotionEffectType type, int min, int max) {
        if (!target.hasPotionEffect(type)) { return false; }
        int rank = target.getPotionEffect(type).getAmplifier();
        return rank >= min && rank <= max;
    }

    @Override
    public String getKey() {
        return "potion";
    }
}
