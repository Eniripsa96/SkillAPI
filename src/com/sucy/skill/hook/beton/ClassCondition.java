/**
 * SkillAPI
 * com.sucy.skill.hook.beton.ClassCondition
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

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.HashMap;

/**
 * Beton Quest condition for a player being a specific class
 */
public class ClassCondition extends Condition
{
    private static final String CLASSES = "classes";
    private static final String EXACT   = "exact";

    String[] classes;
    boolean exact = false;

    public ClassCondition(String packName, String instructions)
            throws InstructionParseException
    {
        super(packName, instructions);
        HashMap<String, Object> data = BetonUtil.parse(instructions, CLASSES, EXACT);

        classes = BetonUtil.asArray(data, CLASSES);
        exact = data.get(EXACT).toString().equalsIgnoreCase("true");
    }

    @Override
    public boolean check(String playerID)
    {
        Player player = PlayerConverter.getPlayer(playerID);
        PlayerData data = SkillAPI.getPlayerData(player);
        if (exact)
        {
            for (String c : classes)
            {
                if (data.isExactClass(SkillAPI.getClass(c))) return true;
            }
        }
        else
        {
            for (String c : classes)
            {
                if (data.isClass(SkillAPI.getClass(c))) return true;
            }
        }
        return false;
    }
}
