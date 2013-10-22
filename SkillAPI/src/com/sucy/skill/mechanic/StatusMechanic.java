package com.sucy.skill.mechanic;

import com.sucy.skill.api.Status;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import com.sucy.skill.api.PlayerSkills;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Mechanic for applying status effects to all targets
 */
public class StatusMechanic implements IMechanic {

    private static final String
            TYPE = "Type",
            LENGTH = "Length";

    /**
     * Applies a status to all targets
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

        // Get attributes
        int level = data.getSkillLevel(skill.getName());
        Status status = Status.values()[skill.getValue(TYPE)];
        int duration = skill.getAttribute(LENGTH, target, level);

        // Apply  potion effect to all
        boolean worked = false;
        for (LivingEntity t : targets) {
            if (t instanceof Player) {
                data.getAPI().getPlayer(((Player) t).getName()).applyStatus(status, duration);
                worked = true;
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
        skill.checkDefault(LENGTH, 3, 1);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { LENGTH };
    }
}
