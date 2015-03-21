package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

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
        if (!(caster instanceof Player)) return false;

        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        String skill = settings.getString(SKILL, "");
        String type = settings.getString(TYPE, "all").toLowerCase();
        double value = attr(caster, VALUE, level, 0, isSelf);

        PlayerData playerData = SkillAPI.getPlayerData((Player)caster);

        PlayerSkill skillData = playerData.getSkill(skill);
        if (skillData == null && !skill.equals("all"))
        {
            skillData = playerData.getSkill(this.skill.getName());
        }

        boolean worked = false;
        if (skill.equals("all"))
        {
            for (PlayerSkill data : playerData.getSkills())
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
