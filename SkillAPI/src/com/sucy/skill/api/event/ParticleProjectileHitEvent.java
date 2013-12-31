package com.sucy.skill.api.event;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.util.effects.ParticleProjectile;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a particle projectile hits an entity
 */
public class ParticleProjectileHitEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private ParticleProjectile projectile;
    private LivingEntity entity;

    /**
     * Constructor
     *
     * @param projectile projectile that hit the entity
     * @param entity     entity that was hit
     */
    public ParticleProjectileHitEvent(ParticleProjectile projectile, LivingEntity entity) {
        this.projectile = projectile;
        this.entity = entity;
    }

    /**
     * @return data of the player unlocking the skill
     */
    public ParticleProjectile getProjectile() {
        return projectile;
    }

    /**
     * @return skill that was unlocked
     */
    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * @return gets the handlers for the event
     */
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return gets the handlers for the event
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
