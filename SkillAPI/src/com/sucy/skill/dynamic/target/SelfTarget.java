package com.sucy.skill.dynamic.target;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies child components to the closest linear entity of each of the
 * provided targets.
 */
public class SelfTarget extends EffectComponent
{
    private static final String REPEATED = "repeated";

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
        boolean worked = false;
        for (LivingEntity t : targets)
        {
            ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
            list.add(caster);
            worked = worked || executeChildren(caster, level, list);

            if (!settings.getBool(REPEATED))
            {
                break;
            }
        }
        return worked;
    }
}
