package com.sucy.skill.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.version.VersionManager;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import com.sucy.skill.api.util.effects.TimedEffect;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;

/**
 * Mechanic for healing all targets
 */
public class HealthMechanic implements IMechanic, Listener {

    private static final String
            HEALTH = "Health",
            DURATION = "Health Duration";

    private HashMap<String, HealthEffect> playerBonuses = new HashMap<String, HealthEffect>();
    private HashMap<Integer, HealthEffect> mobBonuses = new HashMap<Integer, HealthEffect>();

    /**
     * Grants bonus health to all targets
     *
     * @param player  player using the skill
     * @param data    data of the player using the skill
     * @param skill   skill being used
     * @param target  target type of the skill
     * @param targets targets for the effects
     * @return        true if there were targets, false otherwise
     */
    @Override
    public boolean resolve(Player player, PlayerSkills data, DynamicSkill skill, Target target, List<LivingEntity> targets) {

        // Grant health to all targets
        int level = data.getSkillLevel(skill.getName());
        int amount = (int)skill.getAttribute(HEALTH, target, level);
        int duration = (int)(skill.getAttribute(DURATION, level) * 20);
        for (LivingEntity t : targets) {

            HealthEffect effect = new HealthEffect(skill.getAPI(), t, duration, amount);
            effect.start();

            // Players
            if (t instanceof Player) {
                Player p = (Player)t;
                if (playerBonuses.containsKey(p.getName())) {
                    playerBonuses.get(p.getName()).stop();
                }
                playerBonuses.put(p.getName(), effect);
            }

            // Non-players
            else {
                if (mobBonuses.containsKey(t.getEntityId())) {
                    mobBonuses.get(t.getEntityId()).stop();
                }
                mobBonuses.put(t.getEntityId(), effect);
            }
        }

        return true;
    }

    /**
     * Sets default attributes for the skill
     *
     * @param skill  skill to apply to
     * @param prefix prefix to add to the name
     */
    @Override
    public void applyDefaults(DynamicSkill skill, String prefix) {
        skill.checkDefault(prefix + HEALTH, 5, 2);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] {HEALTH};
    }

    /**
     * Clears health effects when a player quits
     *
     * @param event event details
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (playerBonuses.containsKey(event.getPlayer().getName())) {
            playerBonuses.get(event.getPlayer().getName()).stop();
        }
    }

    /**
     * Clears all health effects
     */
    public void clear() {
        for (HealthEffect effect : playerBonuses.values()) {
            effect.stop();
        }
    }

    /**
     * Health task for resetting entity health
     */
    private class HealthEffect extends TimedEffect {

        private SkillAPI api;
        private LivingEntity entity;
        private double bonus;

        /**
         * Constructor
         *
         * @param entity entity to apply to
         * @param ticks  number of ticks to last
         * @param bonus  amount of health to give
         */
        public HealthEffect(SkillAPI plugin, LivingEntity entity, int ticks, double bonus) {
            super(ticks);
            this.api = plugin;
            this.entity = entity;
            this.bonus = bonus;
        }

        /**
         * Gives health to the entity
         */
        @Override
        protected void setup() {
            if (entity instanceof Player) {
                api.getPlayer((Player) entity).addMaxHealth((int)bonus);
            }
            else {
                double maxHealth = entity.getMaxHealth() + bonus;
                if (maxHealth < 1) {
                    maxHealth = 1;
                    bonus = maxHealth - entity.getMaxHealth();
                }
                VersionManager.setMaxHealth(entity, maxHealth);
            }
        }

        /**
         * Removes the health bonus for the entity
         */
        @Override
        protected void clear() {
            if (entity instanceof Player) {
                api.getPlayer((Player) entity).addMaxHealth(-(int)bonus);
                playerBonuses.remove(((Player) entity).getName());
            }
            else {
                VersionManager.setMaxHealth(entity, entity.getMaxHealth() - bonus);
                mobBonuses.remove(entity.getEntityId());
            }
            entity = null;
        }
    }
}
