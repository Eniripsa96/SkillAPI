/**
 * SkillAPI
 * com.sucy.skill.tools.ToolSettings
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
package com.sucy.skill.tools;

import com.rit.sucy.config.CommentedConfig;
import com.rit.sucy.config.parse.DataSection;
import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.log.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

/**
 * Handles loading tool related settings from the config and providing
 * them for other classes.
 */
public class ToolSettings
{
    private static HashMap<String, ItemStack> icons  = new HashMap<String, ItemStack>();
    private static HashMap<String, GUIData>   setups = new HashMap<String, GUIData>();

    /**
     * Loads custom icons for GUI tools from the config
     *
     * @param api API reference
     */
    public ToolSettings(SkillAPI api)
    {
        CommentedConfig config = new CommentedConfig(api, "tool");
        config.saveDefaultConfig();
        DataSection data = config.getConfig();
        for (String key : data.keys())
        {
            try
            {
                DataSection entry = data.getSection(key);
                ItemStack item = new ItemStack(Material.valueOf(entry.getString("type")));
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(TextFormatter.colorString(entry.getString("name")));
                meta.setLore(TextFormatter.colorStringList(entry.getList("lore")));
                item.setItemMeta(meta);
                icons.put(key.toLowerCase(), item);
            }
            catch (Exception ex)
            {
                Logger.invalid("Bad custom tool icon: " + key);
            }
        }

        data = new CommentedConfig(api, "guis").getConfig();
        for (GUIType type : GUIType.values())
        {
            setups.put(type.name(), new GUIData(data.getSection(type.name())));
        }
    }

    /**
     * Gets an icon by key
     *
     * @param key key to use
     * @return icon or null if not found
     */
    public static ItemStack getIcon(String key)
    {
        return icons.get(key.toLowerCase());
    }

    public static GUIData getSetup(GUIType type)
    {
        return setups.get(type.name());
    }
}
