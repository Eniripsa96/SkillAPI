package com.sucy.skill.api.util.effects;

import org.bukkit.entity.LivingEntity;

/**
 * <p>Data for a DOT effect</p>
 * <p>This can be used for both damaging effects and healing effects</p>
 * <p>In order to create a healing effect, just provide a negative damage</p>
 */
public class DOT {

    private int ticksLeft;
    private int frequency;
    private int ticks;
    private int damage;
    private boolean lethal;

    /**
     * Constructor
     *
     * @param ticks     ticks left
     * @param damage    damage per tick
     * @param lethal    lethal or not
     * @param frequency time between ticks
     */
    public DOT(int ticks, int damage, int frequency, boolean lethal) {
        this.ticks = ticks;
        this.damage = damage;
        this.lethal = lethal;
        this.frequency = frequency;
        this.ticksLeft = frequency;
    }

    /**
     * @return the remaining duration of the effect
     */
    public int getDuration() {
        return ticks;
    }

    /**
     * @return damage dealt per tick
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @return true if lethal damage, false if non-lethal
     */
    public boolean isLethal() {
        return lethal;
    }

    /**
     * Damages the entity
     *
     * @param entity entity to damage
     * @return       true if the effect continues, false otherwise
     */
    public boolean apply(LivingEntity entity) {

        // Must still be a valid target
        if (!entity.isValid()) return false;

        // Decrement tick counters
        ticksLeft--;
        ticks--;
        if (ticksLeft > 0) return ticks > 0;

        // Refresh the ticks left
        ticksLeft = frequency;

        // Dealing damage
        if (damage > 0) {

            // Non-lethal damage can't deal the same or more than the entity health
            if (entity.getHealth() <= damage && !lethal) {
                damage = (int)entity.getHealth() - 1;
            }

            // If not dealing damage, the effect has ended
            if (damage <= 0) return false;

            // Damage the entity
            entity.damage(damage);
        }

        // Healing
        else {

            // Cannot go above the enemy health
            double health = entity.getHealth() - damage;
            health = Math.min(entity.getMaxHealth(), health);

            // Heal the entity
            entity.setHealth(health);
        }

        // Ends the effect if the entity is dead
        return ticks > 0 && !entity.isDead();
    }
}
