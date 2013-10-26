package com.sucy.skill.mechanic;

import com.sucy.skill.api.DamageModifier;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import com.sucy.skill.api.PlayerSkills;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Mechanic for granting percentage bonus damage
 */
public class DamagePercentMechanic implements IMechanic {

    private static final String
            PERCENT = "Percent",
            DURATION = "Percent Duration";

    /**
     * Grants a temporary damage bonus to the targets
     *
     * @param player  player using the skill
     * @param data    data of the player using the skill
     * @param skill   skill being used
     * @param target  target type of the skill
     * @param targets targets for the effects
     * @return        true if was able to use
     */
    @Override
    public boolean resolve(Player player, PlayerSkills data, DynamicSkill skill, Target target, List<LivingEntity> targets) {

        // Requires a target
        if (targets.size() == 0) return false;

        // Get attributes
        int level = data.getSkillLevel(skill.getName());
        int bonus = skill.getAttribute(PERCENT, target, level);
        int duration = skill.getAttribute(DURATION, target, level);

        // Add damage modifiers
        for (LivingEntity entity : targets) {
            data.getAPI().getStatusHolder(entity).addDamageModifier(new DamageModifier(1 + bonus / 100.0, duration * 1000));
        }

        return true;
    }

    /**
     * Applies default values for the mechanic attributes
     *
     * @param skill  skill to apply to
     * @param prefix prefix to add to the attribute
     */
    @Override
    public void applyDefaults(DynamicSkill skill, String prefix) {
        skill.checkDefault(prefix + PERCENT, 10, 5);
        skill.checkDefault(prefix + DURATION, 5, 0);
    }

    /**
     * @return names of the attributes used by the mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { PERCENT, DURATION };
    }
}
