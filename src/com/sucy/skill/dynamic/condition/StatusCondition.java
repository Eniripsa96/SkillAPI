/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.StatusCondition
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
import com.sucy.skill.api.util.StatusFlag;
import org.bukkit.entity.LivingEntity;

import java.util.Arrays;

/**
 * A condition for dynamic skills that requires the target to have a status condition
 */
public class StatusCondition extends ConditionComponent {
    private static final String TYPE   = "type";
    private static final String STATUS = "status";

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        final boolean active = !settings.getString(TYPE, "active").equals("not active");
        final String status = settings.getString(STATUS).toLowerCase();

        if (status.equals("any")) {
            return active == Arrays.stream(StatusFlag.ALL).anyMatch(flag -> FlagManager.hasFlag(target, flag));
        } else {
            return FlagManager.hasFlag(target, status) == active;
        }
    }

    @Override
    public String getKey() {
        return "status";
    }
}
