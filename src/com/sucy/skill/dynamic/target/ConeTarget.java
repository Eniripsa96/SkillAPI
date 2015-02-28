package com.sucy.skill.dynamic.target;

import com.rit.sucy.player.Protection;
import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Applies child components to the closest all nearby entities around
 * each of the current targets.
 */
public class ConeTarget extends EffectComponent
{
    private static final String ANGLE  = "angle";
    private static final String RANGE  = "range";
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
        double range = settings.getAttr(RANGE, level, 3.0);
        double angle = settings.getAttr(ANGLE, level, 90.0);
        boolean both = settings.getString(ALLY, "enemy").toLowerCase().equals("both");
        boolean ally = settings.getString(ALLY, "enemy").toLowerCase().equals("ally");
        boolean throughWall = settings.getString(WALL, "false").toLowerCase().equals("true");
        boolean self = settings.getString(CASTER, "false").toLowerCase().equals("true");
        int max = settings.getInt(MAX, 99);
        Location wallCheckLoc = caster.getLocation().add(0, 1.5, 0);
        for (LivingEntity t : targets)
        {
            List<LivingEntity> list = TargetHelper.getConeTargets(caster, angle, range);
            if (self)
            {
                list.add(caster);
            }
            for (int i = 0; i < list.size(); i++)
            {
                LivingEntity target = list.get(i);
                if (i >= max
                    || (!throughWall && TargetHelper.isObstructed(wallCheckLoc, target.getLocation().add(0, 1, 0)))
                    || (!both && ally != Protection.isAlly(caster, target)))
                {
                    list.remove(i);
                    i--;
                }
            }
            worked = executeChildren(caster, level, list) || worked;
        }
        return worked;
    }
}
