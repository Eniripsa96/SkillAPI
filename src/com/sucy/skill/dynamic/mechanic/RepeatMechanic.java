package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Executes child components multiple times
 */
public class RepeatMechanic extends EffectComponent
{
    private static final String REPETITIONS = "repetitions";
    private static final String DELAY       = "delay";
    private static final String PERIOD      = "period";

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
        if (targets.size() > 0)
        {
            boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
            int count = (int) attr(caster, REPETITIONS, level, 3.0, isSelf);
            if (count <= 0)
            {
                return false;
            }
            int delay = (int) (settings.getDouble(DELAY, 0.0) * 20);
            int period = (int) (settings.getDouble(PERIOD, 1.0) * 20);
            new RepeatTask(caster, level, targets, count, delay, period);
            return true;
        }
        return false;
    }

    private class RepeatTask extends BukkitRunnable
    {
        private List<LivingEntity> targets;
        private LivingEntity       caster;
        private int                level;
        private int                count;

        public RepeatTask(LivingEntity caster, int level, List<LivingEntity> targets, int count, int delay, int period)
        {
            this.targets = targets;
            this.caster = caster;
            this.level = level;
            this.count = count;

            runTaskTimer(Bukkit.getPluginManager().getPlugin("SkillAPI"), delay, period);
        }

        @Override
        public void run()
        {
            for (int i = 0; i < targets.size(); i++) {
                if (targets.get(i).isDead() || !targets.get(i).isValid()) {
                    targets.remove(i);
                }
            }

            if (!skill.isActive(caster) || targets.size() == 0)
            {
                cancel();
                return;
            }

            level = skill.getActiveLevel(caster);

            executeChildren(caster, level, targets);
            if (--count <= 0)
            {
                cancel();
            }
        }
    }
}
