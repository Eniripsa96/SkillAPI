package com.sucy.skill.api.util.effects;

import org.bukkit.Effect;

/**
 * Types of particles supported by the API
 */
public enum ParticleType {

    /**
     * An ender eye signal
     */
    ENDER_SIGNAL (Effect.ENDER_SIGNAL),

    /**
     * The flames seen on a mobspawner
     */
    MOBSPAWNER_FLAMES (Effect.MOBSPAWNER_FLAMES),

    /**
     * Smoke effect
     */
    SMOKE (Effect.SMOKE),
    ;

    private final Effect effect;

    /**
     * Enum constructor
     * @param effect
     */
    private ParticleType(Effect effect) {
        this.effect = effect;
    }

    /**
     * @return bukkit effect for displaying the particle
     */
    public Effect getEffect() {
        return effect;
    }
}
