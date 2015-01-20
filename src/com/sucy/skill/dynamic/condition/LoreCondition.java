package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A condition for dynamic skills that requires the target to have a specified potion effect
 */
public class LoreCondition extends EffectComponent
{
    private static final String REGEX = "regex";
    private static final String STRING = "str";

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
        boolean regex = settings.getString(REGEX, "false").toLowerCase().equals("true");
        String str = settings.getString(STRING, "");
        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            if (target.getEquipment() == null || target.getEquipment().getItemInHand() == null) continue;
            List<String> lore = target.getEquipment().getItemInHand().getItemMeta().getLore();
            for (String line : lore)
            {
                if (regex && Pattern.compile(str).matcher(line).find())
                {
                    list.add(target);
                }
                else if (!regex && line.contains(str))
                {
                    list.add(target);
                }
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
