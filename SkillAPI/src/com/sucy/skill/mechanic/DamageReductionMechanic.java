package com.sucy.skill.mechanic;

import com.sucy.skill.api.DamageModifier;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Mechanic for granting damage reduction
 */
public class DamageReductionMechanic implements IMechanic {

    private static final String
            REDUCTION = "Reduction",
            DURATION = "Reduction Duration";

    /**
     * Grants a temporary damage reduction buff to the targets
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
        int reduction = (int)skill.getAttribute(REDUCTION, target, level);
        double duration = skill.getAttribute(DURATION, target, level);

        // Add damage modifiers
        for (LivingEntity entity : targets) {
            data.getAPI().getStatusHolder(entity).addDefenseModifier(new DamageModifier(reduction, (int)(duration * 1000)));
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
        skill.checkDefault(prefix + REDUCTION, 1, 1);
        skill.checkDefault(prefix + DURATION, 5, 0);
    }

    /**
     * @return names of the attributes used by the mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { REDUCTION, DURATION };
    }
}
