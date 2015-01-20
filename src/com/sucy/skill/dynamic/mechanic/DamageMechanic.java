package com.sucy.skill.dynamic.mechanic;

import com.rit.sucy.version.VersionManager;
import com.sucy.skill.api.event.SkillDamageEvent;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Deals damage to each target
 */
public class DamageMechanic extends EffectComponent
{
    private static final String TYPE   = "type";
    private static final String DAMAGE = "value";

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
        boolean percent = settings.getString(TYPE, "damage").toLowerCase().equals("percent");
        double damage = settings.get(DAMAGE, level, 1.0);
        for (LivingEntity target : targets)
        {
            double amount = damage;
            if (percent)
            {
                amount = damage * target.getMaxHealth() / 100;
            }
            SkillDamageEvent event = new SkillDamageEvent(caster, target, amount);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled())
            {
                VersionManager.damage(target, caster, event.getAmount());
            }
        }
        return targets.size() > 0;
    }
}
