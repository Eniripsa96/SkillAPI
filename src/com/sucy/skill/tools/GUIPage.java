/**
 * SkillAPI
 * com.sucy.skill.tools.GUIPage
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

import com.rit.sucy.config.parse.DataSection;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GUIPage
{
    private HashMap<String, Integer> slots  = new HashMap<String, Integer>();
    private HashMap<Integer, String> lookup = new HashMap<Integer, String>();

    public GUIPage() { }

    public GUIPage(DataSection data)
    {
        for (String key : data.keys())
        {
            slots.put(key, data.getInt(key));
        }
    }

    public String get(int index)
    {
        return lookup.get(index);
    }

    public int getIndex(String item)
    {
        if (!slots.containsKey(item))
            return -1;
        else
            return slots.get(item);
    }

    public void fill(ItemStack[] data)
    {
        for (Map.Entry<String, Integer> entry : slots.entrySet())
            data[entry.getValue()] = make(entry.getKey());
    }

    private ItemStack make(String key)
    {
        ItemStack item;
        if (SkillAPI.isSkillRegistered(key))
            item = SkillAPI.getSkill(key).getToolIndicator();
        else if (SkillAPI.isClassRegistered(key))
            item = SkillAPI.getClass(key).getToolIcon();
        else
            item = ToolSettings.getIcon(key);

        return item;
    }

    public void load(ItemStack[] data)
    {
        slots.clear();
        lookup.clear();
        for (int i = 0; i < data.length; i++)
        {
            ItemStack item = data[i];
            if (item == null)
                continue;

            String key = data[i].getItemMeta().getDisplayName();
            slots.put(key, i);
            lookup.put(i, key);
        }
    }

    public void save(DataSection data)
    {
        for (Map.Entry<String, Integer> entry : slots.entrySet())
        {
            data.set(entry.getKey(), entry.getValue());
        }
    }

    public boolean isValid()
    {
        return slots.size() > 0;
    }
}
