package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * SkillAPI © 2017
 * com.sucy.skill.dynamic.mechanic.HealthSetMechanic
 */
public class HealthSetMechanic extends EffectComponent {

    private static final String HEALTH = "health";

    @Override
    public boolean execute(final LivingEntity caster, final int level, final List<LivingEntity> targets) {
        final boolean self = targets.size() == 1 && targets.get(0) == caster;
        final double health = Math.max(1, attr(caster, HEALTH, level, 1, self));

        for (final LivingEntity target : targets) {
            target.setHealth(Math.min(health, target.getMaxHealth()));
        }

        return true;
    }
}
