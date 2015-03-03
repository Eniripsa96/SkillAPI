package com.sucy.skill.dynamic.condition;

import com.sucy.skill.api.util.Combat;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target to be a player
 * who's combat status matches the settings
 */
public class CombatCondition extends EffectComponent
{
    private static final String COMBAT  = "combat";
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
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets)
    {
        boolean combat = !settings.getString(COMBAT, "true").toLowerCase().equals("false");
        double seconds = settings.getDouble(SECONDS, 10);

        List<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            if (target instanceof Player)
            {
                Player player = (Player) target;
                if (Combat.isInCombat(player, seconds) == combat)
                {
                    list.add(player);
                }
            }
        }
        return list.size() > 0
               && executeChildren(caster, level, list);
    }
}
