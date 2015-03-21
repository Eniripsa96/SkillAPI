package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Executes child components after a delay
 */
public class PotionMechanic extends EffectComponent
{
    private static final String POTION  = "potion";
    private static final String AMBIENT = "ambient";
    private static final String TIER    = "tier";
    private static final String SECONDS = "seconds";

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
        if (targets.size() == 0)
        {
            return false;
        }

        try
        {
            boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
            PotionEffectType potion = PotionEffectType.getByName(settings.getString(POTION, "Absorption").toUpperCase().replace(' ', '_'));
            int tier = (int) attr(caster, TIER, level, 1, isSelf) - 1;
            double seconds = attr(caster, SECONDS, level, 3.0, isSelf);
            boolean ambient = !settings.getString(AMBIENT, "true").equals("false");
            int ticks = (int) (seconds * 20);
            for (LivingEntity target : targets)
            {
                target.addPotionEffect(new PotionEffect(potion, ticks, tier), ambient);
            }
            return targets.size() > 0;
        }
        catch (Exception ex)
        {
            return false;
        }
    }
}
