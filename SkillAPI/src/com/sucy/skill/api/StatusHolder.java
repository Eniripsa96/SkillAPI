package com.sucy.skill.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * <p>Status data for an entity</p>
 * <p>Contains information for status effects and damage/defense modifiers</p>
 */
public class StatusHolder {

    private final HashMap<Status, Long> statuses = new HashMap<Status, Long>();
    private final ArrayList<DamageModifier> damageModifiers = new ArrayList<DamageModifier>();
    private final ArrayList<DamageModifier> defenseModifiers = new ArrayList<DamageModifier>();

    /**
     * <p>Applies a status to the holder</p>
     * <p>If a status is already applied that has a longer duration, this does nothing</p>
     * <p>If a status is already applied that has a shorter duration, this overwrites it</p>
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
     * <p>Adds a damage modifier to the holder</p>
     * <p>Multiple damage modifiers stack</p>
     *
     * @param modifier modifier
     */
    public void addDamageModifier(DamageModifier modifier) {
        damageModifiers.add(modifier);
    }

    /**
     * <p>Adds a defense modifier to the holder</p>
     * <p>Multiple defense modifiers stack</p>
     *
     * @param modifier modifier
     */
    public void addDefenseModifier(DamageModifier modifier) {
        defenseModifiers.add(modifier);
    }

    /**
     * <p>Checks if the holder has the status active on them</p>
     *
     * @param status status effect
     * @return       true if contains the status, false otherwise
     */
    public boolean hasStatus(Status status) {
        checkStatuses();
        return statuses.containsKey(status);
    }

    /**
     * <p>Removes a status from the holder</p>
     * <p>If they don't have the status, this does nothing</p>
     *
     * @param status status to remove
     */
    public void removeStatus(Status status) {
        statuses.remove(status);
    }

    /**
     * <p>Gets the time left of a status on the holder</p>
     * <p>If the status isn't applied, this returns 0</p>
     *
     * @param status status to check
     * @return       time remaining on the status
     */
    public int getTimeLeft(Status status) {
        return statuses.containsKey(status) ? Math.max(0, (int) (statuses.get(status) - System.currentTimeMillis() + 999) / 1000) : 0;
    }

    /**
     * <p>Checks the statuses applied to the holder, removing any expired statuses</p>
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
     * <p>Modifies the amount of damage dealt by the holder according to its damage modifiers</p>
     * <p>If the holder has no damage modifiers, this does nothing</p>
     *
     * @param damage base damage dealt
     * @return       modified damage amount
     */
    public double modifyDamageDealt(double damage) {
        return modifyDamage(damageModifiers, damage);
    }

    /**
     * <p>Modifies the amount of damage taken by the holder according to its defense modifiers</p>
     * <p>If the holder has no defense modifiers, this does nothing</p>
     *
     * @param damage base damage dealt
     * @return       modified damage amount
     */
    public double modifyDamageTaken(double damage) {
        return modifyDamage(defenseModifiers, damage);
    }

    /**
     * <p>Modifies a damage amount using the modifier list</p>
     * <p>If the list is empty, this does nothing</p>
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
     * <p>Checks if any modifiers in the list are expired, and removes them if they are</p>
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
