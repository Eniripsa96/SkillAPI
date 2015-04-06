package com.sucy.skill.dynamic.mechanic;

import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Gives an item to each player target
 */
public class ItemMechanic extends EffectComponent
{
    private static final String MATERIAL = "material";
    private static final String AMOUNT   = "amount";
    private static final String DATA     = "data";
    private static final String CUSTOM   = "custom";
    private static final String NAME     = "name";
    private static final String LORE     = "lore";

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
        int data = settings.getInt(DATA, 0);
        ItemStack item = new ItemStack(material, amount, (short) data);

        boolean custom = settings.getString(CUSTOM, "false").toLowerCase().equals("true");
        if (custom)
        {
            ItemMeta meta = item.getItemMeta();
            String name = TextFormatter.colorString(settings.getString(NAME, ""));
            if (name.length() > 0)
            {
                meta.setDisplayName(name);
            }
            List<String> lore = TextFormatter.colorStringList(settings.getStringList(LORE));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        for (LivingEntity target : targets)
        {
            if (target instanceof Player)
            {
                ((Player) target).getInventory().addItem(item);
            }
        }
        return targets.size() > 0;
    }
}
