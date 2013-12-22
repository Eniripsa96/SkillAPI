package com.sucy.skill.mechanic;

import com.sucy.skill.BukkitHelper;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.Status;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.EmbedData;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Mechanic for applying embedded effects under a condition
 */
public class ConditionMechanic implements IMechanic {

    private static final String
            CONDITION = "Condition";

    private static final int
        CONDITIONS = 256, // 2 ^ 8
        OPERATORS = 16;   // 2 ^ 4

    private static final int
        STUN = 0,
        ROOT = 1,
        INVINCIBLE = 2,
        ABSORB = 3,
        SILENCE = 4,
        DISARM = 5,
        CURSE = 6,
        FIRE = 7,
        SPEED = 8,
        SLOWNESS = 9,
        HASTE = 10,
        FATIGUE = 11,
        STRENGTH = 12,
        JUMP = 13,
        NAUSEA = 14,
        REGENERATION = 15,
        RESISTANCE = 16,
        FIRE_RESISTANCE = 17,
        WATER_BREATHING = 18,
        INVISIBILITY = 19,
        BLINDNESS = 20,
        NIGHT_VISION = 21,
        HUNGER = 22,
        WEAKNESS = 23,
        POISON = 24,
        WITHER = 25,
        HEALTH = 26,
        ABSORPTION = 27,
        SATURATION = 28,
        POTION = 29,
        STATUS = 30;

    private static final int
        STOP = 0,
        AND = 1,
        OR = 2,
        NAND = 3,
        NOR = 4,
        XOR = 5,
        XNOR = 6;

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
        int statement = skill.getValue(CONDITION);

        // Prepare the embed data in case it needs to be used
        EmbedData embedData = new EmbedData(player, data, skill);
        skill.beginUsage();

        // Loop through each target
        boolean worked = false;
        for (LivingEntity t : targets) {

            // Initial values
            int targetCondition = statement;
            int operator = OR;
            boolean success = false;

            // Loop through each part of the statement
            do {

                // Grab the next condition
                int condition = targetCondition % CONDITIONS;
                targetCondition /= CONDITIONS;

                // Check the condition
                boolean passed = false;
                if (condition == STUN && data.getAPI().getStatusHolder(t).hasStatus(Status.STUN)) passed = true;
                else if (condition == ROOT && data.getAPI().getStatusHolder(t).hasStatus(Status.ROOT)) passed = true;
                else if (condition == INVINCIBLE && data.getAPI().getStatusHolder(t).hasStatus(Status.INVINCIBLE)) passed = true;
                else if (condition == ABSORB && data.getAPI().getStatusHolder(t).hasStatus(Status.ABSORB)) passed = true;
                else if (condition == SILENCE && data.getAPI().getStatusHolder(t).hasStatus(Status.SILENCE)) passed = true;
                else if (condition == DISARM && data.getAPI().getStatusHolder(t).hasStatus(Status.DISARM)) passed = true;
                else if (condition == CURSE && data.getAPI().getStatusHolder(t).hasStatus(Status.CURSE)) passed = true;
                else if (condition == FIRE && t.getFireTicks() > 0) passed = true;
                else if (condition == SPEED && t.hasPotionEffect(PotionEffectType.SPEED)) passed = true;
                else if (condition == SLOWNESS && t.hasPotionEffect(PotionEffectType.SLOW)) passed = true;
                else if (condition == HASTE && t.hasPotionEffect(PotionEffectType.FAST_DIGGING)) passed = true;
                else if (condition == FATIGUE && t.hasPotionEffect(PotionEffectType.SLOW_DIGGING)) passed = true;
                else if (condition == STRENGTH && t.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) passed = true;
                else if (condition == JUMP && t.hasPotionEffect(PotionEffectType.JUMP)) passed = true;
                else if (condition == NAUSEA && t.hasPotionEffect(PotionEffectType.CONFUSION)) passed = true;
                else if (condition == REGENERATION && t.hasPotionEffect(PotionEffectType.REGENERATION)) passed = true;
                else if (condition == RESISTANCE && t.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) passed = true;
                else if (condition == FIRE_RESISTANCE && t.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) passed = true;
                else if (condition == WATER_BREATHING && t.hasPotionEffect(PotionEffectType.WATER_BREATHING)) passed = true;
                else if (condition == INVISIBILITY && t.hasPotionEffect(PotionEffectType.INVISIBILITY)) passed = true;
                else if (condition == BLINDNESS && t.hasPotionEffect(PotionEffectType.BLINDNESS)) passed = true;
                else if (condition == NIGHT_VISION && t.hasPotionEffect(PotionEffectType.NIGHT_VISION)) passed = true;
                else if (condition == HUNGER && t.hasPotionEffect(PotionEffectType.HUNGER)) passed = true;
                else if (condition == WEAKNESS && t.hasPotionEffect(PotionEffectType.WEAKNESS)) passed = true;
                else if (condition == POISON && t.hasPotionEffect(PotionEffectType.POISON)) passed = true;
                else if (condition == WITHER && t.hasPotionEffect(PotionEffectType.WITHER)) passed = true;
                else if (BukkitHelper.isVerstionAtLeast(BukkitHelper.MC_1_6_2) && condition == HEALTH && t.hasPotionEffect(PotionEffectType.HEALTH_BOOST)) passed = true;
                else if (BukkitHelper.isVerstionAtLeast(BukkitHelper.MC_1_6_2) && condition == ABSORPTION && t.hasPotionEffect(PotionEffectType.ABSORPTION)) passed = true;
                else if (BukkitHelper.isVerstionAtLeast(BukkitHelper.MC_1_6_2) && condition == SATURATION && t.hasPotionEffect(PotionEffectType.SATURATION)) passed = true;
                else if (condition == POTION && t.getActivePotionEffects().size() > 0) passed = true;
                else if (condition == STATUS && data.getAPI().getStatusHolder(t).hasStatuses()) passed = true;

                // Operators
                if (operator == AND) success = success && passed;
                else if (operator == OR) success = success || passed;
                else if (operator == NAND) success = !(success && passed);
                else if (operator == NOR) success = !(success || passed);
                else if (operator == XOR) success = success != passed;
                else if (operator == XNOR) success = success == passed;

                // Grab the next operator
                operator = targetCondition % OPERATORS;
                targetCondition /= OPERATORS;
            }

            // Stop when the next operator is a "Stop" operator
            while (operator != STOP);

            // Apply the embedded effects if the condition passed
            if (success) {
                embedData.resolveNonTarget(t.getLocation());
                embedData.resolveTarget(t);
                worked = true;
            }
        }
        skill.stopUsage();

        return worked;
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
    }

    /**
     * @return names of the attributes used by the mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[0];
    }
}
