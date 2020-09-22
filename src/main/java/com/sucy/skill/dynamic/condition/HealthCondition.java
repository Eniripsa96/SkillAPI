/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.HealthCondition
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

/**
 * A condition for dynamic skills that requires the target's health to fit the requirement
 */
public class HealthCondition extends ConditionComponent {
    private static final String TYPE = "type";
    private static final String MIN  = "min-value";
    private static final String MAX  = "max-value";

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        final String type = settings.getString(TYPE).toLowerCase();
        final double min = parseValues(caster, MIN, level, 0);
        final double max = parseValues(caster, MAX, level, 999);

        double value;
        switch (type) {
            case "difference percent":
                value = (target.getHealth() - caster.getHealth()) * 100 / caster.getHealth();
                break;
            case "difference":
                value = target.getHealth() - caster.getHealth();
                break;
            case "percent":
                value = target.getHealth() * 100 / target.getMaxHealth();
                break;
            default:
                value = target.getHealth();
        }
        return value >= min && value <= max;
    }

    @Override
    public String getKey() {
        return "health";
    }
}
