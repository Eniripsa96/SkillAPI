package com.sucy.skill.api.util.effects;

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
     * Launches a circle of arrows using the horizontal direction of the entity
     *
     * @param source         player to fire from
     * @param projectileType type of projectile to launch
     * @param amount         amount of projectiles to fire
     * @param angle          angle of the arc
     * @param speed          speed of the projectiles
     * @return               list of projectile entity IDs
     */
    public static List<Integer> launchHorizontalCircle(Player source, Class<? extends Projectile> projectileType, int amount, int angle, int speed) {
        List<Integer> list = new ArrayList<Integer>();

        vel = source.getLocation().getDirection();
        vel.setY(0);
        vel.multiply(speed / vel.length());

        // Fire one straight ahead if odd
        if (amount % 2 == 1) {
            Projectile projectile = source.launchProjectile(projectileType);
            projectile.setVelocity(vel);
            list.add(projectile.getEntityId());
        }

        double a = (double)angle / (amount - 1);
        for (int i = 0; i < (amount - 1) / 2; i++) {
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
                projectile.setVelocity(v);
                list.add(projectile.getEntityId());
            }
        }

        return list;
    }

    /**
     * Launches a circle of arrows using the direction of the entity
     *
     * @param source         player to fire from
     * @param projectileType type of projectile to launch
     * @param amount         amount of projectiles to fire
     * @param angle          angle of the arc
     * @param speed          speed of the projectiles
     * @return               list of projectile entity IDs
     */
    public static List<Integer> launchCircle(LivingEntity source, Class<? extends Projectile> projectileType, int amount, int angle, int speed) {
        List<Integer> list = new ArrayList<Integer>();

        // Fire one straight ahead if odd
        if (amount % 2 == 1) {
            Projectile projectile = source.launchProjectile(projectileType);
            projectile.setVelocity(projectile.getVelocity().multiply(speed / projectile.getVelocity().length()));
            list.add(projectile.getEntityId());
            if (amount == 1) return list;
        }

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
        for (int i = 0; i < (amount - 1) / 2; i++) {
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
                projectile.setVelocity(vel);
                list.add(projectile.getEntityId());
            }
        }

        return list;
    }
}
