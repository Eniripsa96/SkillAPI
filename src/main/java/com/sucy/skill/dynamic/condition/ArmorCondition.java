/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.ArmorCondition
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
package com.sucy.skill.dynamic.condition;

import com.google.common.collect.ImmutableList;
import com.rit.sucy.config.parse.DataSection;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.dynamic.ItemChecker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Function;

public class ArmorCondition extends ConditionComponent {
    private static final String ARMOR = "armor";

    private List<Function<EntityEquipment, ItemStack>> getters;

    @Override
    public String getKey() {
        return "armor";
    }

    @Override
    public void load(DynamicSkill skill, DataSection config) {
        super.load(skill, config);
        getters = determineGetters();
    }

    private List<Function<EntityEquipment, ItemStack>> determineGetters() {
        final String type = settings.getString(ARMOR).toLowerCase();
        switch (type) {
            case "helmet":
                return ImmutableList.of(EntityEquipment::getHelmet);
            case "chestplate":
                return ImmutableList.of(EntityEquipment::getChestplate);
            case "leggings":
                return ImmutableList.of(EntityEquipment::getLeggings);
            case "boots":
                return ImmutableList.of(EntityEquipment::getBoots);
            default: // All
                return ImmutableList.of(
                        EntityEquipment::getHelmet,
                        EntityEquipment::getChestplate,
                        EntityEquipment::getLeggings,
                        EntityEquipment::getBoots);
        }
    }

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        final EntityEquipment equipment = target.getEquipment();
        return equipment != null && getters.stream().anyMatch(
                getter -> ItemChecker.check(getter.apply(equipment), level, settings));
    }
}
