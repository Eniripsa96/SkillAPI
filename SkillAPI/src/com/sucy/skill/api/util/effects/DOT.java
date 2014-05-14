package com.sucy.skill.api.util.effects;

import com.rit.sucy.version.VersionManager;
import com.sucy.skill.api.skill.ClassSkill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * <p>Data for a DOT effect</p>
 * <p>This can be used for both damaging effects and healing effects</p>
 * <p>In order to create a healing effect, just provide a negative damage</p>
 */
public class DOT {

    private ClassSkill skill;
    private Player caster;
    private int ticksLeft;
    private int frequency;
    private int ticks;
    private double damage;
    private boolean lethal;

    /**
     * Constructor
     *
     * @param ticks     ticks left
     * @param damage    damage per tick
     * @param frequency time between ticks
     * @param lethal    lethal or not
     */
    public DOT(int ticks, double damage, int frequency, boolean lethal) {
        this(null, null, ticks, damage, frequency, lethal);
    }

    /**
     * Constructor
     *
     * @param skill     skill applying the DOT
     * @param caster    player casting the skill
     * @param ticks     ticks left
     * @param damage    damage per tick
     * @param frequency time between ticks
     * @param lethal    lethal or not
     */
    public DOT(ClassSkill skill, Player caster, int ticks, double damage, int frequency, boolean lethal) {
        this.skill = skill;
        this.caster = caster;
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
    public double getDamage() {
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
            if (skill != null) {
                skill.beginUsage();
            }
            if (caster != null) {
                VersionManager.damage(entity, caster, damage);
            }
            else VersionManager.damage(entity, damage);
            if (skill != null) {
                skill.stopUsage();
            }
        }

        // Healing
        else {

            if (skill != null) {
                skill.beginUsage();
            }

            VersionManager.heal(entity, -damage);

            if (skill != null) {
                skill.stopUsage();
            }
        }

        // Ends the effect if the entity is dead
        return ticks > 0 && !entity.isDead();
    }
}
