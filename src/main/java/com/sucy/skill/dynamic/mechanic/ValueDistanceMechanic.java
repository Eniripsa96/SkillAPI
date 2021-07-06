package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * SkillAPI © 2017
 * com.sucy.skill.dynamic.mechanic.ValueDistanceMechanic
 */
public class ValueDistanceMechanic extends MechanicComponent
{
    private static final String KEY  = "key";

    @Override
    public String getKey() {
        return "value distance";
    }

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
        if (!settings.has(KEY) || !(caster instanceof Player))
        {
            return false;
        }

        final String key = settings.getString(KEY);
        final HashMap<String, Object> data = DynamicSkill.getCastData(caster);
        data.put(key, targets.get(0).getLocation().distance(caster.getLocation()));
        return true;
    }
}
