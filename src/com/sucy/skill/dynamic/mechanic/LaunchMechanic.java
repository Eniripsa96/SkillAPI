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

        double forward = settings.get(FORWARD, level, 0);
        double upward = settings.get(UPWARD, level, 0);
        double right = settings.get(RIGHT, level, 0);
        for (LivingEntity target : targets)
        {
            Vector dir = target.getLocation().getDirection().setY(0).normalize();
            Vector nor = dir.crossProduct(up);
            target.setVelocity(dir.multiply(forward).add(nor.multiply(right)).setY(upward));
        }
        return true;
    }
}
