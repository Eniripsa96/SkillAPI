package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Executes child components after a delay
 */
public class FireMechanic extends EffectComponent
{
    private static final String SECONDS = "seconds";

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
    public boolean execute(final LivingEntity caster, final int level, final List<LivingEntity> targets)
    {
        if (targets.size() == 0)
        {
            return false;
        }
        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        double seconds = attr(caster, SECONDS, level, 3.0, isSelf);
        int ticks = (int) (seconds * 20);
        for (LivingEntity target : targets)
        {
            target.setFireTicks(Math.max(ticks, target.getFireTicks()));
        }
        return targets.size() > 0;
    }
}
