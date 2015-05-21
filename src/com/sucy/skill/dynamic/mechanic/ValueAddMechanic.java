package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;

/**
 * Adds to a cast data value
 */
public class ValueAddMechanic extends EffectComponent
{
    private static final String KEY    = "key";
    private static final String AMOUNT = "amount";

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
        if (targets.size() == 0 || !settings.has(KEY))
        {
            return false;
        }

        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        String key = settings.getString(KEY);
        double amount = attr(caster, AMOUNT, level, 1, isSelf);
        HashMap<String, Object> data = skill.getCastData(caster);
        if (!data.containsKey(key) || !NUMBER.matcher(data.get(key).toString()).matches()) data.put(key, amount);
        else data.put(key, amount + Double.parseDouble(data.get(key).toString()));
        return true;
    }
}
