package com.sucy.skill.hook.beton;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.entity.Player;
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

    public ClassCondition(String playerID, String instructions)
    {
        super(playerID, instructions);
        HashMap<String, Object> data = BetonUtil.parse(instructions, CLASSES, EXACT);

        classes = BetonUtil.asArray(data, CLASSES);
        exact = data.get(EXACT).toString().equalsIgnoreCase("true");
    }

    @Override
    public boolean isMet()
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
