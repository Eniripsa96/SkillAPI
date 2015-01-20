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
        PlayerSkill playerSkill = this.skill.getPlayerData().getSkill(skill);

        boolean worked = false;
        if (skill.equals("all"))
        {
            for (PlayerSkill data : this.skill.getPlayerData().getSkills())
            {
                if (data.isOnCooldown() == (value < 0))
                {
                    continue;
                }
                if (type.equals("percent"))
                {
                    data.subtractCooldown(value * data.getData().getCooldown(data.getLevel()) / 100);
                }
                else
                {
                    data.subtractCooldown(value);
                }
                worked = true;
            }
        }
        else if (playerSkill != null && playerSkill.isOnCooldown() == (value > 0))
        {
            if (type.equals("percent"))
            {
                playerSkill.subtractCooldown(value * playerSkill.getData().getCooldown(playerSkill.getLevel()) / 100);
            }
            else
            {
                playerSkill.subtractCooldown(value);
            }
            worked = true;
        }
        return worked;
    }
}
