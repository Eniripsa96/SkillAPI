package com.sucy.skill.api.util.effects;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * <p>A temporary effect that expires after an amount of time</p>
 */
public abstract class TimedEffect extends BukkitRunnable {

    private JavaPlugin plugin;
    private boolean running;
    private int ticks;

    /**
     * Constructor
     *
     * @param ticks ticks to run for
     */
    public TimedEffect(int ticks) {
        plugin = (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin("SkillAPI");
        this.ticks = ticks;
        running = false;
    }

    /**
     * Constructor
     *
     * @param plugin plugin reference
     * @param ticks  ticks to run for
     */
    public TimedEffect(JavaPlugin plugin, int ticks) {
        this.plugin = plugin;
        this.ticks = ticks;
        running = false;
    }

    /**
     * <p>Retrieves the number of ticks the effect lasts for</p>
     * <p>This may not be the duration of the active effect,
     * just the last duration set to this effect.</p>
     *
     * @return effect duration in ticks
     */
    public int getTicks() {
        return ticks;
    }

    /**
     * <p>Sets a new duration for the effect to last</p>
     * <p>This will not change the duration of any currently
     * running task. Using start() after this method will
     * use the new amount of ticks however.</p>
     *
     * @param ticks number of ticks to last
     */
    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    /**
     * <p>Starts the effect timer</p>
     * <p>If the timer is already running, this
     * resets it back to the original time.</p>
     */
    public void start() {
        if (running) {
            cancel();
            runTaskLater(plugin, ticks);
        }
        else {
            setup();
            runTaskLater(plugin, ticks);
        }
        running = true;
    }

    /**
     * <p>Ends the timed effect prematurely, clearing all effects</p>
     * <p>If the effect already wasn't active, this will do nothing</p>
     */
    public void stop() {
        if (running) {
            cancel();
            clear();
        }
    }

    /**
     * <p>Sets up the effect</p>
     * <p>This is called when the effect begins</p>
     * <p>If the effect is reapplied before a previous
     * application expired, this will not be called for
     * the new application.</p>
     */
    protected abstract void setup();

    /**
     * <p>Clears the effect</p>
     * <p>This is called when the effect expires</p>
     * <p>If the effect is reapplied before expiring, this
     * will not get called until the new application expires.</p>
     */
    protected abstract void clear();

    /**
     * <p>Clears the effect when the time runs out</p>
     */
    @Override
    public void run() {
        running = false;
        clear();
    }
}
