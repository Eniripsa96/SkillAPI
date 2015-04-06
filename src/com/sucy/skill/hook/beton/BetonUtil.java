package com.sucy.skill.hook.beton;

import pl.betoncraft.betonquest.BetonQuest;

import java.util.HashMap;

public class BetonUtil
{
    public static HashMap<String, Object> parse(String data, String... keys)
    {
        HashMap<String, Object> parsed = new HashMap<String, Object>();
        for (String part : data.split(" "))
        {
            for (String key : keys)
            {
                if (part.startsWith(keys + ":"))
                {
                    if (part.contains(","))
                    {
                        parsed.put(key, part.substring(key.length() + 1).split(","));
                    }
                    else
                    {
                        parsed.put(key, part.substring(key.length() + 1));
                    }
                }
            }
        }
        for (String key : keys)
        {
            if (!parsed.containsKey(key))
            {
                parsed.put(key, "");
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
