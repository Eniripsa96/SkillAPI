package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.util.ParticleHelper;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Plays a particle effect
 */
public class ParticleMechanic extends EffectComponent
{
    private static final Vector UP = new Vector(0, 1, 0);

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
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets)
    {
        if (targets.size() == 0)
        {
            return false;
        }

        double forward = settings.getDouble(FORWARD, 0);
        double upward = settings.getDouble(UPWARD, 0);
        double right = settings.getDouble(RIGHT, 0);

        for (LivingEntity target : targets)
        {
            Location loc = target.getLocation();
            Vector dir = loc.getDirection().setY(0).normalize();
            Vector side = dir.crossProduct(UP);
            loc.add(dir.multiply(forward)).add(0, upward, 0).add(side.multiply(right));

            ParticleHelper.play(loc, settings);
        }

        return targets.size() > 0;
    }
}
