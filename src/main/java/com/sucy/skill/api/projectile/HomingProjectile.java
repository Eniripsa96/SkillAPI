package com.sucy.skill.api.projectile;

import com.sucy.skill.api.particle.target.EffectTarget;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

/**
 * SkillAPI © 2018
 * com.sucy.skill.api.projectile.HomingProjectile
 */
public class HomingProjectile extends CustomProjectile {
    private static final Vector UP = new Vector(0, 1, 0);

    private final CustomProjectile baseProjectile;
    private final EffectTarget target;
    private final double threshold;
    private final double angle;

    public HomingProjectile(final CustomProjectile baseProjectile, final EffectTarget target, final double angle) {
        super(baseProjectile.getShooter());
        this.baseProjectile = baseProjectile;
        this.target = target;
        this.angle = angle;
        this.threshold = Math.cos(angle);
    }

    @Override
    public Location getLocation() {
        return baseProjectile.getLocation();
    }

    @Override
    protected Event expire() {
        return baseProjectile.expire();
    }

    @Override
    protected Event land() {
        return baseProjectile.land();
    }

    @Override
    protected Event hit(final LivingEntity entity) {
        return baseProjectile.hit(entity);
    }

    @Override
    protected boolean landed() {
        return baseProjectile.landed();
    }

    @Override
    protected double getCollisionRadius() {
        return baseProjectile.getCollisionRadius();
    }

    @Override
    protected Vector getVelocity() {
        return baseProjectile.getVelocity();
    }

    @Override
    protected void setVelocity(Vector vel) {
        baseProjectile.setVelocity(vel);
    }

    @Override
    public void run() {
        final Vector vel = getVelocity();
        final double speed = vel.length();
        final Vector dir = vel.multiply(1 / speed);
        final Vector towards = target.getLocation().toVector().subtract(getLocation().toVector());
        final Vector targetDir = towards.normalize();
        final double dot = dir.dot(targetDir);
        if (dot >= threshold) {
            setVelocity(targetDir.multiply(speed));
        } else {
            final double diff = Math.acos(dot);
            final double t = angle / diff;
            final double sinAngle = Math.sin(diff);
            //final double m = Math.sin()
            //baseProjectile.setVelocity(result);
        }
    }
}
