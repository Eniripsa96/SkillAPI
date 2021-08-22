/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.ItemMechanic
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.dynamic.mechanic;

import com.rit.sucy.text.TextFormatter;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Gives an item to each player target
 */
public class ItemMechanic extends MechanicComponent
{
    private static final String MATERIAL = "material";
    private static final String AMOUNT   = "amount";
    private static final String DATA     = "data";
    private static final String BYTE     = "byte";
    private static final String CUSTOM   = "custom";
    private static final String NAME     = "name";
    private static final String LORE     = "lore";

    @Override
    public String getKey() {
        return "item";
    }

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
        int durability = settings.getInt(DATA, 0);
        int data = settings.getInt(BYTE, 0);
        ItemStack item = new ItemStack(material, amount, (short) durability, (byte) data);

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

        boolean worked = false;
        for (LivingEntity target : targets)
        {
            if (target instanceof Player)
            {
                worked = ((Player) target).getInventory().addItem(item).isEmpty() || worked;
            }
        }
        return targets.size() > 0;
    }
}
