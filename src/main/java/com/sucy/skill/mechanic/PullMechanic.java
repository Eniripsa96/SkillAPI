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
 * Mechanic for pulling targets toward the caster
 */
public class PullMechanic implements IMechanic {

    private static final String SPEED = "Pull Speed";

    /**
     * Pulls in all targets
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
        double speed = skill.getAttribute(SPEED, target, level);

        // Pull in all targets
        for (LivingEntity t : targets) {
            Vector vel = player.getLocation().subtract(t.getLocation()).toVector();
            if (vel.lengthSquared() == 0) continue;
            vel.multiply(speed / vel.length());
            vel.setY(vel.getY() / 5 + 0.5);
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
        skill.checkDefault(prefix + SPEED, 3, 1);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { SPEED };
    }
}
