package com.sucy.skill.api.util.effects;

import com.sucy.skill.api.dynamic.EmbedData;
import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for launching projectiles in special ways
 */
public class ProjectileHelper {

    private static final Vector X_VEC = new Vector(1, 0, 0);
    private static final double DEGREE_TO_RAD = Math.PI / 180;
    private static Vector vel = new Vector();

    /**
     * Launches a circle of projectiles using the horizontal direction of the entity
     *
     * @param source         player to fire from
     * @param projectileType type of projectile to launch
     * @param amount         amount of projectiles to fire
     * @param angle          angle of the arc
     * @param speed          speed of the projectiles
     * @return               list of projectile entity IDs
     */
    public static List<Projectile> launchHorizontalCircle(Player source, Class<? extends Projectile> projectileType, int amount, int angle, double speed) {
        List<Projectile> list = new ArrayList<Projectile>();

        vel = source.getLocation().getDirection();
        vel.setY(0);
        vel.multiply(speed / vel.length());

        // Fire one straight ahead if odd
        if (amount % 2 == 1) {
            Projectile projectile = source.launchProjectile(projectileType);
            if (projectile instanceof Fireball) {
                ((Fireball)projectile).setDirection(vel);
            }
            else projectile.setVelocity(vel);
            list.add(projectile);
            amount--;
        }

        if (amount <= 0) return list;

        double a = (double)angle / (amount - 1);
        for (int i = 0; i < amount / 2; i++) {
            for (int j = -1; j <= 1; j += 2) {

                // Initial calculations
                double b = angle / 2 * j - a * i * j;
                double cos = Math.cos(b * DEGREE_TO_RAD);
                double sin = Math.sin(b * DEGREE_TO_RAD);

                // Get the velocity
                Vector v = vel.clone();
                v.setX(v.getX() * cos - v.getZ() * sin);
                v.setZ(v.getX() * sin + v.getZ() * cos);

                // Launch the projectile
                Projectile projectile = source.launchProjectile(projectileType);
                if (projectile instanceof Fireball) {
                    ((Fireball)projectile).setDirection(v);
                }
                else projectile.setVelocity(v);
                list.add(projectile);
            }
        }

        return list;
    }

    /**
     * Launches a circle of projectiles using the direction of the entity
     *
     * @param source         player to fire from
     * @param projectileType type of projectile to launch
     * @param amount         amount of projectiles to fire
     * @param angle          angle of the arc
     * @param speed          speed of the projectiles
     * @return               list of projectile entity IDs
     */
    public static List<Projectile> launchCircle(LivingEntity source, Class<? extends Projectile> projectileType, int amount, int angle, double speed) {
        List<Projectile> list = new ArrayList<Projectile>();

        // Fire one straight ahead if odd
        if (amount % 2 == 1) {
            Projectile projectile = source.launchProjectile(projectileType);
            Vector vel = projectile.getVelocity().multiply(speed / projectile.getVelocity().length());
            if (projectile instanceof Fireball) {
                ((Fireball)projectile).setDirection(vel);
            }
            else projectile.setVelocity(vel);
            list.add(projectile);
            amount--;
        }

        if (amount <= 0) return list;

        // Get the base velocity
        Vector base = source.getLocation().getDirection();
        base.setY(0);
        vel.setX(speed);
        vel.setY(0);
        vel.setZ(0);

        // Get the vertical angle
        double vBaseAngle = base.angle(source.getLocation().getDirection());
        if (source.getLocation().getDirection().getY() < 0) vBaseAngle = -vBaseAngle;
        double hAngle = base.angle(X_VEC) / DEGREE_TO_RAD;
        if (base.getZ() < 0) hAngle = -hAngle;

        double angleIncrement = (double)angle / (amount - 1);
        for (int i = 0; i < amount / 2; i++) {
            for (int direction = -1; direction <= 1; direction += 2) {

                // Initial calculations
                double bonusAngle = angle / 2 * direction - angleIncrement * i * direction;
                double totalAngle = hAngle + bonusAngle;
                double vAngle = vBaseAngle * Math.cos(bonusAngle * DEGREE_TO_RAD);
                double x = Math.cos(vAngle);

                // Get the velocity
                vel.setX(x *  Math.cos(totalAngle * DEGREE_TO_RAD) * speed);
                vel.setY(Math.sin(vAngle) * speed);
                vel.setZ(x * Math.sin(totalAngle * DEGREE_TO_RAD) * speed);

                // Launch the projectile
                Projectile projectile = source.launchProjectile(projectileType);
                if (projectile instanceof Fireball) {
                    ((Fireball)projectile).setDirection(vel);
                }
                else projectile.setVelocity(vel);
                list.add(projectile);
            }
        }

        return list;
    }

