/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.FlagCondition
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

import com.sucy.skill.api.util.FlagManager;
import org.bukkit.entity.LivingEntity;

/**
 * A condition for dynamic skills that requires the target to have a specified flag active
 */
public class FlagCondition extends ConditionComponent {
    private static final String TYPE = "type";
    private static final String KEY  = "key";

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        final String flag = settings.getString(KEY);
        final boolean set = !settings.getString(TYPE, "set").toLowerCase().equals("not set");
        return FlagManager.hasFlag(target, flag) == set;
    }

    @Override
    public String getKey() {
        return "flag";
    }
}
