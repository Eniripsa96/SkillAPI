package com.sucy.skill.dynamic.condition;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target to be a player who is given class
 */
public class AttributeCondition extends EffectComponent
{
    private static final String ATTR = "attribute";
    private static final String MIN  = "min";
    private static final String MAX  = "max";

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
        String attr = settings.getString(ATTR, null);
        int min = (int) attr(caster, MIN, level, 0, true);
        int max = (int) attr(caster, MAX, level, 999, true);

        if (attr == null) return false;

        List<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            if (target instanceof Player)
            {
                Player player = (Player) target;
                PlayerData data = SkillAPI.getPlayerData(player);

                int num = data.getAttribute(attr);
                if (num >= min && num <= max) list.add(player);
            }
        }
        return list.size() > 0
               && executeChildren(caster, level, list);
    }
}
