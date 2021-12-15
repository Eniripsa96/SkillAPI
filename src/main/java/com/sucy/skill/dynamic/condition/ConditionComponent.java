package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.condition.ConditionComponent
 */
public abstract class ConditionComponent extends EffectComponent {

    /** {@inheritDoc} */
    @Override
    public ComponentType getType() {
        return ComponentType.CONDITION;
    }

    /** {@inheritDoc} */
    @Override
    public boolean execute(
            final LivingEntity caster, final int level, final List<LivingEntity> targets, double critChance) {

        final List<LivingEntity> filtered = targets.stream()
                .filter(t -> test(caster, level, t, critChance))
                .collect(Collectors.toList());

        return filtered.size() > 0 && executeChildren(caster, level, filtered, critChance);
    }

    abstract boolean test(final LivingEntity caster, final int level, final LivingEntity target, double critChance);
}
