package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.util.Lists;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.Random;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.TriggerComponent
 */
public class TriggerComponent extends EffectComponent {
    private boolean running = false;
    private static Random gen = new Random();

    public boolean isRunning() {
        return running;
    }

    public boolean trigger(final LivingEntity caster, final LivingEntity target, final int level) {
        return execute(caster, level, Lists.asList(target), false);
    }

    @Override
    public String getKey() {
        return "trigger";
    }

    @Override
    public ComponentType getType() {
        return ComponentType.TRIGGER;
    }

    @Override
    public boolean execute(final LivingEntity caster, final int level, final List<LivingEntity> targets, boolean isCrit) {
        try {
            running = true;
            isCrit = this.skill.getCritChance() > gen.nextDouble();
            return executeChildren(caster, level, targets, isCrit);
        } finally {
            running = false;
        }
    }
}
