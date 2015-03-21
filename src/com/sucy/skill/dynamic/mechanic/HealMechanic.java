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
    private static final String TYPE  = "type";
    private static final String VALUE = "value";

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
        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        boolean percent = settings.getString(TYPE, "health").toLowerCase().equals("percent");
        double value = attr(caster, VALUE, level, 1.0, isSelf);
        if (value < 0) return false;
        for (LivingEntity target : targets)
        {
            double amount = value;
            if (percent)
            {
                amount = target.getMaxHealth() * value / 100;
            }

            SkillHealEvent event = new SkillHealEvent(caster, target, amount);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled())
            {
                VersionManager.heal(target, event.getAmount());
            }
        }
        return targets.size() > 0;
    }
}
