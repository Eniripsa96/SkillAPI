package com.sucy.skill.api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * SkillAPI © 2018
 * com.sucy.skill.api.AllyChecker
 */
public interface CombatProtection {
    boolean canAttack(final Player attacker, final Player defender);
    boolean canAttack(final Player attacker, final LivingEntity defender);
    boolean canAttack(final LivingEntity attacker, final LivingEntity defender);
}
