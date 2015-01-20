package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Executes child components after a delay
 */
public class DelayMechanic extends EffectComponent
{
    private static final String SECONDS = "delay";

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

        double seconds = settings.get(SECONDS, level, 2.0);
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("SkillAPI"), new Runnable()
        {
            @Override
            public void run()
            {
                executeChildren(caster, level, targets);
            }
        }, (long) (seconds * 20));
        return true;
    }
}
