/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.OffhandCondition
 * <p/>
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2016 Steven Sucy
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.dynamic.condition;

import com.rit.sucy.version.VersionManager;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.dynamic.ItemChecker;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Item condition for a player's off hand
 */
public class OffhandCondition extends EffectComponent {
    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets) {
        if (!VersionManager.isVersionAtLeast(VersionManager.V1_9_0))
            return false;

        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();

        for (LivingEntity target : targets)
            if (target.getEquipment() != null && ItemChecker.check(target.getEquipment().getItemInOffHand(), level, settings))
                list.add(target);

        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
