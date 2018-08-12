/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.DirectionCondition
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

import com.rit.sucy.config.parse.DataSection;
import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.entity.LivingEntity;

import java.util.function.BiPredicate;

/**
 * A condition for dynamic skills that requires the target or caster to be facing a direction relative to the other
 */
public class DirectionCondition extends ConditionComponent
{
    private static final String TYPE      = "type";
    private static final String DIRECTION = "direction";

    private BiPredicate<LivingEntity, LivingEntity> test;
    private boolean                                 towards;

    @Override
    public String getKey() {
        return "direction";
    }

    @Override
    public void load(DynamicSkill skill, DataSection config) {
        super.load(skill, config);
        towards = settings.getString(DIRECTION).equalsIgnoreCase("towards");
        test = settings.getString(TYPE).equalsIgnoreCase("target")
                ? (caster, target) -> TargetHelper.isInFront(target, caster)
                : TargetHelper::isInFront;
    }

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        return test.test(caster, target) == towards;
    }
}
