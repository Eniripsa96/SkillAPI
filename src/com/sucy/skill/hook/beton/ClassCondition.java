package com.sucy.skill.hook.beton;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.HashMap;
import pl.betoncraft.betonquest.InstructionParseException;

/**
 * Beton Quest condition for a player being a specific class
 * in the format 'Class name'
 * 
 * For example, the condition would look like
 * is_cleric: Class Cleric
 */
public class ClassCondition extends Condition
{
    private static final String CLASSES = "classes";
    private static final String EXACT   = "exact";

    String[] classes;
    boolean exact = false;
    String classcheck = "";
    
    
    public ClassCondition(String playerID, String instructions) throws InstructionParseException
    {
        super(playerID, instructions);
               
        String[] parts = instructions.split(" ");
        if (parts.length < 2) 
        {
            throw new InstructionParseException("Class to check is not specified");
        }
        classcheck = parts[1];
    }

    @Override
    public boolean check(String playerID) {
        
        Player player = PlayerConverter.getPlayer(playerID);
        PlayerData data = SkillAPI.getPlayerData(player);
        
        //Return true if the player is the Class specified
        if (data.isClass(SkillAPI.getClass(classcheck))) return true;
            
        return false;
    }
    
}
