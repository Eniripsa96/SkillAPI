package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Mechanic for making targets dash forward
 */
public class LaunchMechanic implements IMechanic {

    private static final String
            V_SPEED = "Vertical Speed",
            H_SPEED = "Horizontal Speed";

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

        // Get attributes
        boolean worked = false;
        int level = data.getSkillLevel(skill.getName());
        int vSpeed = skill.getAttribute(V_SPEED, target, level);
        int hSpeed = skill.getAttribute(H_SPEED, target, level);

        // Make all targets dash forward
        for (LivingEntity t : targets) {
            Vector vel = player.getLocation().getDirection();
            if (vel.lengthSquared() == 0) continue;
            vel.setY(0);
            vel.multiply(hSpeed / vel.length());
            vel.setY(vSpeed + 0.5);
            t.setVelocity(vel);
            worked = true;
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
        skill.checkDefault(prefix + V_SPEED, 0, 0);
        skill.checkDefault(prefix + H_SPEED, 3, 1);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { H_SPEED, V_SPEED };
    }
}
