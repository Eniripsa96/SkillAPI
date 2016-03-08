package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Executes child components continuously
 */
public class PassiveMechanic extends EffectComponent
{
    private static final String PERIOD = "seconds";

    private static final HashMap<String, ArrayList<PassiveTask>> TASKS = new HashMap<String, ArrayList<PassiveTask>>();

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
            PassiveTask task = new PassiveTask(caster, level, targets, period);

            if (!TASKS.containsKey(skill.getName())) TASKS.put(skill.getName(), new ArrayList<PassiveTask>());
            TASKS.get(skill.getName()).add(task);

            return true;
        }
        return false;
    }

    /**
     * Stops all passive tasks for the player
     *
     * @param player player to cancel tasks for
     * @param skill  skill to cancel
     */
    public static void stopTasks(LivingEntity player, String skill)
    {
        ArrayList<PassiveTask> tasks = TASKS.get(skill);
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

    private class PassiveTask extends BukkitRunnable
    {
        private List<LivingEntity> targets;
        private LivingEntity       caster;
        private int                level;

        public PassiveTask(LivingEntity caster, int level, List<LivingEntity> targets, int period)
        {
            this.targets = targets;
            this.caster = caster;
            this.level = level;

            SkillAPI.schedule(this, 0, period);
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
            else if (caster instanceof Player)
            {
                PlayerSkill data = getSkillData(caster);
                if (data == null || !data.isUnlocked() || !((Player) caster).isOnline())
                {
                    cancel();
                    TASKS.get(skill.getName()).remove(this);
                    return;
                }
            }
            level = skill.getActiveLevel(caster);
            executeChildren(caster, level, targets);
        }
    }
}
