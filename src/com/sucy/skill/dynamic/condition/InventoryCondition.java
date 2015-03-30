package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target to have a specified item
 */
public class InventoryCondition extends EffectComponent
{
    private static final String MATERIAL = "material";
    private static final String DATA = "data";
    private static final String AMOUNT = "amount";

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
        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        String item = settings.getString(MATERIAL, "").toUpperCase().replace(" ", "_");
        short data = (short)settings.getInt(DATA, 0);
        int amount = settings.getInt(AMOUNT, 1);
        ItemStack check;
        try
        {
            check = new ItemStack(Material.valueOf(item), 1, data);
        }
        catch (Exception ex) {
            return false;
        }

        for (LivingEntity target : targets)
        {
            if (!(target instanceof Player)) continue;

            if (((Player)target).getInventory().containsAtLeast(check, amount))
            {
                list.add(target);
            }
        }

        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
