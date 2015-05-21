package com.sucy.skill.dynamic.target;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Applies a flag to each target
 */
public class RememberTarget extends EffectComponent
{
    private static final String KEY     = "key";

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
        if (!settings.has(KEY))
        {
            return false;
        }

        String key = settings.getString(KEY);
        Object data = skill.getCastData(caster).get(key);
        try
        {
            List<LivingEntity> remembered = (List<LivingEntity>)data;
            for (int i = 0; i < remembered.size(); i++) {
                if (remembered.get(i).isDead() || !remembered.get(i).isValid()) {
                    remembered.remove(i);
                    i--;
                }
            }
            return targets.size() > 0 && executeChildren(caster, level, remembered);
        }
        catch(Exception ex)
        {
            return false;
        }
    }
}
