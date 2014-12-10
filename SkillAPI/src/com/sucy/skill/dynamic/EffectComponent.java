package com.sucy.skill.dynamic;

import com.sucy.skill.api.Settings;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class EffectComponent
{
    private final   ArrayList<EffectComponent> children = new ArrayList<EffectComponent>();
    protected final Settings                   settings = new Settings();

    public void add(EffectComponent child)
    {
        children.add(child);
    }

    protected boolean executeChildren(LivingEntity caster, int level, List<LivingEntity> targets)
    {
        boolean worked = false;
        for (EffectComponent child : children)
        {
            worked = worked || child.execute(caster, level, targets);
        }
        return worked;
    }

    public abstract boolean execute(LivingEntity caster, int level, List<LivingEntity> targets);
}
