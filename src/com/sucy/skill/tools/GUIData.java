/**
 * SkillAPI
 * com.sucy.skill.tools.GUIData
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
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GUIData
{
    private static final String
        ROWS = "rows",
        PAGES = "pages",
        SLOTS = "slots";

    private final HashMap<Integer, GUIPage> pageMap = new HashMap<Integer, GUIPage>();

    private int rows  = 3;
    private int pages = 1;
    private int nav   = 0;

    public GUIData()
    {
        pageMap.put(0, new GUIPage());
    }

    public GUIData(DataSection data)
    {
        if (data != null)
        {
            rows = data.getInt(ROWS, rows);
            this.pages = data.getInt(PAGES, this.pages);
            DataSection pages = data.getSection(SLOTS);
            if (pages != null)
                for (String page : pages.keys())
                    this.pageMap.put(Integer.parseInt(page), new GUIPage(pages.getSection(page)));
        }
        while (pageMap.size() < pages)
            pageMap.put(pageMap.size(), new GUIPage());
    }

    public GUIPage getPage()
    {
        return pageMap.get(nav);
    }

    public int getSize()
    {
        return rows * 9;
    }

    public void init(ItemStack[] contents)
    {
        nav = 0;
        fill(contents);
    }

    public void load(ItemStack[] contents)
    {
        pageMap.get(nav).load(contents);
    }

    public void fill(ItemStack[] contents)
    {
        pageMap.get(nav).fill(contents);
    }

    public void next(ItemStack[] contents)
    {
        nav = (nav + 1) % pages;
        fill(contents);
    }

    public void prev(ItemStack[] contents)
    {
        nav = (nav + pages - 1) % pages;
        fill(contents);
    }

    public int getPages()
    {
        return pages;
    }

    public void addPage()
    {
        pageMap.put(nav + 1, new GUIPage());
        pages += 1;
        nav++;
    }

    public void removePage()
    {
        pageMap.remove(nav);
        pages -= 1;
        nav = Math.min(nav, pages - 1);
    }

    public void shrink()
    {
        if (rows > 1)
            rows--;

        for (GUIPage page : pageMap.values())
            page.remove(getSize(), getSize() + 9);
    }

    public void grow()
    {
        if (rows < 6)
            rows++;
    }

    public boolean isValid()
    {
        for (GUIPage page : pageMap.values())
            if (page.isValid())
                return true;

        return false;
    }

    public void save(DataSection data)
    {
        data.set(ROWS, rows);
        data.set(PAGES, pages);
        DataSection slots = data.createSection(SLOTS);
        for (Map.Entry<Integer, GUIPage> entry : pageMap.entrySet())
        {
            entry.getValue().save(slots.createSection(entry.getKey().toString()));
        }
    }
}
