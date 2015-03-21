package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Applies a flag to each target
 */
public class StatusMechanic extends EffectComponent
{
    private static final String KEY      = "status";
    private static final String DURATION = "duration";

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

        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        String key = settings.getString(KEY, "stun").toLowerCase();
        double seconds = attr(caster, DURATION, level, 3.0, isSelf);
        int ticks = (int) (seconds * 20);
        for (LivingEntity target : targets)
        {
            FlagManager.addFlag(target, key, ticks);
        }
        return targets.size() > 0;
    }
}
