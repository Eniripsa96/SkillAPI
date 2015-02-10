package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Applies a damage immunity flag to each target
 */
public class ImmunityMechanic extends EffectComponent
{
    private static final String TYPE    = "type";
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
        if (targets.size() == 0 || !settings.has(TYPE))
        {
            return false;
        }

        String key = settings.getString(TYPE);
        double seconds = settings.getAttr(SECONDS, level, 3.0);
        int ticks = (int) (seconds * 20);
        for (LivingEntity target : targets)
        {
            FlagManager.addFlag(target, "immune:" + key.toUpperCase().replace(" ", "_"), ticks);
        }
        return targets.size() > 0;
    }
}
