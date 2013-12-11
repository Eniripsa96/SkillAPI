package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * Mechanic for healing all targets
 */
public class HealthMechanic implements IMechanic {

    private static final String HEALTH = "Health";

    private HashMap<String, Integer> playerBonuses = new HashMap<String, Integer>();
    private HashMap<String, Integer> mobBonuses = new HashMap<String, Integer>();

    /**
     * Grants bonus health to all targets
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

        // Grant health to all targets
        boolean worked = false;
        int level = data.getSkillLevel(skill.getName());
        int amount = (int)skill.getAttribute(HEALTH, target, level);
        for (LivingEntity t : targets) {

            // Players
            if (t instanceof Player) {
                data.getAPI().getPlayer(((Player) t).getName()).addMaxHealth(amount);
            }

            // Non-players
            else {
                double prevHealth = t.getHealth();
                double maxHealth = t.getMaxHealth() + amount;
                if (maxHealth < 1) {
                    maxHealth = 1;
                    amount = (int)(maxHealth - t.getMaxHealth());
                }
                t.setMaxHealth(maxHealth);
                double newHealth = t.getHealth() + amount;
                if (newHealth < 1) newHealth = 1;
                t.setHealth(newHealth);
            }
        }

        return true;
    }

    /**
     * Sets default attributes for the skill
     *
     * @param skill  skill to apply to
     * @param prefix prefix to add to the name
     */
    @Override
    public void applyDefaults(DynamicSkill skill, String prefix) {
        skill.checkDefault(prefix + HEALTH, 5, 2);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] {HEALTH};
    }
}
