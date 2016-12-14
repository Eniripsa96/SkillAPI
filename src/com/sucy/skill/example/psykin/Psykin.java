/**
 * SkillAPI
 * com.sucy.skill.example.psykin.Psykin
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

import com.sucy.skill.api.classes.ClassAttribute;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.listener.ListenerUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Psykin extends RPGClass implements Listener
{
    public static final String OFFENDER = "offender";

    public Psykin()
    {
        super("Psykin", makeIcon(), 100);

        settings.set(ClassAttribute.HEALTH, 20, 2);
        settings.set(ClassAttribute.MANA, 30, 4);

        setPrefix(ChatColor.DARK_PURPLE + "Psykin");
        setManaName(ChatColor.DARK_GREEN + "Capacity");
        setManaRegen(2);
        setAllowedExpSources(ExpSource.MOB, ExpSource.QUEST, ExpSource.COMMAND);

        addSkills(
            Retribution.NAME,
            Rupture.NAME
        );
    }

    private static ItemStack makeIcon()
    {
        ItemStack icon = new ItemStack(Material.BOOK);
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Psykin");
        List<String> lore = new ArrayList<String>();
        lore.add("");
        lore.add("A mysterious class known for");
        lore.add("their mystical powers.");
        meta.setLore(lore);
        icon.setItemMeta(meta);
        return icon;
    }

    @EventHandler
    public void onDamaged(EntityDamageByEntityEvent event)
    {
        LivingEntity offender = ListenerUtil.getDamager(event);
        if (offender != null)
            DynamicSkill.getCastData((LivingEntity) event.getEntity()).put(OFFENDER, offender);
    }
}
