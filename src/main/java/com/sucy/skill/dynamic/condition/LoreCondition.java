/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.LoreCondition
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
import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class LoreCondition extends ConditionComponent {
    private static final String REGEX  = "regex";
    private static final String STRING = "str";

    private Predicate<String> test;

    @Override
    public String getKey() {
        return "lore";
    }

    @Override
    public void load(DynamicSkill skill, DataSection config) {
        super.load(skill, config);
        final boolean regex = settings.getString(REGEX, "false").toLowerCase().equals("true");
        final String str = settings.getString(STRING, "");
        if (regex) {
            final Pattern pattern = Pattern.compile(str);
            test = line -> pattern.matcher(line).find();
        } else {
            test = line -> line.contains(str);
        }
    }

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        final EntityEquipment items = target.getEquipment();
        if (items == null || items.getItemInHand() == null || !items.getItemInHand().hasItemMeta()) { return false; }

        final List<String> lore = items.getItemInHand().getItemMeta().getLore();
        return lore != null && lore.stream().anyMatch(test);
    }
}
