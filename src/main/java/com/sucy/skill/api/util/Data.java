/**
 * SkillAPI
 * com.sucy.skill.api.util.Data
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
package com.sucy.skill.api.util;

import com.rit.sucy.config.CustomFilter;
import com.rit.sucy.config.parse.DataSection;
import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.log.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Helper class for managing loading/saving certain data
 */
public class Data
{
    private static final String MAT  = "icon";
    private static final String DATA = "icon-data";
    private static final String LORE = "icon-lore";

    /**
     * Parses a material from a string
     *
     * @param name material name string
     *
     * @return parsed material or null if invalid
     */
    public static Material parseMat(String name)
    {
        try
        {
            return Material.valueOf(name.toUpperCase().replace(' ', '_'));
        }
        catch (Exception ex)
        {
            Logger.invalid("Failed to parse " + name);
            return null;
        }
    }

    /**
     * Serializes an item icon into a configuration
     *
     * @param item   item to serialize
     * @param config config to serialize into
     */
    public static void serializeIcon(ItemStack item, ConfigurationSection config)
    {
        config.set(MAT, item.getType().name());
        config.set(DATA, item.getData().getData());
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore())
        {
            List<String> lore = item.getItemMeta().getLore();
            lore.add(0, item.getItemMeta().getDisplayName());
            int count = lore.size();
            for (int i = 0; i < count; i++)
            {
                lore.add(lore.remove(0).replace(ChatColor.COLOR_CHAR, '&'));
            }
            config.set(LORE, lore);
        }
    }

    /**
     * Parses an item icon from a configuration
     *
     * @param config config to load from
     *
     * @return parsed item icon or a plain Jack O' Lantern if invalid
     */
    public static ItemStack parseIcon(ConfigurationSection config, CustomFilter... filters)
    {
        if (config == null)
        {
            return new ItemStack(Material.JACK_O_LANTERN);
        }

        try
        {
            ItemStack item = new ItemStack(parseMat(config.getString(MAT, "JACK_O_LANTERN")));
            short value = (short) config.getInt(DATA, 0);
            if (config.contains(LORE))
            {
                List<String> lore = TextFormatter.colorStringList(config.getStringList(LORE));
                if (lore.size() == 0)
                {
                    return item;
                }
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(lore.remove(0));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            item.setDurability(value);
            return item;
        }
        catch (Exception ex)
        {
            return new ItemStack(Material.JACK_O_LANTERN);
        }
    }

    /**
     * Serializes an item icon into a configuration
     *
     * @param item   item to serialize
     * @param config config to serialize into
     */
    public static void serializeIcon(ItemStack item, DataSection config)
    {
        config.set(MAT, item.getType().name());
        config.set(DATA, item.getData().getData());
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore())
        {
            List<String> lore = item.getItemMeta().getLore();
            lore.add(0, item.getItemMeta().getDisplayName());
            int count = lore.size();
            for (int i = 0; i < count; i++)
            {
                lore.add(lore.remove(0).replace(ChatColor.COLOR_CHAR, '&'));
            }
            config.set(LORE, lore);
        }
    }

    /**
     * Parses an item icon from a configuration
     *
     * @param config config to load from
     *
     * @return parsed item icon or a plain Jack O' Lantern if invalid
     */
    public static ItemStack parseIcon(DataSection config)
    {
        if (config == null)
        {
            return new ItemStack(Material.JACK_O_LANTERN);
        }

        try
        {
            ItemStack item = new ItemStack(parseMat(config.getString(MAT, "JACK_O_LANTERN")));
            short value = (short) config.getInt(DATA, 0);
            if (config.has(LORE))
            {
                List<String> lore = TextFormatter.colorStringList(config.getList(LORE));
                if (lore.size() == 0)
                {
                    return item;
                }
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(lore.remove(0));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            item.setDurability(value);
            return item;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return new ItemStack(Material.JACK_O_LANTERN);
        }
    }
}
