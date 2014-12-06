package com.sucy.skill.dynamic.target;

import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies child components to the closest linear entity of each of the
 * provided targets.
 */
public class SingleTarget extends EffectComponent
{
    private static final String RANGE     = "range";
    private static final String TOLERANCE = "tolerance";
    private static final String ALLY      = "ally";

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
            LivingEntity target = TargetHelper.getLivingTarget(caster, attributes.get(RANGE, level, 5), attributes.get(TOLERANCE, level, 4));
            if (target != null)
            {
                ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
                list.add(target);
                worked = worked || executeChildren(caster, level, list);
            }
        }
        return worked;
    }
}
