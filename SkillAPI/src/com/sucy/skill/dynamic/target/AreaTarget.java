package com.sucy.skill.dynamic.target;

import com.rit.sucy.player.Protection;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies child components to the closest all nearby entities around
 * each of the current targets.
 */
public class AreaTarget extends EffectComponent
{
    private static final String RADIUS = "radius";
    private static final String ALLY   = "ally";
    private static final String MAX    = "max";

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
        double radius = settings.get(RADIUS, level, 5.0);
        boolean ally = settings.getBool(ALLY, false);
        int max = settings.getInt(MAX, 999);
        for (LivingEntity t : targets)
        {
            ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
            List<Entity> entities = t.getNearbyEntities(radius, radius, radius);
            for (int i = 0; i < entities.size() && list.size() < max; i++)
            {
                if (entities.get(i) instanceof LivingEntity)
                {
                    LivingEntity target = (LivingEntity) entities.get(i);
                    if (ally == Protection.isAlly(caster, target))
                    {
                        list.add((LivingEntity) entities.get(i));
                        if (list.size() >= max)
                        {
                            break;
                        }
                    }
                }
            }
            worked = worked || executeChildren(caster, level, list);
        }
        return worked;
    }
}
