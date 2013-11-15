package com.sucy.skill.api.util.effects;

import org.bukkit.Effect;
import org.bukkit.Location;

import java.util.Random;

/**
 * Utility class for doing particle effects
 */
public class ParticleHelper {

    private static final Random random = new Random();

    /**
     * Randomly plays particle effects within the circle
     *
     * @param loc       location to center the effect around
     * @param type      type of particle to use
     * @param radius    radius of the circle
     * @param amount    amount of particles to use
     * @param direction direction to orientate the circle
     */
    public static void fillCircle(Location loc, ParticleType type, int radius, int amount, Direction direction) {
        fillCircle(loc, type, radius, 0, amount, direction);
    }

    /**
     * Randomly plays particle effects within the circle
     *
     * @param loc       location to center the effect around
     * @param type      type of particle to use
     * @param data      data value to use
     * @param radius    radius of the circle
     * @param amount    amount of particles to use
     * @param direction direction to orientate the circle
     */
    public static void fillCircle(Location loc, ParticleType type, int data, int radius, int amount, Direction direction) {
        Location temp = loc.clone();
        int rSquared = radius * radius;
        int twoRadius = radius * 2;
        int index = 0;

        // Play the particles
        while (index < amount) {
            if (direction == Direction.XY || direction == Direction.XZ)
                temp.setX(loc.getX() + random.nextFloat() * twoRadius - radius);
            if (direction == Direction.XY || direction == Direction.YZ)
                temp.setY(loc.getY() + random.nextFloat() * twoRadius - radius);
            if (direction == Direction.XZ || direction == Direction.YZ)
                temp.setZ(loc.getZ() + random.nextFloat() * twoRadius - radius);

            if (temp.distanceSquared(loc) > rSquared) continue;

            loc.getWorld().playEffect(temp, type.getEffect(), 0);
            index++;
        }
    }

    /**
     * Randomly plays particle effects within the sphere
     *
     * @param loc       location to center the effect around
     * @param type      type of particle to use
     * @param radius    radius of the sphere
     * @param amount    amount of particles to use
     */
    public static void fillSphere(Location loc, ParticleType type, int radius, int amount) {
        fillSphere(loc, type, 0, radius, amount);
    }

    /**
     * Randomly plays particle effects within the sphere
     *
     * @param loc       location to center the effect around
     * @param type      type of particle to use
     * @param data      data value to use
     * @param radius    radius of the sphere
     * @param amount    amount of particles to use
     */
    public static void fillSphere(Location loc, ParticleType type, int data, int radius, int amount) {
        Location temp = loc.clone();
        int rSquared = radius * radius;
        int twoRadius = radius * 2;
        int index = 0;

        // Play the particles
        while (index < amount) {
            temp.setX(loc.getX() + random.nextFloat() * twoRadius - radius);
            temp.setY(loc.getY() + random.nextFloat() * twoRadius - radius);
            temp.setZ(loc.getZ() + random.nextFloat() * twoRadius - radius);

            if (temp.distanceSquared(loc) > rSquared) continue;

            loc.getWorld().playEffect(temp, type.getEffect(), 0);
            index++;
        }
    }

    /**
     * Randomly plays particle effects within a hemisphere
     *
     * @param loc    location to center the effect around
     * @param type   type of particle to use
     * @param radius radius of the hemisphere
     * @param amount amount of particles to use
     */
    public static void fillHemisphere(Location loc, ParticleType type, int radius, int amount) {
        fillHemisphere(loc, type, 0, radius, amount);
    }

    /**
     * Randomly plays particle effects within a hemisphere
     *
     * @param loc    location to center the effect around
     * @param type   type of particle to use
     * @param data   data value to use
     * @param radius radius of the hemisphere
     * @param amount amount of particles to use
     */
    public static void fillHemisphere(Location loc, ParticleType type, int data, int radius, int amount) {
        Location temp = loc.clone();
        int rSquared = radius * radius;
        int twoRadius = radius * 2;
        int index = 0;

        // Play the particles
        while (index < amount) {
            temp.setX(loc.getX() + random.nextFloat() * twoRadius - radius);
            temp.setY(loc.getY() + random.nextFloat() * radius);
            temp.setZ(loc.getZ() + random.nextFloat() * twoRadius - radius);

            if (temp.distanceSquared(loc) > rSquared) continue;

            loc.getWorld().playEffect(temp, type.getEffect(), 0);
            index++;
        }
    }
}
