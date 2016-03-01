package com.sucy.skill.hook.beton;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.HashMap;

/**
 * Beton Quest condition for a player being a certain level
 */
public class LevelCondition extends Condition
{
    private static final String LEVEL = "level";
    private static final String GROUP = "group";
    private static final String MIN   = "min";

    String group;
    int    level;
    boolean min = false;

    public LevelCondition(String packName, String instructions)
            throws InstructionParseException
    {
        super(packName, instructions);
        HashMap<String, Object> data = BetonUtil.parse(instructions, LEVEL, MIN, GROUP);

        group = data.get(GROUP).toString();
        level = Integer.parseInt(data.get(LEVEL).toString());
        min = data.get(MIN).toString().equalsIgnoreCase("true");
    }

    @Override
    public boolean check(String playerID)
    {
        Player player = PlayerConverter.getPlayer(playerID);
        PlayerData data = SkillAPI.getPlayerData(player);
        PlayerClass playerClass = data.getClass(group);
        if (playerClass == null) playerClass = data.getMainClass();
        return playerClass != null && ((level == playerClass.getLevel()) || ((playerClass.getLevel() > level) == min));
    }
}
