package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Launches the target in a given direction relative to their forward direction
 */
public class LaunchMechanic extends EffectComponent
{
    private Vector up = new Vector(0, 1, 0);

    private static final String FORWARD = "forward";
    private static final String UPWARD  = "upward";
    private static final String RIGHT   = "right";

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
    public boolean execute(final LivingEntity caster, final int level, final List<LivingEntity> targets)
    {
        if (targets.size() == 0)
        {
            return false;
        }

        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        double forward = attr(caster, FORWARD, level, 0, isSelf);
        double upward = attr(caster, UPWARD, level, 0, isSelf);
        double right = attr(caster, RIGHT, level, 0, isSelf);
        for (LivingEntity target : targets)
        {
            Vector dir = target.getLocation().getDirection().setY(0).normalize();
            Vector nor = dir.clone().crossProduct(up);
            dir.multiply(forward);
            dir.add(nor.multiply(right)).setY(upward);

            target.setVelocity(dir);
        }
        return targets.size() > 0;
    }
}
