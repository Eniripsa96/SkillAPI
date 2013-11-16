package com.sucy.skill.api.util.effects;

import com.sucy.skill.SkillAPI;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * A set of DOTs applied to an entity
 */
public class DOTSet extends BukkitRunnable {

    private final HashMap<String, DOT> effects = new HashMap<String, DOT>();
    private final LivingEntity target;

    /**
     * Constructor
     *
     * @param api    API Reference
     * @param target target of the DOT set
     */
    public DOTSet(SkillAPI api, LivingEntity target) {
        runTaskTimer(api, 20, 20);
        this.target = target;
    }

    /**
     * <p>Adds a new effect to the set</p>
     * <p>If an effect with the key already exists, it is replaced</p>
     *
     * @param key effect key
     * @param dot DOT details
     */
    public void addEffect(String key, DOT dot) {
        effects.put(key, dot);
    }

    /**
     * Clears all DOT effects on the entity
     */
    public void clearEffects() {
        effects.clear();
    }

    /**
     * @return true if the set contains active effects, false otherwise
     */
    public boolean hasEffects() {
        return effects.size() > 0;
    }

    /**
     * Clears the DOT effect using a certain key
     *
     * @param key DOT key
     */
    public void clearEffect(String key) {
        if (effects.containsKey(key)) {
            effects.remove(key);
        }
    }

    /**
     * Retrieves the DOT effect using the given key
     *
     * @param key DOT key
     * @return    DOT effect using the key or null if not found
     */
    public DOT getEffect(String key) {
        return effects.get(key);
    }

    /**
     * Runs the effect
     */
    @Override
    public void run() {
        for (Map.Entry<String, DOT> entry : effects.entrySet()) {
            DOT dot = entry.getValue();
            boolean running = dot.apply(target);
            if (!running) effects.remove(entry.getKey());
        }
    }
}
