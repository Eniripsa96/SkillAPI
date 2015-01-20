package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target to have a specified potion effect
 */
public class ToolCondition extends EffectComponent
{
    private static final String MATERIAL = "material";
    private static final String TOOL     = "tool";

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
        String material = settings.getString(MATERIAL).toUpperCase();
        String tool = settings.getString(TOOL).toUpperCase().replace("SHOVEL", "SPADE");
        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            if (target.getEquipment() == null || target.getEquipment().getItemInHand() == null)
            {
                continue;
            }
            String hand = target.getEquipment().getItemInHand().getType().name();
            if ((material.equals("ANY") || hand.contains(material)) && (tool.equals("ANY") || hand.contains(tool)))
            {
                list.add(target);
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
