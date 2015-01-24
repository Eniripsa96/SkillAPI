package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Lowers the cooldowns of the caster's skills
 */
public class CooldownMechanic extends EffectComponent
{
    private static final String SKILL = "skill";
    private static final String TYPE  = "type";
    private static final String VALUE = "value";

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
        String skill = settings.getString(SKILL);
        String type = settings.getString(TYPE).toLowerCase();
        double value = settings.get(VALUE, level);
        PlayerSkill skillData = getSkillData(caster);
        if (skill == null) return false;

        boolean worked = false;
        if (skill.equals("all"))
        {
            for (PlayerSkill data : skillData.getPlayerData().getSkills())
            {
                if (data.isOnCooldown() == (value < 0))
                {
                    continue;
                }
                if (type.equals("percent"))
                {
                    data.subtractCooldown(value * data.getCooldown() / 100);
                }
                else
                {
                    data.subtractCooldown(value);
                }
                worked = true;
            }
        }
        else if (skillData != null && skillData.isOnCooldown() == (value > 0))
        {
            if (type.equals("percent"))
            {
                skillData.subtractCooldown(value * skillData.getCooldown() / 100);
            }
            else
            {
                skillData.subtractCooldown(value);
            }
            worked = true;
        }
        return worked;
    }
}
