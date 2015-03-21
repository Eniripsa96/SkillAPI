package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Launches the target in a given direction relative to their forward direction
 */
public class PushMechanic extends EffectComponent
{
    private static final String SPEED = "speed";

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
        double speed = attr(caster, SPEED, level, 3.0, isSelf);
        boolean worked = false;
        for (LivingEntity target : targets)
        {
            Vector vel = target.getLocation().subtract(caster.getLocation()).toVector();
            if (vel.lengthSquared() == 0)
            {
                continue;
            }
            vel.multiply(speed / vel.lengthSquared());
            vel.setY(vel.getY() / 5 + 0.5);
            target.setVelocity(vel);
            worked = true;
        }
        return worked;
    }
}
