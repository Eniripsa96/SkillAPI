package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.api.util.StatusFlag;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Purges a target of positive potion or status effects
 */
public class PurgeMechanic extends EffectComponent
{
    private static final PotionEffectType[] POTIONS = new PotionEffectType[] {
            PotionEffectType.ABSORPTION, PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.FAST_DIGGING,
            PotionEffectType.FIRE_RESISTANCE, PotionEffectType.HEALTH_BOOST, PotionEffectType.INCREASE_DAMAGE,
            PotionEffectType.INVISIBILITY, PotionEffectType.JUMP, PotionEffectType.NIGHT_VISION,
            PotionEffectType.REGENERATION, PotionEffectType.SATURATION, PotionEffectType.SPEED,
            PotionEffectType.WATER_BREATHING
    };

    private static final String STATUS = "status";
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
        boolean worked = false;
        String status = settings.getString(STATUS, "None").toLowerCase();
        String potion = settings.getString(POTION).toUpperCase().replace(' ', '_');
        PotionEffectType type = null;
        try
        {
            type = PotionEffectType.getByName(potion);
        }
        catch (Exception ex)
        {
            // Invalid potion type
        }

        for (LivingEntity target : targets)
        {
            if (status.equals("all"))
            {
                for (String flag : StatusFlag.POSITIVE)
                {
                    if (FlagManager.hasFlag(target, flag))
                    {
                        FlagManager.removeFlag(target, flag);
                        worked = true;
                    }
                }
            }
            else if (FlagManager.hasFlag(target, status))
            {
                FlagManager.removeFlag(target, status);
                worked = true;
            }

            if (potion.equals("all"))
            {
                for (PotionEffectType p : POTIONS)
                {
                    if (target.hasPotionEffect(p))
                    {
                        target.removePotionEffect(p);
                        worked = true;
                    }
                }
            }
            else if (type != null && target.hasPotionEffect(type))
            {
                target.removePotionEffect(type);
                worked = true;
            }
        }
        return worked;
    }
}
