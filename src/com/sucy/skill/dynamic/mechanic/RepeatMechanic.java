package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Executes child components multiple times
 */
public class RepeatMechanic extends EffectComponent
{
    private static final String REPETITIONS = "repetitions";
    private static final String DELAY       = "delay";
    private static final String PERIOD      = "period";

    private static final HashMap<String, ArrayList<RepeatTask>> TASKS = new HashMap<String, ArrayList<RepeatTask>>();

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
            RepeatTask task = new RepeatTask(caster, level, targets, count, delay, period);

            if (!TASKS.containsKey(skill.getName())) TASKS.put(skill.getName(), new ArrayList<RepeatTask>());
            TASKS.get(skill.getName()).add(task);

            return true;
        }
        return false;
    }

    /**
     * Stops all repeat tasks for the player
     *
     * @param player player to cancel tasks for
     * @param skill  skill to cancel
     */
    public static void stopTasks(LivingEntity player, String skill)
    {
        ArrayList<RepeatTask> tasks = TASKS.get(skill);
        if (tasks == null) return;
        for (int i = 0; i < tasks.size(); i++)
        {
            if (tasks.get(i).caster == player)
            {
                tasks.get(i).cancel();
                tasks.remove(i);
                i--;
            }
        }
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

            SkillAPI.schedule(this, delay, period);
        }

        @Override
        public void run()
        {
            for (int i = 0; i < targets.size(); i++)
            {
                if (targets.get(i).isDead() || !targets.get(i).isValid())
                {
                    targets.remove(i);
                }
            }

            if (!skill.isActive(caster) || targets.size() == 0)
            {
                cancel();
                TASKS.get(skill.getName()).remove(this);
                return;
            }

            level = skill.getActiveLevel(caster);

            executeChildren(caster, level, targets);
            if (--count <= 0)
            {
                cancel();
                TASKS.get(skill.getName()).remove(this);
            }
        }
    }
}
