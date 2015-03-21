package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the lighting at the target's location to be within a range
 */
public class LightCondition extends EffectComponent
{
    private static final String MIN = "min-light";
    private static final String MAX = "max-light";

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
        double min = attr(caster, MIN, level, 0, isSelf);
        double max = attr(caster, MAX, level, 0, isSelf);

        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            if (target.getLocation().getBlock().getLightLevel() >= min
                && target.getLocation().getBlock().getLightLevel() <= max)
            {
                list.add(target);
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
