/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.DamageLoreMechanic
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

import com.rit.sucy.config.parse.NumberParser;
import com.rit.sucy.version.VersionManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deals damage based on a held item's lore to each target
 */
public class DamageLoreMechanic extends MechanicComponent
{
    private static final String REGEX      = "regex";
    private static final String MULTIPLIER = "multiplier";
    private static final String HAND       = "hand";
    private static final String TRUE       = "true";

    @Override
    public String getKey() {
        return "damage lore";
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
        String regex = settings.getString(REGEX, "Damage: {value}");
        regex = regex.replace("{value}", "([0-9]+)");
        Pattern pattern = Pattern.compile(regex);
        double m = parseValues(caster, MULTIPLIER, level, 1.0);
        boolean worked = false;
        boolean offhand = VersionManager.isVersionAtLeast(VersionManager.V1_9_0)
            && settings.getString(HAND).equalsIgnoreCase("offhand");
        boolean trueDmg = settings.getBool(TRUE, false);

        if (caster.getEquipment() == null)
            return false;

        ItemStack hand;
        if (offhand)
            hand = caster.getEquipment().getItemInOffHand();
        else hand = caster.getEquipment().getItemInHand();

        if (hand == null || !hand.hasItemMeta() || !hand.getItemMeta().hasLore())
            return false;

        List<String> lore = hand.getItemMeta().getLore();
        for (String line : lore)
        {
            line = ChatColor.stripColor(line);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find())
            {
                String value = matcher.group(1);
                try
                {
                    double base = NumberParser.parseDouble(value);
                    if (base * m > 0)
                    {
                        for (LivingEntity target : targets)
                        {
                            if (target.isDead())
                                continue;

                            if (trueDmg)
                                skill.trueDamage(target, base * m, caster);
                            else
                                skill.damage(target, base * m, caster);
                        }
                        worked = targets.size() > 0;
                        break;
                    }
                }
                catch (Exception ex)
                {
                    // Not a valid value
                }
            }
        }
        return worked;
    }
}
