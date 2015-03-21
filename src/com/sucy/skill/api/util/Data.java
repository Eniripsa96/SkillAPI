package com.sucy.skill.api.util;

import com.rit.sucy.config.CustomFilter;
import com.rit.sucy.config.parse.DataSection;
import com.rit.sucy.text.TextFormatter;
import org.bukkit.Bukkit;
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
            Bukkit.getLogger().info("Failed to parse " + name);
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
    public static ItemStack parseIcon(ConfigurationSection config, CustomFilter ... filters)
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
            Bukkit.getLogger().info("Null config");
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
