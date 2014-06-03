package com.sucy.skill.api;

/**
 * A modifier for the amount of damage an entity takes or deals
 */
public class DamageModifier {

    private int bonus;
    private double multiplier;
    private long duration;

    /**
     * Constructor
     *
     * @param bonus    bonus damage (negative to reduce damage)
     * @param duration modifier duration in milliseconds
     */
    public DamageModifier(int bonus, long duration) {
        this(bonus, 1, duration);
    }

    /**
     * Constructor
     *
     * @param multiplier damage multiplier (less than one to reduce damage)
     * @param duration   modifier duration in milliseconds
     */
    public DamageModifier(double multiplier, long duration) {
        this (0, multiplier, duration);
    }

    /**
     * Constructor
     *
     * @param bonus      bonus damage (negative to reduce damage)
     * @param multiplier damage multiplier (less than one to reduce damage)
     * @param duration   modifier duration in milliseconds
     */
    public DamageModifier(int bonus, double multiplier, long duration) {
        this.bonus = bonus;
        this.multiplier = multiplier;
        this.duration = System.currentTimeMillis() + duration;
    }

    /**
     * @return whether or not the modifier is expired
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > duration;
    }

    /**
     * @return damage bonus
     */
    public int getBonus() {
        return bonus;
    }

    /**
     * @return damage multiplier
     */
    public double getMultiplier() {
        return multiplier;
    }
}
