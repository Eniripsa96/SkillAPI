package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target to have a specified potion effect
 */
public class WaterCondition extends EffectComponent
{
    private static final String STATE = "state";

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
        boolean out = settings.getString(STATE).toLowerCase().equals("out of water");
        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            Material block = target.getLocation().getBlock().getType();
            if (out != (block == Material.WATER || block == Material.STATIONARY_WATER))
            {
                list.add(target);
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
