/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.ValueLoreMechanic
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

import com.rit.sucy.version.VersionManager;
import com.sucy.skill.dynamic.ItemChecker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Adds to a cast data value
 */
public class ValueLoreMechanic extends MechanicComponent
{
    private static final String KEY        = "key";
    private static final String REGEX      = "regex";
    private static final String MULTIPLIER = "multiplier";
    private static final String HAND       = "hand";

    @Override
    public String getKey() {
        return "value lore";
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
        if (targets.size() == 0 || !settings.has(KEY))
            return false;

        String key = settings.getString(KEY);
        double multiplier = parseValues(caster, MULTIPLIER, level, 1);
        boolean offhand = settings.getString(HAND, "").equalsIgnoreCase("offhand");
        String regex = settings.getString(REGEX, "Damage: {value}");

        if (caster.getEquipment() == null)
            return false;

        ItemStack hand;
        if (offhand && VersionManager.isVersionAtLeast(VersionManager.V1_9_0))
            hand = caster.getEquipment().getItemInOffHand();
        else hand = caster.getEquipment().getItemInHand();

        return ItemChecker.findLore(caster, hand, regex, key, multiplier);
    }
}
