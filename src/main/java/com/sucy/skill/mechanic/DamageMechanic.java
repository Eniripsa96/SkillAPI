package com.sucy.skill.mechanic;

import com.rit.sucy.version.VersionManager;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Mechanic for dealing damage to each target
 */
public class DamageMechanic implements IMechanic {

    private static final String DAMAGE = "Damage";

    /**
     * Deals damage to all targets
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

        // Damage all targets
        boolean worked = false;
        int level = data.getSkillLevel(skill.getName());
        double damage = skill.getAttribute(DAMAGE, target, level);
        for (LivingEntity t : targets) {
            double prevHealth = t.getHealth();
            VersionManager.damage(t, player, damage);
            worked = worked || prevHealth != t.getHealth();
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
        skill.checkDefault(prefix + DAMAGE, 4, 2);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { DAMAGE };
    }
}
