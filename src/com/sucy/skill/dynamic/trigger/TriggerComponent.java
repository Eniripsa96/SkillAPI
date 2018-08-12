package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.TriggerComponent
 */
public class TriggerComponent extends EffectComponent {

    private boolean running = false;

    public boolean isRunning() {
        return running;
    }

    public boolean trigger(final LivingEntity caster, final LivingEntity target, final int level) {
        final ArrayList<LivingEntity> targets = new ArrayList<>();
        targets.add(target);
        return execute(caster, level, targets);
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
    public boolean execute(final LivingEntity caster, final int level, final List<LivingEntity> targets) {
        try {
            running = true;
            return executeChildren(caster, level, targets);
        } finally {
            running = false;
        }
    }
}
