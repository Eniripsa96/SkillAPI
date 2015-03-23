package com.sucy.skill.dynamic.target;

import com.rit.sucy.player.Protection;
import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Location;
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
    private static final String ALLY   = "group";
    private static final String MAX    = "max";
    private static final String WALL   = "wall";
    private static final String CASTER = "caster";

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
        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        double radius = attr(caster, RADIUS, level, 3.0, isSelf);
        boolean both = settings.getString(ALLY, "enemy").toLowerCase().equals("both");
        boolean ally = settings.getString(ALLY, "enemy").toLowerCase().equals("ally");
        boolean throughWall = settings.getString(WALL, "false").toLowerCase().equals("true");
        boolean self = settings.getString(CASTER, "false").toLowerCase().equals("true");
        double max = attr(caster, MAX, level, 99, isSelf);
        Location wallCheckLoc = caster.getLocation().add(0, 0.5, 0);
        for (LivingEntity t : targets)
        {
            ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
            List<Entity> entities = t.getNearbyEntities(radius, radius, radius);
            if (t != caster)
            {
                list.add(t);
            }
            if (self)
            {
                list.add(caster);
            }

            for (int i = 0; i < entities.size() && list.size() < max; i++)
            {
                if (entities.get(i) instanceof LivingEntity)
                {
                    LivingEntity target = (LivingEntity) entities.get(i);
                    if (!throughWall && TargetHelper.isObstructed(wallCheckLoc, target.getLocation().add(0, 0.5, 0)))
                    {
                        continue;
                    }
                    if (both || ally == Protection.isAlly(caster, target))
                    {
                        list.add(target);
                        if (list.size() >= max)
                        {
                            break;
                        }
                    }
                }
            }

            worked = executeChildren(caster, level, list) || worked;
        }
        return worked;
    }
}
