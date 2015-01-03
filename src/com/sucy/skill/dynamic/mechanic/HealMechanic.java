package com.sucy.skill.dynamic.mechanic;

import com.rit.sucy.version.VersionManager;
import com.sucy.skill.api.event.SkillHealEvent;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Heals each target
 */
public class HealMechanic extends EffectComponent
{
    private static final String HEALTH = "health";

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
        double health = settings.get(HEALTH, level, 1.0);
        for (LivingEntity target : targets)
        {
            SkillHealEvent event = new SkillHealEvent(caster, target, health);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled())
            {
                VersionManager.heal(target, event.getAmount());
            }
        }
        return targets.size() > 0;
    }
}
