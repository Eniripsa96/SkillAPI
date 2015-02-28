package com.sucy.skill.dynamic.condition;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target to be a player who is given class
 */
public class ClassCondition extends EffectComponent
{
    private static final String CLASS = "class";
    private static final String EXACT = "exact";

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
        RPGClass c = SkillAPI.getClass(settings.getString(CLASS));
        boolean exact = settings.getBool(EXACT, false);

        if (c == null) return false;

        List<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            if (target instanceof Player)
            {
                Player player = (Player)target;
                PlayerData data = SkillAPI.getPlayerData(player);
                if (!exact && data.isClass(c)) list.add(player);
                else if (exact && data.isExactClass(c)) list.add(player);
            }
        }
        return list.size() > 0
               && executeChildren(caster, level, list);
    }
}
