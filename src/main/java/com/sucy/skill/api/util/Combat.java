/**
 * SkillAPI
 * com.sucy.skill.api.util.Combat
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
