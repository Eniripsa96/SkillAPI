package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Mechanic for making targets dash forward
 */
public class CommandMechanic implements IMechanic {

    private static final String
            COMMAND = "Command",
            COMMAND_TYPE = "CommandType";

    /**
     * Forces all targets to dash
     *
     * @param player  player using the skill
     * @param data    data of the player using the skill
     * @param skill   skill being used
     * @param target  target type of the skill
     * @param targets targets for the effects
     * @return        true if there were targets, false otherwise
     */
    @Override
    public boolean resolve(Player player, PlayerSkills data, DynamicSkill skill, Target target, List<LivingEntity> targets) {

        // Needs targets
        if (targets.size() == 0 || !skill.hasString(COMMAND)) return false;

        // Get values
        String command = skill.getString(COMMAND);
        if (command.startsWith("/")) command = command.substring(1);
        String[] commands;
        if (command.contains(",")) commands = command.split(",");
        else commands = new String[] { command };
        int type = skill.getValue(COMMAND_TYPE);

        // Run commands
        boolean worked = false;
        for (LivingEntity t : targets) {
            if (t instanceof Player) {
                Player p = (Player)t;
                worked = true;
                for (String c : commands) {
                    String filtered = c.replace("{player}", p.getName());
                    if (type == 0) {
                        boolean op = p.isOp();
                        p.setOp(true);
                        skill.getAPI().getServer().dispatchCommand(p, filtered);
                        p.setOp(op);
                    }
                    else skill.getAPI().getServer().dispatchCommand(Bukkit.getConsoleSender(), filtered);
                }
            }
        }

        return worked;
    }

    /**
     * Sets default attributes for the skill
     *
     * @param skill  skill to apply to
     * @param prefix prefix to add to the name
     */
    @Override
    public void applyDefaults(DynamicSkill skill, String prefix) {
        if (!skill.isSet(COMMAND_TYPE)) skill.setValue(COMMAND_TYPE, 0);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[0];
    }
}
