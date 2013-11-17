package com.sucy.skill.api.util;

import java.lang.Long;import java.lang.Math;import java.lang.String;import java.lang.System;import java.util.HashMap;

/**
 * Helper class for managing cooldowns
 */
public class CooldownManager {

    private final HashMap<String, Long> timers = new HashMap<String, Long>();
    private int duration;

    /**
     * Constructor
     *
     * @param duration length of the cooldown in seconds
     */
    public CooldownManager(int duration) {
        this.duration = duration;
    }

    /**
     * Checks if the player is on cooldown
     *
     * @param player name of the player to check
     * @return       true if on cooldown, false otherwise
     */
    public boolean onCooldown(String player) {
        return timeLeft(player) > 0;
    }

    /**
     * Gets the time left on the cooldown for the player
     *
     * @param player name of the player to retrieve for
     * @return       time left on the cooldown in seconds
     */
    public int timeLeft(String player) {
        player = player.toLowerCase();
        if (!timers.containsKey(player)) {
            return 0;
        }

        return duration - (int)((System.currentTimeMillis() - timers.get(player)) / 1000);
    }

    /**
     * Starts the cooldown for the player
     *
     * @param player name of the player to start for
     */
    public void startCooldown(String player) {
        timers.put(player.toLowerCase(), System.currentTimeMillis());
    }

    /**
     * Clears the cooldown for the player
     *
     * @param player name of the player to clear for
     */
    public void clearCooldown(String player) {
        timers.remove(player.toLowerCase());
    }

    /**
     * @return duration of the cooldown in seconds
     */
    public int getDuration() {
        return Math.max(0, duration);
    }

    /**
     * Sets the duration for the cooldown
     *
     * @param value duration of the cooldown in seconds
     */
    public void setDuration(int value) {
        this.duration = value;
    }
}
