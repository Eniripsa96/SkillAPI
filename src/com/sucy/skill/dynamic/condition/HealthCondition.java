package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target's health to fit the requirement
 */
public class HealthCondition extends EffectComponent
{
    private static final String TYPE = "type";
    private static final String MIN = "min-value";
    private static final String MAX = "max-value";

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
        String type = settings.getString(TYPE).toLowerCase();
        double min = settings.get(MIN, level);
        double max = settings.get(MAX, level);

        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            double value;
            if (type.equals("difference percent"))
            {
                value = (target.getHealth() - caster.getHealth()) * 100 / caster.getHealth();
            }
            else if (type.equals("difference"))
            {
                value = target.getHealth() - caster.getHealth();
            }
            else if (type.equals("percent"))
            {
                value = target.getHealth() * 100 / target.getMaxHealth();
            }
            else // type.equals("health")
            {
                value = target.getHealth();
            }
            if (value >= min && value <= max)
            {
                list.add(target);
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
