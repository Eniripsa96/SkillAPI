package com.sucy.skill.dynamic.target;

import com.rit.sucy.player.Protection;
import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies child components to the entities in a line in front of each of the
 * provided targets.
 */
public class LinearTarget extends EffectComponent
{
    private static final String RANGE     = "range";
    private static final String TOLERANCE = "tolerance";
    private static final String ALLY      = "ally";
    private static final String MAX       = "max";

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
        double tolerance = settings.get(TOLERANCE, level, 4.0);
        double range = settings.get(RANGE, level, 5.0);
        boolean ally = settings.getBool(ALLY, false);
        int max = settings.getInt(MAX, 999);
        for (LivingEntity t : targets)
        {
            ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
            List<LivingEntity> result = TargetHelper.getLivingTargets(t, range, tolerance);
            for (LivingEntity target : result)
            {
                if (ally != Protection.canAttack(caster, target, true))
                {
                    list.add(target);
                    if (list.size() >= max)
                    {
                        break;
                    }
                }
            }
            worked = worked || executeChildren(caster, level, list);
        }
        return worked;
    }
}