    /**
     * Rains a group of projectiles down on a target location
     *
     * @param source     entity to fire the projectiles
     * @param target     target location
     * @param projectile type of projectile to launch
     * @param amount     amount of projectiles to launch
     * @param height     height above the target location to launch them from
     * @param radius     radius of the rain
     * @param speed      speed of the projectiles
     * @return           list of projectile entity IDs
     */
    public static List<Projectile> rainProjectiles(LivingEntity source, Location target, Class<? extends Projectile> projectile, int amount, int height, double radius, int speed) {

        // Initialize data
        List<Projectile> list = new ArrayList<Projectile>();
        if (amount <= 0) return list;
        target.add(0, height, 0);
        Vector vel = new Vector(0, -speed, 0);

        // Fire the one in the center
        Projectile p = source.launchProjectile(projectile);
        p.teleport(target);
        if (p instanceof Fireball) {
            ((Fireball)p).setDirection(vel);
        }
        else p.setVelocity(vel);
        list.add(p);
        amount--;

        // Launch projectiles
        int tiers = (amount + 7) / 8;
        for (int i = 0; i < tiers; i++) {
            double rad = radius * (tiers - i) / tiers;
            int tierNum = Math.min(amount, 8);
            double increment = 360 / tierNum;
            double angle = (i % 2) * 22.5;
            for (int j = 0; j < tierNum; j++) {
                double dx = Math.cos(angle) * rad;
                double dz = Math.sin(angle) * rad;
                Location loc = target.clone();
                loc.add(dx, 0, dz);
                p = source.launchProjectile(projectile);
                p.teleport(loc);
                if (p instanceof Fireball) {
                    ((Fireball)p).setDirection(vel);
                }
                else p.setVelocity(vel);
                list.add(p);
                angle += increment;
            }
            amount -= tierNum;
        }

        return list;
    }

    /**
     * Launches a circle of particle projectiles using the horizontal direction of the entity
     *
     * @param source   player to fire from
     * @param particle type of particle to launch
     * @param data     data value for the particle
     * @param amount   amount of projectiles to fire
     * @param angle    angle of the arc
     * @param speed    speed of the projectiles
     * @param damage   damage the projectile will deal
     */
    public static void launchHorizontalCircle(Player source, ParticleType particle, int data, int amount, int angle, double speed, double damage) {
        launchHorizontalCircle(source, particle, data, amount, angle, speed, damage, null);
    }

    /**
     * Launches a circle of particle projectiles using the direction of the entity
     *
     * @param source   player to fire from
     * @param particle type of particle to display
     * @param data     data value for the particle
     * @param amount   amount of projectiles to fire
     * @param angle    angle of the arc
     * @param speed    speed of the projectiles
     * @param damage   damage the projectile will deal
     */
    public static void launchCircle(LivingEntity source, ParticleType particle, int data, int amount, int angle, double speed, double damage) {
        launchCircle(source, particle, data, amount, angle, speed, damage, null);
    }

    /**
     * Rains a group of particle projectiles down on a target location
     *
     * @param source   entity to fire the projectiles
     * @param target   target location
     * @param particle particle to display
     * @param data     data value for the projectile
     * @param amount   amount of projectiles to launch
     * @param height   height above the target location to launch them from
     * @param radius   radius of the rain
     * @param speed    speed of the projectiles
     * @param damage   damage that the projectile will deal
     */
    public static void rainProjectiles(LivingEntity source, Location target, ParticleType particle, int data, int amount, int height, double radius, double speed, double damage) {
        rainProjectiles(source, target, particle, data, amount, height, radius, speed, damage, null);
    }

    /**
     * Launches a circle of particle projectiles using the horizontal direction of the entity
     *
     * @param source   player to fire from
     * @param particle type of particle to launch
     * @param data     data value for the particle
     * @param amount   amount of projectiles to fire
     * @param angle    angle of the arc
     * @param speed    speed of the projectiles
     * @param damage   damage the projectile will deal
     * @param embed    embed data to attack to the projectile for a dynamic skill
     */
    public static void launchHorizontalCircle(Player source, ParticleType particle, int data, int amount, int angle, double speed, double damage, EmbedData embed) {

        vel = source.getLocation().getDirection();
        vel.setY(0);
        vel.multiply(speed / vel.length());

        // Fire one straight ahead if odd
        if (amount % 2 == 1) {
            ParticleProjectile p = new ParticleProjectile(source, source.getLocation().add(0, 1, 0), vel, particle, data, 2, 0);
            p.embed(embed);
            amount--;
        }

        if (amount <= 0) return;

        double a = (double)angle / (amount - 1);
        for (int i = 0; i < amount / 2; i++) {
            for (int j = -1; j <= 1; j += 2) {

                // Initial calculations
                double b = angle / 2 * j - a * i * j;
                double cos = Math.cos(b * DEGREE_TO_RAD);
                double sin = Math.sin(b * DEGREE_TO_RAD);

                // Get the velocity
                Vector v = vel.clone();
                v.setX(v.getX() * cos - v.getZ() * sin);
                v.setZ(v.getX() * sin + v.getZ() * cos);

                // Launch the projectile
                ParticleProjectile p = new ParticleProjectile(source, source.getLocation().add(0, 1, 0), v, particle, data, 2, damage);
                p.embed(embed);
            }
        }
    }

