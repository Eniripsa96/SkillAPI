package com.sucy.skill.dynamic.condition;

import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * A condition for dynamic skills that requires the caster's class level to be within a range
 */
public class ClassLevelCondition extends EffectComponent
{
    private static final String MIN_LEVEL = "min-level";
    private static final String MAX_LEVEL = "max-level";

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
        int min = settings.getInt(MIN_LEVEL);
        int max = settings.getInt(MAX_LEVEL);

        PlayerClass data = getSkillData(caster).getPlayerClass();
        return data.getLevel() >= min
               && data.getLevel() <= max
               && executeChildren(caster, level, targets);
    }
}
