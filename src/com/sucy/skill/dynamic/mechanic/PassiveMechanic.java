package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Executes child components continuously
 */
public class PassiveMechanic extends EffectComponent
{
    private static final String PERIOD = "seconds";

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
            int period = (int) (attr(caster, PERIOD, level, 1.0, isSelf) * 20);
            new RepeatTask(caster, level, targets, period);
            return true;
        }
        return false;
    }

    private class RepeatTask extends BukkitRunnable
    {
        private List<LivingEntity> targets;
        private LivingEntity       caster;
        private int                level;

        public RepeatTask(LivingEntity caster, int level, List<LivingEntity> targets, int period)
        {
            this.targets = targets;
            this.caster = caster;
            this.level = level;

            runTaskTimer(Bukkit.getPluginManager().getPlugin("SkillAPI"), 0, period);
        }

        @Override
        public void run()
        {
            if (!caster.isValid() || caster.isDead() || !skill.isActive(caster))
            {
                cancel();
                return;
            }
            else if (caster instanceof Player)
            {
                PlayerSkill data = getSkillData(caster);
                if (data == null || !data.isUnlocked() || !((Player) caster).isOnline())
                {
                    cancel();
                    return;
                }
            }
            level = skill.getActiveLevel(caster);
            executeChildren(caster, level, targets);
        }
    }
}
