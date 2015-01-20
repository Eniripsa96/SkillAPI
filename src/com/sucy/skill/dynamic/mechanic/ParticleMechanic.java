package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.util.ParticleHelper;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Plays a particle effect
 */
public class ParticleMechanic extends EffectComponent
{
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
        if (targets.size() == 0)
        {
            return false;
        }

        for (LivingEntity target : targets)
        {
            ParticleHelper.play(target.getLocation(), settings);
        }

        return true;
    }
}
