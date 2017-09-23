package com.sucy.skill.dynamic;

import org.bukkit.entity.LivingEntity;

/**
 * SkillAPI © 2017
 * com.sucy.skill.dynamic.TriggerContext
 */
public class TriggerContext {
    public final EffectComponent component;
    public final LivingEntity caster;
    public final int level;

    public TriggerContext(final EffectComponent component, final LivingEntity caster, final int level) {
        this.component = component;
        this.caster = caster;
        this.level = level;
    }
}
