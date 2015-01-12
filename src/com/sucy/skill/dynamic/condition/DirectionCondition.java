package com.sucy.skill.dynamic.condition;

import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target or caster to be facing a direction relative to the other
 */
public class DirectionCondition extends EffectComponent
{
    private static final String TYPE = "ty[e";
    private static final String DIRECTION = "direction";

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
        String dir = settings.getString(DIRECTION).toLowerCase();
        boolean towards = dir.equals("towards");

        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            if (type.equals("target"))
            {
                if (TargetHelper.isInFront(target, caster) == towards)
                {
                    list.add(target);
                }
            }
            else // type.equals("normal")
            {
                if (TargetHelper.isInFront(caster, target) == towards)
                {
                    list.add(target);
                }
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
