package com.sucy.skill.api.util;

import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Helper method for checking whether or not an entity is in combat
 */
public class Combat
{
    private static HashMap<String, Long> timers = new HashMap<String, Long>();

    /**
     * Updates the combat status of the player
     *
     * @param player player to mark as starting combat
     */
    public static void applyCombat(Player player)
    {
        timers.put(player.getName(), System.currentTimeMillis());
    }

    /**
     * Clears the combat timer data for the given player
     *
     * @param player player to clear for
     */
    public static void clearData(Player player)
    {
        timers.remove(player.getName());
    }

    /**
     * Checks whether or not the player is in combat or not
     *
     * @param player  player to check for
     * @param seconds seconds before being counted as in combat
     *
     * @return true if in combat, false otherwise
     */
    public static boolean isInCombat(Player player, double seconds)
    {
        return timers.containsKey(player.getName()) && System.currentTimeMillis() - timers.get(player.getName()) < seconds * 1000;
    }

    /**
     * Checks whether or not the player is out of combat or not
     *
     * @param player  player to check for
     * @param seconds seconds before being counted as out of combat
     *
     * @return true if out of combat, false otherwise
     */
    public static boolean isOutOfCombat(Player player, double seconds)
    {
        return !isInCombat(player, seconds);
    }
}
