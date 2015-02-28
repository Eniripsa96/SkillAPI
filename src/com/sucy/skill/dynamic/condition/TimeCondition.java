package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * A condition for dynamic skills that requires the game time to match the settings
 */
public class TimeCondition extends EffectComponent
{
    private static final String TIME = "time";

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     *
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets)
    {
        boolean night = settings.getString(TIME).toLowerCase().equals("night");
        return night == (caster.getWorld().getTime() >= 12300 && caster.getWorld().getTime() <= 23850)
               && executeChildren(caster, level, targets);
    }
}
