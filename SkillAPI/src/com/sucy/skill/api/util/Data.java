package com.sucy.skill.api.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Data
{
    public static Material parseMat(String name) {
        try {
            return Material.valueOf(name.toUpperCase().replace(' ', '_'));
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static String serializeIcon(ItemStack item) {
        return item.getType().name() + "," + item.getData().getData();
    }

    public static ItemStack parseIcon(String data) {
        if (data == null) return new ItemStack(Material.APPLE);

        String[] pieces;
        if (data.contains(","))
        {
            pieces = data.split(",");
        }
        else
        {
            pieces = new String[] { data };
        }
        Material icon = parseMat(pieces[0]);
        if (icon == null)
        {
            return new ItemStack(Material.APPLE);
        }
        byte matData = 0;
        if (pieces.length > 1)
        {
            try
            {
                matData = Byte.parseByte(pieces[1]);
            }
            catch (Exception ex) {
                // Do nothing
            }
        }
        return new ItemStack(icon, 1, (short) 0, matData);
    }
}