    /**
     * Launches a circle of particle projectiles using the direction of the entity
     *
     * @param source   player to fire from
     * @param particle type of particle to display
     * @param data     data value for the particle
     * @param amount   amount of projectiles to fire
     * @param angle    angle of the arc
     * @param speed    speed of the projectiles
     * @param damage   damage the projectile will deal
     * @param embed    embed data to attack to the projectile for a dynamic skill
     */
    public static void launchCircle(LivingEntity source, ParticleType particle, int data, int amount, int angle, double speed, double damage, EmbedData embed) {

        // Fire one straight ahead if odd
        if (amount % 2 == 1) {
            ParticleProjectile p = ParticleProjectile.launch(source, speed, particle, data, 0);
            p.embed(embed);
            amount--;
        }

        if (amount <= 0) return;

        // Get the base velocity
        Vector base = source.getLocation().getDirection();
        base.setY(0);
        vel.setX(speed);
        vel.setY(0);
        vel.setZ(0);

        // Get the vertical angle
        double vBaseAngle = base.angle(source.getLocation().getDirection());
        if (source.getLocation().getDirection().getY() < 0) vBaseAngle = -vBaseAngle;
        double hAngle = base.angle(X_VEC) / DEGREE_TO_RAD;
        if (base.getZ() < 0) hAngle = -hAngle;

        double angleIncrement = (double)angle / (amount - 1);
        for (int i = 0; i < amount / 2; i++) {
            for (int direction = -1; direction <= 1; direction += 2) {

                // Initial calculations
                double bonusAngle = angle / 2 * direction - angleIncrement * i * direction;
                double totalAngle = hAngle + bonusAngle;
                double vAngle = vBaseAngle * Math.cos(bonusAngle * DEGREE_TO_RAD);
                double x = Math.cos(vAngle);

                // Get the velocity
                vel.setX(x *  Math.cos(totalAngle * DEGREE_TO_RAD) * speed);
                vel.setY(Math.sin(vAngle) * speed);
                vel.setZ(x * Math.sin(totalAngle * DEGREE_TO_RAD) * speed);

                // Launch the projectile
                ParticleProjectile p = new ParticleProjectile(source, source.getLocation().add(0, 1, 0), vel, particle, data, 2, damage);
                p.embed(embed);
            }
        }
    }

    /**
     * Rains a group of particle projectiles down on a target location
     *
     * @param source   entity to fire the projectiles
     * @param target   target location
     * @param particle particle to display
     * @param data     data value for the projectile
     * @param amount   amount of projectiles to launch
     * @param height   height above the target location to launch them from
     * @param radius   radius of the rain
     * @param speed    speed of the projectiles
     * @param damage   damage that the projectile will deal
     * @param embed    embed data to attack to the projectile for a dynamic skill
     */
    public static void rainProjectiles(LivingEntity source, Location target, ParticleType particle, int data, int amount, int height, double radius, double speed, double damage, EmbedData embed) {

        // Initialize data
        if (amount <= 0) return;
        target.add(0, height, 0);
        Vector vel = new Vector(0, -speed, 0);

        // Fire the one in the center
        ParticleProjectile p = new ParticleProjectile(source, source.getLocation().add(0, 1, 0), vel, particle, data, 2, 0);
        p.teleport(target);
        p.embed(embed);
        amount--;

        // Launch projectiles
        int tiers = (amount + 7) / 8;
        for (int i = 0; i < tiers; i++) {
            double rad = radius * (tiers - i) / tiers;
            int tierNum = Math.min(amount, 8);
            double increment = 360 / tierNum;
            double angle = (i % 2) * 22.5;
            for (int j = 0; j < tierNum; j++) {
                double dx = Math.cos(angle) * rad;
                double dz = Math.sin(angle) * rad;
                Location loc = target.clone();
                loc.add(dx, 0, dz);
                p = new ParticleProjectile(source, source.getLocation().add(0, 1, 0), vel, particle, data, 2, damage);
                p.teleport(loc);
                p.embed(embed);
                angle += increment;
            }
            amount -= tierNum;
        }
    }
}
