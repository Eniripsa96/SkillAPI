package com.sucy.skill.mechanic;

import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import com.sucy.skill.api.PlayerSkills;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Mechanic for pushing targets away from the caster
 */
public class TauntMechanic implements IMechanic {

    /**
     * Makes all targets dash
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

        // Taunt all targets
        boolean worked = false;
        for (LivingEntity t : targets) {
            if (t instanceof Creature) {
                ((Creature)t).setTarget(player);
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
    public void applyDefaults(DynamicSkill skill, String prefix) { }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[0];
    }
}
