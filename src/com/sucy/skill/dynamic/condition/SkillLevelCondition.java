package com.sucy.skill.dynamic.condition;

import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * A condition for dynamic skills that requires the caster's skill level to be within a range
 */
public class SkillLevelCondition extends EffectComponent
{
    private static final String SKILL     = "skill";
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

        String skill = settings.getString(SKILL, "");
        PlayerSkill data = getSkillData(caster).getPlayerData().getSkill(skill);
        if (data == null) data = getSkillData(caster);

        return data.getLevel() >= min
               && data.getLevel() <= max
               && executeChildren(caster, level, targets);
    }
}
