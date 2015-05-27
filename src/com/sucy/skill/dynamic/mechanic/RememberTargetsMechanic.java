package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Applies a flag to each target
 */
public class RememberTargetsMechanic extends EffectComponent
{
    private static final String KEY = "key";

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
        if (targets.size() == 0 || !settings.has(KEY))
        {
            return false;
        }

        String key = settings.getString(KEY);
        skill.getCastData(caster).put(key, targets);
        return true;
    }
}
