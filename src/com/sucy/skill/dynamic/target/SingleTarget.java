package com.sucy.skill.dynamic.target;

import com.rit.sucy.player.Protection;
import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Location;
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
    private static final String ALLY      = "group";
    private static final String WALL      = "wall";

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
        double range = attr(caster, RANGE, level, 5.0, isSelf);
        double tolerance = attr(caster, TOLERANCE, level, 4.0, isSelf);
        boolean both = settings.getString(ALLY, "enemy").toLowerCase().equals("both");
        boolean ally = settings.getString(ALLY, "enemy").toLowerCase().equals("ally");
        boolean throughWall = settings.getString(WALL, "false").toLowerCase().equals("true");
        Location wallCheckLoc = caster.getLocation().add(0, 0.5, 0);
        for (LivingEntity t : targets)
        {
            LivingEntity target = TargetHelper.getLivingTarget(t, range, tolerance);
            if (target != null)
            {
                if (!throughWall && TargetHelper.isObstructed(wallCheckLoc, target.getLocation().add(0, 0.5, 0)))
                {
                    continue;
                }
                if (both || ally != Protection.canAttack(caster, target))
                {
                    ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
                    list.add(target);
                    worked = executeChildren(caster, level, list) || worked;
                }
            }
        }
        return worked;
    }
}
