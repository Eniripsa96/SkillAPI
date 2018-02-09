/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.MessageMechanic
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

import com.rit.sucy.mobs.MobManager;
import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * Sends a message to each player target
 */
public class MessageMechanic extends EffectComponent
{
    private static final String MESSAGE = "message";

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
        if (targets.size() == 0 || !settings.has(MESSAGE))
        {
            return false;
        }

        String message = TextFormatter.colorString(settings.getString(MESSAGE));
        if (message == null) return false;

        // Grab values
        HashMap<String, Object> data = DynamicSkill.getCastData(caster);
        int i = message.indexOf('{');
        while (i >= 0)
        {
            int j = message.indexOf('}', i);
            String key = message.substring(i + 1, j);
            if (data.containsKey(key))
            {
                Object obj = data.get(key);
                if (obj instanceof Player)
                    obj = ((Player) obj).getName();
                else if (obj instanceof LivingEntity)
                    obj = MobManager.getName((LivingEntity) obj);
                message = message.substring(0, i) + obj + message.substring(j + 1);
            }
            i = message.indexOf('{', j);
        }

        // Display message
        boolean worked = false;
        for (LivingEntity target : targets)
        {
            if (target instanceof Player)
            {
                target.sendMessage(message);
                worked = true;
            }
        }
        return worked;
    }
}
