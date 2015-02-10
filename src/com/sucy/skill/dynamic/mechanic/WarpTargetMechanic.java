package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Strikes lightning about each target with an offset
 */
public class WarpTargetMechanic extends EffectComponent
{
    private static final String TYPE = "type";

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

        boolean toCaster = settings.getString(TYPE, "caster to target").toLowerCase().equals("target to caster");
        for (LivingEntity target : targets)
        {
            if (toCaster)
            {
                target.teleport(caster);
            }
            else
            {
                caster.teleport(target);
            }
        }
        return targets.size() > 0;
    }
}
