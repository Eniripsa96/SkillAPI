package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Applies a flag to each target
 */
public class FlagMechanic extends EffectComponent
{
    private static final String KEY = "key";
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
        if (targets.size() == 0) return false;

        String key = settings.getString(KEY);
        double seconds = settings.get(SECONDS, level);
        int ticks = (int)(seconds * 20);
        for (LivingEntity target : targets)
        {
            FlagManager.addFlag(target, key, ticks);
        }
        return true;
    }
}
