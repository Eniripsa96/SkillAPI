/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.LoreCondition
 * <p/>
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2014 Steven Sucy
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
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

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A condition for dynamic skills that requires the target to have a specified potion effect
 */
public class LoreCondition extends EffectComponent {
    private static final String REGEX = "regex";
    private static final String STRING = "str";

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
        boolean regex = settings.getString(REGEX, "false").toLowerCase().equals("true");
        String str = settings.getString(STRING, "");
        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets) {
            if (target.getEquipment() == null || target.getEquipment().getItemInHand() == null
                    || !target.getEquipment().getItemInHand().hasItemMeta()
                    || !target.getEquipment().getItemInHand().getItemMeta().hasLore()) {
                continue;
            }
            List<String> lore = target.getEquipment().getItemInHand().getItemMeta().getLore();
            for (String line : lore) {
                if (regex && Pattern.compile(str).matcher(line).find()) {
                    list.add(target);
                } else if (!regex && line.contains(str)) {
                    list.add(target);
                }
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
