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
