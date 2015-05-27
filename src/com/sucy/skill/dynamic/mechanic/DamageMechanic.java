package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.EffectComponent;
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
        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        String pString = settings.getString(TYPE, "damage").toLowerCase();
        boolean percent = pString.equals("multiplier") || pString.equals("percent");
        boolean missing = pString.equals("percent missing");
        boolean left = pString.equals("percent left");
        double damage = attr(caster, DAMAGE, level, 1.0, isSelf);
        if (damage < 0) return false;
        for (LivingEntity target : targets)
        {
            double amount = damage;
            if (percent)
            {
                amount = damage * target.getMaxHealth() / 100;
            }
            else if (missing)
            {
                amount = damage * (target.getMaxHealth() - target.getHealth()) / 100;
            }
            else if (left)
            {
                amount = damage * target.getHealth() / 100;
            }
            skill.damage(target, amount, caster);
        }
        return targets.size() > 0;
    }
}
