package com.sucy.skill.api.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for determining what a player can attack
 */
public class Protection {

    /**
     * Checks if a player can be PvPed
     *
     * @param attacker player attacking the other
     * @param target   player being attacked
     * @return         true if the attack is allowed
     */
    public static boolean canAttack(Player attacker, LivingEntity target) {
        if (target instanceof Tameable) {
            Tameable entity = (Tameable)target;
            if (entity.isTamed() && entity.getOwner().getName().equals(attacker.getName())) {
                return false;
            }
        }
        if (attacker == target) return false;
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, target, EntityDamageEvent.DamageCause.CUSTOM, 1);
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled() && event.getDamage() > 0;
    }

    /**
     * Checks if the target is an ally
     *
     * @param attacker player attacking
     * @param target   ally of the player
     * @return         true if ally, false otherwise
     */
    public static boolean isAlly(Player attacker, LivingEntity target) {
        return !canAttack(attacker, target);
    }

    /**
     * Retrieves all living entities the player can attack from the list
     *
     * @param attacker player that is attacking
     * @param targets  targets the player is trying to attack
     * @return         list of targets the player can attack
     */
    public static List<LivingEntity> canAttack(Player attacker, List<LivingEntity> targets) {
        List<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity entity : targets) {
            if (canAttack(attacker, entity)) {
                list.add(entity);
            }
        }
        return list;
    }

    /**
     * Retrieves all living entities the player cannot attack from the list
     *
     * @param attacker player that is attacking
     * @param targets  targets the player is trying to attack
     * @return         list of targets the player cannot attack
     */
    public static List<LivingEntity> cannotAttack(Player attacker, List<LivingEntity> targets) {
        List<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity entity : targets) {
            if (!canAttack(attacker, entity)) {
                list.add(entity);
            }
        }
        return list;
    }
}
