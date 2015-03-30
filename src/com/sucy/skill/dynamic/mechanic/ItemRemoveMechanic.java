package com.sucy.skill.dynamic.mechanic;

import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.List;

/**
 * Gives an item to each player target
 */
public class ItemRemoveMechanic extends EffectComponent
{
    private static final String MATERIAL = "material";
    private static final String AMOUNT   = "amount";
    private static final String DATA     = "data";

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
        String mat = settings.getString(MATERIAL, "arrow").toUpperCase().replace(" ", "_");
        Material material;
        try
        {
            material = Material.valueOf(mat);
        }
        catch (Exception ex)
        {
            return false;
        }
        int amount = settings.getInt(AMOUNT, 1);
        short data = (short)settings.getInt(DATA, 0);
        ItemStack item = new ItemStack(material, amount, data);

        for (LivingEntity target : targets)
        {
            if (target instanceof Player)
            {
                ((Player) target).getInventory().remove(item);
            }
        }
        return targets.size() > 0;
    }
}
