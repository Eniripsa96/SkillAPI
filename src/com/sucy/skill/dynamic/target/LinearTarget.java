package com.sucy.skill.dynamic.target;

import com.rit.sucy.player.Protection;
import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Location;
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
    private static final String ALLY      = "group";
    private static final String MAX       = "max";
    private static final String WALL      = "wall";
    private static final String CASTER    = "caster";

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
        double tolerance = settings.getAttr(TOLERANCE, level, 4.0);
        double range = settings.getAttr(RANGE, level, 5.0);
        boolean both = settings.getString(ALLY, "enemy").toLowerCase().equals("both");
        boolean ally = settings.getString(ALLY, "enemy").toLowerCase().equals("ally");
        boolean throughWall = settings.getString(WALL, "false").toLowerCase().equals("true");
        boolean self = settings.getString(CASTER, "false").toLowerCase().equals("true");

        int max = settings.getInt(MAX, 999);
        Location wallCheckLoc = caster.getLocation().add(0, 1.5, 0);
        for (LivingEntity t : targets)
        {
            ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
            if (self)
            {
                list.add(caster);
            }
            List<LivingEntity> result = TargetHelper.getLivingTargets(t, range, tolerance);
            for (LivingEntity target : result)
            {
                if (!throughWall && TargetHelper.isObstructed(wallCheckLoc, target.getLocation().add(0, 1, 0)))
                {
                    continue;
                }
                if (both || ally != Protection.canAttack(caster, target, true))
                {
                    list.add(target);
                    if (list.size() >= max)
                    {
                        break;
                    }
                }
            }
            worked = executeChildren(caster, level, list) || worked;
        }
        return worked;
    }
}
