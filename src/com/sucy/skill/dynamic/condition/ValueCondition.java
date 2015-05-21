package com.sucy.skill.dynamic.condition;

import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target to have a specified flag active
 */
public class ValueCondition extends EffectComponent
{
    private static final String KEY  = "key";
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
        String key = settings.getString(KEY);
        double min = attr(caster, MIN, level, 1, true);
        double max = attr(caster, MAX, level, 999, true);
        Object data = skill.getCastData(caster).get(key);

        if (data != null && NUMBER.matcher(data.toString()).matches())
        {
            double value = Double.parseDouble(data.toString());
            if (value >= min && value <= max)
            {
                List<LivingEntity> t = new ArrayList<LivingEntity>();
                t.add(caster);
                return executeChildren(caster, level, t);
            }
        }

        return false;
    }
}
