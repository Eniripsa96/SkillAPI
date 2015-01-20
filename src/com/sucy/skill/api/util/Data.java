package com.sucy.skill.api.util;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class Data
{
    private static final String MAT = "icon";
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

    public static String serializeIcon(ConfigurationSection config)
    {
        config.set();
    }

    public static ItemStack parseIcon(ConfigurationSection config)
    {
        if (config == null)
        {
            return new ItemStack(Material.JACK_O_LANTERN);
        }

        try
        {
            ItemStack item = new ItemStack(parseMat(config.getString("icon", "JACK_O_LANTERN")));
            MaterialData data = new MaterialData(item.getType(), (byte))
        }
        catch (Exception ex)
        {
            return new ItemStack(Material.JACK_O_LANTERN);
        }
    }
}
