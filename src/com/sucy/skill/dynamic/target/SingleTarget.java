package com.sucy.skill.dynamic.target;

import com.rit.sucy.player.Protection;
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
        double range = settings.get(RANGE, level, 5.0);
        double tolerance = settings.get(TOLERANCE, level, 4.0);
        boolean ally = settings.getBool(ALLY, false);
        for (LivingEntity t : targets)
        {
            LivingEntity target = TargetHelper.getLivingTarget(t, range, tolerance);
            if (target != null && ally != Protection.canAttack(caster, target, true))
            {
                ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
                list.add(target);
                worked = worked || executeChildren(caster, level, list);
            }
        }
        return worked;
    }
}
