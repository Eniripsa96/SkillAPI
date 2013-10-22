package com.sucy.skill.mechanic;

import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import com.sucy.skill.api.PlayerSkills;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Mechanic for applying potion effects to all targets
 */
public class PotionMechanic implements IMechanic {

    private static final String
        TYPE = "Type",
        DURATION = "Duration",
        TIER = "Tier";

    /**
     * Applies potion effects to all targets
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
        int level = data.getSkillLevel(skill.getName());
        PotionEffectType potionType = PotionEffectType.values()[skill.getValue(TYPE)];
        int duration = skill.getAttribute(DURATION, target, level);
        int tier = skill.getAttribute(TIER, target, level);

        // Must have a target
        if (targets.size() == 0) return false;

        // Apply  potion effect to all
        for (LivingEntity t : targets) {
            t.addPotionEffect(new PotionEffect(potionType, duration, tier), true);
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
        skill.checkDefault(DURATION, 5, 2);
        skill.checkDefault(TIER, 0, 0);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { DURATION, TIER };
    }
}
