package com.sucy.skill.dynamic.condition;

import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.api.util.StatusFlag;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target to have a status condition
 */
public class StatusCondition extends EffectComponent
{
    private static final String TYPE   = "type";
    private static final String STATUS = "status";

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
        boolean active = !settings.getString(TYPE, "active").equals("not active");
        String status = settings.getString(STATUS).toLowerCase();
        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            if (status.equals("any"))
            {
                boolean has = false;
                for (String flag : StatusFlag.ALL)
                {
                    if (FlagManager.hasFlag(target, flag))
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
            else if (FlagManager.hasFlag(target, status) == active)
            {
                list.add(target);
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
