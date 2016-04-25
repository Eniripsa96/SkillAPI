/**
 * SkillAPI
 * com.sucy.skill.example.psykin.Retribution
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package com.sucy.skill.example.psykin;

import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Retribution extends Skill implements SkillShot
{
    public static final String NAME = "Retribution";

    private static final String RADIUS = "radius";
    private static final String DELAY = "delay";

    public Retribution()
    {
        super(NAME, "Delayed AOE", makeIcon(), 10);
    }

    @Override
    public boolean cast(LivingEntity user, int level)
    {
        Object offender = DynamicSkill.getCastData(user).get(Psykin.OFFENDER);
        if (offender == null)
            return false;

        LivingEntity target = (LivingEntity) offender;

        return true;
    }

    private static ItemStack makeIcon()
    {
        ItemStack icon = new ItemStack(Material.DIAMOND);
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName("Ͽ=----=\u058E=----=Ͼ");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(" &d{name} &7[{level} / {max}] ");
        lore.add("");
        lore.add(" {req:level}Level: {attr:level}");
        lore.add(" {req:cost}Cost: {attr:cost}");
        lore.add("");
        lore.add(" &2Capacity: {attr:mana}");
        lore.add(" &2Cooldown: {attr:cooldown}");
        lore.add(" &2Radius: {attr:radius}");
        lore.add(" &2Delay: {attr:delay}");
        lore.add("");
        lore.add(" Builds up energy around the ");
        lore.add(" last foe that attacked you, ");
        lore.add(" exploding after a short time. ");
        meta.setLore(lore);
        icon.setItemMeta(meta);
        return icon;
    }
}
