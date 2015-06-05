package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * Adds to a cast data value
 */
public class ValueAttributeMechanic extends EffectComponent
{
    private static final String KEY  = "key";
    private static final String ATTR = "attribute";

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
        if (!settings.has(KEY) || !settings.has(ATTR) || !(caster instanceof Player))
        {
            return false;
        }

        String key = settings.getString(KEY);
        String attr = settings.getString(ATTR);
        HashMap<String, Object> data = DynamicSkill.getCastData(caster);
        data.put(key, SkillAPI.getPlayerData((Player) caster).getAttribute(attr));
        return true;
    }
}
