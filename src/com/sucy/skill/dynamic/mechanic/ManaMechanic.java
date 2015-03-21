package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ManaCost;
import com.sucy.skill.api.enums.ManaSource;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Gives mana to each target
 */
public class ManaMechanic extends EffectComponent
{
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
        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        boolean percent = settings.getString(TYPE, "mana").toLowerCase().equals("percent");
        double value = attr(caster, VALUE, level, 1.0, isSelf);

        boolean worked = false;
        for (LivingEntity target : targets)
        {
            if (!(target instanceof Player))
            {
                continue;
            }

            worked = true;

            PlayerData data = SkillAPI.getPlayerData((Player) target);
            double amount;
            if (percent)
            {
                amount = data.getMaxMana() * value / 100;
            }
            else
            {
                amount = value;
            }

            if (amount > 0)
            {
                data.giveMana(amount, ManaSource.SKILL);
            }
            else
            {
                data.useMana(-amount, ManaCost.SKILL_EFFECT);
            }
        }
        return worked;
    }
}
