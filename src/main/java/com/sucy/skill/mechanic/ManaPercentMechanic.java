package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Mechanic for giving mana to targets
 */
public class ManaPercentMechanic implements IMechanic {

    private static final String MANA = "Percent Mana";

    /**
     * Gives mana to all targets
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

        // Change mana of all player targets
        boolean worked = false;
        int level = data.getSkillLevel(skill.getName());
        double amount = skill.getAttribute(MANA, target, level);
        for (LivingEntity t : targets) {
            if (t instanceof Player) {
                PlayerSkills p = skill.getAPI().getPlayer((Player) t);
                int prevMana = p.getMana();
                p.gainMana((int)Math.max(1, amount * p.getMaxMana() / 100));
                worked = worked || (p.getMana() != prevMana);
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
        skill.checkDefault(prefix + MANA, 10, 5);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { MANA };
    }
}
