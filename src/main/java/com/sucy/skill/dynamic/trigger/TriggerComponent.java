package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.util.Lists;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.TriggerComponent
 */
public class TriggerComponent extends EffectComponent {
    private boolean running = false;

    public boolean isRunning() {
        return running;
    }

    public boolean trigger(final LivingEntity caster, final LivingEntity target, final int level, double critChance) {
        return execute(caster, level, Lists.asList(target), critChance);
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
    public boolean execute(final LivingEntity caster, final int level, final List<LivingEntity> targets, double critChance) {
        try {
            running = true;
            return executeChildren(caster, level, targets, critChance);
        } finally {
            running = false;
        }
    }
}
