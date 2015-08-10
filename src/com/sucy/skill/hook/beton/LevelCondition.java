package com.sucy.skill.hook.beton;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import pl.betoncraft.betonquest.InstructionParseException;

/**
 * Beton Quest condition for a player being a certain level
 * In the format Level num
 * 
 * For example, to check for a player higher level than 10:
 * is_ten: Level 10
 */
public class LevelCondition extends Condition
{
    private static final String LEVEL = "level";
    private static final String GROUP = "group";
    private static final String MIN   = "min";

    String group;
    int    level;
    boolean min = false;
   
    public LevelCondition(String playerID, String instructions) throws InstructionParseException
    {
        super(playerID, instructions);
     
        String[] parse = instructions.split(" ");
        if (parse.length < 2) {
            throw new InstructionParseException("Please specify a minimum level");
        }
        level = Integer.parseInt(parse[1]);

    }
    
    @Override
    public boolean check(String playerID)
    {
        Player player = PlayerConverter.getPlayer(playerID);
        PlayerData data = SkillAPI.getPlayerData(player);
        PlayerClass playerClass = data.getClass(group);
        if (playerClass == null) playerClass = data.getMainClass();
        //Check if player is over the specified level
        if (playerClass.getLevel() >= level) 
        {
            return true;
        }
        return false;
        
    }

}
