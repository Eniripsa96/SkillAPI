package com.sucy.skill.api.event;

/**
 * <p>A type of attack, either melee or projectile</p>
 * <p>Used in the PlayerOnHitEvent and PlayerOnDamagedEvent</p>
 */
public enum AttackType {

    /**
     * A melee attack
     */
    MELEE,

    /**
     * A projectile attack
     */
    PROJECTILE
}
