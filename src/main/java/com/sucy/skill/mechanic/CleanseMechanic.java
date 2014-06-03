package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.Status;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Mechanic for removing potion or status effects
 */
public class CleanseMechanic implements IMechanic {

    private static final Status[] STATUSES = Status.values();
    private static final PotionEffectType[] POTIONS = PotionEffectType.values();

    private static final String CLEANSE = "Cleanse";

    /**
     * Cleanses statuses from each target
     *
     * @param player  player using the skill
     * @param data    data of the player using the skill
     * @param skill   skill being used
     * @param target  target type of the skill
     * @param targets targets for the effects
     * @return        true if cleansed any statuses, false otherwise
     */
    @Override
    public boolean resolve(Player player, PlayerSkills data, DynamicSkill skill, Target target, List<LivingEntity> targets) {

        // Get the attributes
        int cleanse = skill.getValue(CLEANSE);

        boolean worked = false;

        // Cycle through each target
        for (LivingEntity t : targets) {
            int value = cleanse;

            // Check statuses
            int index = -1;
            for (Status status : STATUSES) {
                if (value == 0) break;
                index++;
                if ((value & 1) == 1 && data.getAPI().getStatusHolder(t).hasStatus(STATUSES[index])) {
                    data.removeStatus(STATUSES[index]);
                    worked = true;
                }
                value /= 2;
            }

            // Check fire
            if ((value & 1) == 1 && t.getFireTicks() > 0) {
                t.setFireTicks(0);
                worked = true;
            }
            value /= 2;

            // Check potion effects
            index = -1;
            for (PotionEffectType potion : POTIONS) {
                if (value == 0) break;
                index++;
                if ((value & 1) == 1 && t.hasPotionEffect(potion)) {
                    t.removePotionEffect(potion);
                    worked = true;
                }
                value /= 2;
            }
        }

        return worked;
    }

    /**
     * Applies default values to the skill
     *
     * @param skill  skill to apply to
     * @param prefix prefix to add to the attribute
     */
    @Override
    public void applyDefaults(DynamicSkill skill, String prefix) {
        if (!skill.isSet(CLEANSE)) skill.setValue(CLEANSE, 1);
    }

    /**
     * @return array of names of attributes for this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[0];
    }
}
