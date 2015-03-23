package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.listener.MechanicListener;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Applies a flag to each target
 */
public class SpeedMechanic extends EffectComponent
{
    private static final float BASE_SPEED = 0.2f;

    private static final String MULTIPLIER = "multiplier";
    private static final String DURATION   = "duration";

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
        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        float multiplier = (float) attr(caster, MULTIPLIER, level, 1.2, isSelf);
        double seconds = attr(caster, DURATION, level, 3.0, isSelf);
        int ticks = (int) (seconds * 20);
        boolean worked = false;
        for (LivingEntity target : targets)
        {
            if (!(target instanceof Player)) continue;

            FlagManager.addFlag(target, MechanicListener.SPEED_KEY, ticks);
            ((Player) target).setWalkSpeed(multiplier * BASE_SPEED);
            worked = true;
        }
        return worked;
    }
}
