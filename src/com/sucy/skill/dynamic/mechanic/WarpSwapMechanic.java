package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Strikes lightning about each target with an offset
 */
public class WarpSwapMechanic extends EffectComponent
{
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
        if (targets.size() > 0) {
            Location tloc = targets.get(0).getLocation();
            Location cloc = caster.getLocation();
            targets.get(0).teleport(cloc);
            caster.teleport(tloc);
            return true;
        }
        return false;
    }
}
