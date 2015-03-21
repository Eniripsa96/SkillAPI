package com.sucy.skill.dynamic.condition;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target's mana to fit the requirement
 */
public class ManaCondition extends EffectComponent
{
    private static final String TYPE = "type";
    private static final String MIN  = "min-value";
    private static final String MAX  = "max-value";

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
        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        String type = settings.getString(TYPE).toLowerCase();
        double min = attr(caster, MIN, level, 0, isSelf);
        double max = attr(caster, MAX, level, 99, isSelf);

        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            if (!(target instanceof Player))
            {
                continue;
            }
            double value;
            PlayerData data = SkillAPI.getPlayerData((Player) target);
            PlayerSkill skill = getSkillData(caster);
            double mana = data.getMana();
            if (type.equals("difference percent"))
            {
                value = (mana - skill.getPlayerData().getMana()) * 100 / skill.getPlayerData().getMana();
            }
            else if (type.equals("difference"))
            {
                value = mana - skill.getPlayerData().getMana();
            }
            else if (type.equals("percent"))
            {
                value = mana * 100 / data.getMaxMana();
            }
            else // type.equals("health")
            {
                value = mana;
            }
            if (value >= min && value <= max)
            {
                list.add(target);
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
