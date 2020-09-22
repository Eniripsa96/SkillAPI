package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.EffectComponent;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.mechanic.MechanicComponent
 */
public abstract class MechanicComponent extends EffectComponent {
    @Override
    public ComponentType getType() {
        return ComponentType.MECHANIC;
    }
}
