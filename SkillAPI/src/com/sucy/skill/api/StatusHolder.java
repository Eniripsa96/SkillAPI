package com.sucy.skill.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Manager of an entity's statuses
 */
public class StatusHolder {

    private final HashMap<Status, Long> statuses = new HashMap<Status, Long>();
    private final ArrayList<DamageModifier> damageModifiers = new ArrayList<DamageModifier>();
    private final ArrayList<DamageModifier> defenseModifiers = new ArrayList<DamageModifier>();

    /**
     * Adds a status to the holder
     *
     * @param status   status of the holder
     * @param duration duration of the status in milliseconds
     */
    public void addStatus(Status status, long duration) {
        duration += System.currentTimeMillis();
        if (statuses.containsKey(status) && statuses.get(status) >= duration) return;
        statuses.put(status, duration);
    }

    /**
     * Adds a damage modifier to the holder
     *
     * @param modifier modifier
     */
    public void addDamageModifier(DamageModifier modifier) {
        damageModifiers.add(modifier);
    }

    /**
     * Adds a defense modifier to the holder
     *
     * @param modifier modifier
     */
    public void addDefenseModifier(DamageModifier modifier) {
        defenseModifiers.add(modifier);
    }

    /**
     * Checks if the holder has the status
     *
     * @param status status effect
     * @return       true if contains the status, false otherwise
     */
    public boolean hasStatus(Status status) {
        checkStatuses();
        return statuses.containsKey(status);
    }

    /**
     * Removes a status from the holder
     *
     * @param status status to remove
     */
    public void removeStatus(Status status) {
        statuses.remove(status);
    }

    /**
     * Gets the time left on a status applied to the holder
     *
     * @param status status to check
     * @return       time remaining on the status
     */
    public int getTimeLeft(Status status) {
        return statuses.containsKey(status) ? Math.max(0, (int) (statuses.get(status) - System.currentTimeMillis() + 999) / 1000) : 0;
    }

    /**
     * Checks statuses, removing any expired statuses
     */
    private void checkStatuses() {
        Set<Status> keys = statuses.keySet();
        for (Status status : keys) {
            if (statuses.get(status) < System.currentTimeMillis()) {
                statuses.remove(status);
            }
        }
    }

    /**
     * Modifies the amount of damage the holder does
     *
     * @param damage base damage dealt
     * @return       modified damage amount
     */
    public double modifyDamageDealt(double damage) {
        return modifyDamage(damageModifiers, damage);
    }

    /**
     * Modifies the amount of damage the holder does
     *
     * @param damage base damage dealt
     * @return       modified damage amount
     */
    public double modifyDamageTaken(double damage) {
        return modifyDamage(defenseModifiers, damage);
    }

    /**
     * Modifies a damage amount using modifiers
     *
     * @param modifiers damage modifiers
     * @param damage    initial damage
     * @return          modified damage
     */
    private double modifyDamage(ArrayList<DamageModifier> modifiers, double damage) {
        checkModifiers(modifiers);

        // Apply bonus damage first
        for (DamageModifier modifier : modifiers) {
            damage += modifier.getBonus();
        }

        // Apply multiplier after
        for (DamageModifier modifier : modifiers) {
            damage *= modifier.getMultiplier();
        }

        return damage;
    }

    /**
     * Checks if any damage modifiers are expired
     *
     * @param modifiers modifiers to check
     */
    private void checkModifiers(ArrayList<DamageModifier> modifiers) {
        for (int i = 0; i < modifiers.size(); i++) {
            if (modifiers.get(i).isExpired()) {
                modifiers.remove(i);
                i--;
            }
        }
    }
}
