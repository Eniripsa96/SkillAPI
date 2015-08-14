package com.sucy.skill.hook.beton;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.HashMap;
import pl.betoncraft.betonquest.InstructionParseException;

/**
 * Beton Quest condition for a player being a specific class in the format
 * 'Class name'
 *
 * For example, the condition would look like is_cleric: Class Cleric
 */
public class SkillCondition extends Condition {

    String skillName = "";

    public SkillCondition(String playerID, String instructions) throws InstructionParseException {
        super(playerID, instructions);

        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            throw new InstructionParseException("Skill Condition: Skill name name... name");
        }

        for (int i = 1; i < parts.length; i++) {
            if (i == 1) {
                skillName += parts[i];
            } else {
                skillName += " " + parts[i];
            }
        }

    }

    @Override
    public boolean check(String playerID) {

        Player player = PlayerConverter.getPlayer(playerID);
        PlayerData data = SkillAPI.getPlayerData(player);

        if (data.getSkill(skillName) != null) {
            return true;
        }

        return false;
    }

}
