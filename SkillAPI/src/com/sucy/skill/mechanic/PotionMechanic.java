package com.sucy.skill.mechanic;

import com.sucy.skill.BukkitHelper;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
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
        int potionValue = skill.getValue(TYPE);
        int duration = (int)(skill.getAttribute(DURATION, target, level) * 20);
        int tier = (int)skill.getAttribute(TIER, target, level);

        // Must have a target
        if (targets.size() == 0) return false;

        // Apply  potion effect to all
        while (potionValue > 0) {
            PotionEffectType potionType = POTION_TYPES.get(potionValue % 32);
            potionValue /= 32;
            for (LivingEntity t : targets) {
                t.addPotionEffect(new PotionEffect(potionType, duration, tier), true);
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
        skill.checkDefault(prefix + DURATION, 5, 2);
        skill.checkDefault(prefix + TIER, 0, 0);
        if (!skill.isSet(TYPE) || !POTION_TYPES.containsKey(skill.getValue(TYPE))) {
            skill.setValue(TYPE, 1);
        }
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { DURATION, TIER };
    }

    private static final HashMap<Integer, PotionEffectType> POTION_TYPES = new HashMap<Integer, PotionEffectType>() {{
        put(1, PotionEffectType.SPEED);
        put(2, PotionEffectType.SLOW);
        put(3, PotionEffectType.FAST_DIGGING);
        put(4, PotionEffectType.SLOW_DIGGING);
        put(5, PotionEffectType.INCREASE_DAMAGE);
        put(8, PotionEffectType.JUMP);
        put(9, PotionEffectType.CONFUSION);
        put(10, PotionEffectType.REGENERATION);
        put(11, PotionEffectType.DAMAGE_RESISTANCE);
        put(12, PotionEffectType.FIRE_RESISTANCE);
        put(13, PotionEffectType.WATER_BREATHING);
        put(14, PotionEffectType.INVISIBILITY);
        put(15, PotionEffectType.BLINDNESS);
        put(16, PotionEffectType.NIGHT_VISION);
        put(17, PotionEffectType.HUNGER);
        put(18, PotionEffectType.WEAKNESS);
        put(19, PotionEffectType.POISON);
        put(20, PotionEffectType.WITHER);

        if (BukkitHelper.isVersionAtLeast(BukkitHelper.MC_1_6_2_MIN)) {
            put(21, PotionEffectType.HEALTH_BOOST);
            put(22, PotionEffectType.ABSORPTION);
            put(23, PotionEffectType.SATURATION);
        }
    }};
}
