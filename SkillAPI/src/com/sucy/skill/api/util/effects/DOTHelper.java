package com.sucy.skill.api.util.effects;

import com.sucy.skill.SkillAPI;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

/**
 * Utility class for applying DOTs to targets
 */
public class DOTHelper implements Listener {

    private final HashMap<Integer, DOTSet> effects = new HashMap<Integer, DOTSet>();
    private final SkillAPI api;

    /**
     * Constructor
     *
     * @param api API reference
     */
    public DOTHelper(SkillAPI api) {
        this.api = api;
        api.getServer().getPluginManager().registerEvents(this, api);
    }

    /**
     * Retrieves the DOT Set for an entity
     *
     * @param target target of the DOT
     * @return       DOT Set for the entity
     */
    public DOTSet getDOTSet(LivingEntity target) {
        if (!effects.containsKey(target.getEntityId())) {
            effects.put(target.getEntityId(), new DOTSet(api, target));
        }

        return effects.get(target.getEntityId());
    }

    /**
     * Removes DOT effects on death
     *
     * @param event event details
     */
    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        int id = event.getEntity().getEntityId();
        if (effects.containsKey(id)) {
            DOTSet set = effects.remove(id);
            set.cancel();
        }
    }

    /**
     * Removes DOT effects on quit
     *
     * @param event event details
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        int id = event.getPlayer().getEntityId();
        if (effects.containsKey(id)) {
            DOTSet set = effects.remove(id);
            set.cancel();
        }
    }
}
