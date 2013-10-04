package com.sucy.skill.api.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Helper class for finding the target of a player
 */
public class TargetHelper {

    /**
     * <p>Gets the entity the player is looking at</p>
     * <p>Has a little bit of tolerance to make targeting easier</p>
     *
     * @param player player to check
     * @param range  maximum range to check
     * @return       entity player is looking at or null if not found
     */
    public static LivingEntity getLivingTarget(Player player, int range) {
        List<Entity> list = player.getNearbyEntities(range, range, range);

        Vector facing = player.getLocation().getDirection();
        double fLengthSq = facing.lengthSquared();

        for (Entity entity : list) {
            if (!isInFront(player, entity) || !(entity instanceof LivingEntity)) continue;

            Vector relative = entity.getLocation().subtract(player.getLocation()).toVector();
            double dot = relative.dot(facing);
            double rLengthSq = relative.lengthSquared();
            double cosSquared = (dot * dot) / (rLengthSq + fLengthSq);
            double sinSquared = 1 - cosSquared;

            relative = player.getLocation().subtract(entity.getLocation()).toVector();
            double dSquared = relative.lengthSquared() * sinSquared;

            // If close enough to vision line, return the entity
            if (dSquared < 4) return (LivingEntity) entity;
        }

        return null;
    }

    /**
     * Checks if the entity is in front of the player
     *
     * @param player player to check for
     * @param target target to check against
     * @return       true if the target is in front of the player
     */
    public static boolean isInFront(Player player, Entity target) {

        // Get the necessary vectors
        Vector facing = player.getLocation().getDirection();
        Vector relative = target.getLocation().subtract(player.getLocation()).toVector();

        // If the dot product is positive, the target is in front
        return facing.dot(relative) >= 0;
    }
}
