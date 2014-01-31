package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.EmbedData;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Mechanic for applying embedded effects under a condition
 */
public class ValueConditionMechanic implements IMechanic {

    private static final String
            CONDITION = "ConditionKey",
            VALUE = "ConditionValue",
            TYPE = "ConditionType";

    private static final int
            HEALTH = 0,
            PERCENT_HEALTH = 1,
            HEALTH_DIFFERENCE = 2,
            PERCENT_HEALTH_DIFFERENCE = 3,
            MANA = 4,
            PERCENT_MANA = 5,
            MANA_DIFFERENCE = 6,
            PERCENT_MANA_DIFFERENCE = 7,
            LEVEL = 8,
            LEVEL_DIFFERENCE = 9,
            PERCENT_LEVEL_DIFFERENCE = 10,
            ELEVATION = 11,
            ELEVATION_DIFFERENCE = 12,
            LIGHT_LEVEL = 13;

    private static final int
            AT_LEAST = 0,
            AT_MOST = 1,
            MORE_THAN = 2,
            LESS_THAN = 3,
            EXACTLY = 4,
            NOT = 5;

    /**
     * Grants a temporary damage bonus to the targets
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

        if (targets.isEmpty()) return false;

        // Get attributes
        int condition = skill.getValue(CONDITION);
        int value = skill.getValue(VALUE);
        int type = skill.getValue(TYPE);

        // Prepare the embed data in case it needs to be used
        EmbedData embedData = new EmbedData(player, data, skill);

        // Loop through each target
        boolean worked = false;
        for (LivingEntity t : targets) {

            // Initial values
            boolean passed = false;
            PlayerSkills tData = null;
            if (t instanceof Player) tData = skill.getAPI().getPlayer(((Player) t).getName());

            // Check the condition
            if (condition == HEALTH && compare(value, (int)t.getHealth(), type)) passed = true;
            else if (condition == PERCENT_HEALTH && compare(value, (int)(t.getHealth() * 100 / t.getMaxHealth()), type)) passed = true;
            else if (condition == HEALTH_DIFFERENCE && compare(value, (int)(t.getHealth() - player.getHealth()), type)) passed = true;
            else if (condition == PERCENT_HEALTH_DIFFERENCE && compare(value, (int)((100 * t.getHealth() - player.getHealth()) / player.getHealth()), type)) passed = true;
            else if (condition == MANA && tData != null && compare(value, tData.getMana(), type)) passed = true;
            else if (condition == PERCENT_MANA && tData != null && compare(value, tData.getMana() * 100 / tData.getMaxMana(), type)) passed = true;
            else if (condition == MANA_DIFFERENCE && tData != null && compare(value, tData.getMana() - data.getMana(), type)) passed = true;
            else if (condition == PERCENT_MANA_DIFFERENCE && tData != null && compare(value, 100 * (tData.getMana() - data.getMana()) / data.getMana(), type)) passed = true;
            else if (condition == LEVEL && tData != null && compare(value, tData.getLevel(), type)) passed = true;
            else if (condition == LEVEL_DIFFERENCE && tData != null && compare(value, tData.getLevel() - data.getLevel(), type)) passed = true;
            else if (condition == PERCENT_LEVEL_DIFFERENCE && tData != null && compare(value, 100 * (tData.getLevel() - data.getLevel()) / data.getLevel(), type)) passed = true;
            else if (condition == ELEVATION && compare(value, t.getLocation().getBlockY(), type)) passed = true;
            else if (condition == ELEVATION_DIFFERENCE && compare(value, t.getLocation().getBlockY() - player.getLocation().getBlockY(), type)) passed = true;
            else if (condition == LIGHT_LEVEL && compare(value, t.getLocation().getBlock().getLightLevel(), type)) passed = true;

            // Apply the embedded effects if the condition passed
            if (passed) {
                embedData.resolveNonTarget(t.getLocation());
                embedData.resolveTarget(t);
                worked = true;
            }
        }

        return worked;
    }

    /**
     * Compares the condition using the comparison type
     *
     * @param condition  condition value
     * @param actual     actual value
     * @param comparison comparison type
     * @return           true if passed, false otherwise
     */
    private boolean compare(int condition, int actual, int comparison) {
        if (comparison == AT_LEAST) return actual >= condition;
        else if (comparison == AT_MOST) return actual <= condition;
        else if (comparison == MORE_THAN) return actual > condition;
        else if (comparison == LESS_THAN) return actual < condition;
        else if (comparison == EXACTLY) return actual == condition;
        else if (comparison == NOT) return actual != condition;
        return false;
    }

    /**
     * Applies default values for the mechanic attributes
     *
     * @param skill  skill to apply to
     * @param prefix prefix to add to the attribute
     */
    @Override
    public void applyDefaults(DynamicSkill skill, String prefix) {
        if (!skill.isSet(CONDITION)) skill.setValue(CONDITION, 0);
        if (!skill.isSet(VALUE)) skill.setValue(VALUE, 0);
        if (!skill.isSet(TYPE)) skill.setValue(TYPE, 0);
    }

    /**
     * @return names of the attributes used by the mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[0];
    }
}
