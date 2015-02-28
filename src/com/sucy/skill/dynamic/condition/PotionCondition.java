package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target to have a specified potion effect
 */
public class PotionCondition extends EffectComponent
{
    private static final String TYPE   = "type";
    private static final String POTION = "potion";

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
        boolean active = !settings.getString(TYPE, "active").toLowerCase().equals("not active");
        String potion = settings.getString(POTION, "").toUpperCase().replace(' ', '_');
        PotionEffectType type = null;
        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        try
        {
            type = PotionEffectType.getByName(potion);
            for (LivingEntity target : targets)
            {
                if (target.hasPotionEffect(type) == active)
                {
                    list.add(target);
                }
            }
        }
        catch (Exception ex)
        {
            for (LivingEntity target : targets)
            {
                boolean has = false;
                for (PotionEffectType check : PotionEffectType.values())
                {
                    if (check != null && target.hasPotionEffect(check))
                    {
                        has = true;
                        break;
                    }
                }
                if (has == active)
                {
                    list.add(target);
                }
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
