package com.sucy.skill.mechanic;

import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import com.sucy.skill.api.PlayerSkills;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Mechanic for healing all targets
 */
public class HealMechanic implements IMechanic {

    private static final String HEAL = "Heal";

    /**
     * Heals all targets
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
        int amount = skill.getAttribute(HEAL, target, level);
        for (LivingEntity t : targets) {
            double prevHealth = t.getHealth();
            if (t instanceof Player) {
                data.getAPI().getPlayer(((Player) t).getName()).heal(amount);
                worked = true;
            }
            else {
                double health = prevHealth + amount;
                if (health > t.getMaxHealth()) health = t.getMaxHealth();
                t.setHealth(health);
            }
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
        skill.checkDefault(prefix + HEAL, 4, 2);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { HEAL };
    }
}
