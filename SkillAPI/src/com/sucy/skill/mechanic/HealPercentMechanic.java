package com.sucy.skill.mechanic;

import com.sucy.skill.version.VersionManager;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Mechanic for damaging targets based on missing mana
 */
public class HealPercentMechanic implements IMechanic {

    private static final String
            HEALTH = "Heal Percent",
            TYPE = "HealType";

    /**
     * Damages targets based on missing mana
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
        double amount = skill.getAttribute(HEALTH, target, level);
        int damageType = skill.getValue(TYPE);
        for (LivingEntity t : targets) {
            double damage;

            // Missing health
            if (damageType == 1) damage = amount * (t.getMaxHealth() - t.getHealth()) / 100.0;

            // Current health
            else if (damageType == 0) damage = amount * t.getHealth() / 100.0;

            // Max health
            else damage = amount * t.getMaxHealth() / 100;

            double prevHealth = t.getHealth();
            if (t instanceof Player) {
                skill.getAPI().getPlayer((Player) t).heal(damage);
            }
            else VersionManager.heal(t, damage);
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
        skill.checkDefault(prefix + HEALTH, 10, 5);
        if (!skill.isSet(TYPE)) skill.setValue(TYPE, 0);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] {HEALTH};
    }
}
