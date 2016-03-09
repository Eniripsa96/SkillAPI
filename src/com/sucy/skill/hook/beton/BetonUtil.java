/**
 * SkillAPI
 * com.sucy.skill.hook.beton.BetonUtil
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
package com.sucy.skill.hook.beton;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;

import java.util.HashMap;

public class BetonUtil
{
    public static HashMap<String, Object> parse(String data, String... keys)
            throws InstructionParseException
    {
        String[] parts = data.split(" ");
        if (keys.length != parts.length)
            throw new InstructionParseException("Missing arguments for the condition");

        HashMap<String, Object> parsed = new HashMap<String, Object>();

        int i = 0;
        for (String part : parts)
        {
            if (part.contains(","))
            {
                parsed.put(keys[i], part.split(","));
            }
            else
            {
                parsed.put(keys[i], part);
            }
        }
        return parsed;
    }

    public static String[] asArray(HashMap<String, Object> data, String key)
    {
        return data.get(key) instanceof String[] ? (String[]) data.get(key) : new String[] { data.get(key).toString() };
    }

    public static void register()
    {
        BetonQuest api = BetonQuest.getInstance();
        api.registerConditions("Class", ClassCondition.class);
        api.registerConditions("Level", LevelCondition.class);
    }
}
