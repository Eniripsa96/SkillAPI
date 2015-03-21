package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target to fit the elevation requirement
 */
public class ElevationCondition extends EffectComponent
{
    private static final String TYPE = "type";
    private static final String MIN  = "min-value";
    private static final String MAX  = "max-value";

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
        String type = settings.getString(TYPE).toLowerCase();
        double min = attr(caster, MIN, level, 0, isSelf);
        double max = attr(caster, MAX, level, 255, isSelf);

        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            double value;
            if (type.equals("difference"))
            {
                value = target.getLocation().getY() - caster.getLocation().getY();
            }
            else
            {
                value = target.getLocation().getY();
            }
            if (value >= min && value <= max)
            {
                list.add(target);
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
