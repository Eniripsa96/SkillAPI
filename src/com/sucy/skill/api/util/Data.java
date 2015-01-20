package com.sucy.skill.api.util;

import com.rit.sucy.text.TextFormatter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class Data
{
    private static final String MAT  = "icon";
    private static final String DATA = "icon-data";
    private static final String LORE = "icon-lore";

    public static Material parseMat(String name)
    {
        try
        {
            return Material.valueOf(name.toUpperCase().replace(' ', '_'));
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public static void serializeIcon(ItemStack item, ConfigurationSection config)
    {
        config.set(MAT, item.getType().name());
        config.set(DATA, item.getData().getData());
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore())
        {
            List<String> lore = item.getItemMeta().getLore();
            lore.add(0, item.getItemMeta().getDisplayName());
            config.set(LORE, lore);
        }
    }

    public static ItemStack parseIcon(ConfigurationSection config)
    {
        if (config == null)
        {
            return new ItemStack(Material.JACK_O_LANTERN);
        }

        try
        {
            ItemStack item = new ItemStack(parseMat(config.getString(MAT, "JACK_O_LANTERN")));
            MaterialData data = new MaterialData(item.getType(), (byte)config.getInt(DATA, 0));
            item.setData(data);
            if (config.contains(LORE))
            {
                List<String> lore = TextFormatter.colorStringList(config.getStringList(LORE));
                if (lore.size() == 0) return item;
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(lore.remove(0));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            return item;
        }
        catch (Exception ex)
        {
            return new ItemStack(Material.JACK_O_LANTERN);
        }
    }
}
