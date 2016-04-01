/**
 * SkillAPI
 * com.sucy.skill.tools.GUITool
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
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.skills.Skill;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;

public class GUITool implements ToolMenu
{
    private static boolean inUse = false;

    public static boolean isInUse()
    {
        return inUse;
    }

    private static HashMap<String, GUIData> setups = new HashMap<String, GUIData>();

    private static CommentedConfig config;

    private static ItemStack
        NEXT,
        PREV,
        SHRINK,
        GROW,
        CLASS_SELECT,
        CLASS_DETAIL,
        SKILL_TREE;

    private static void init()
    {
        NEXT = make(Material.BOOK, ChatColor.GOLD + "Next");
        PREV = make(Material.BOOK, ChatColor.GOLD + "Previous");
        SHRINK = make(Material.MELON_SEEDS, ChatColor.GOLD + "Shrink", "", "Removes a row from the GUI");
        GROW = make(Material.MELON, ChatColor.GOLD + "Grow", "", "Adds a row to the GUI");
        CLASS_SELECT = make(Material.IRON_SWORD, ChatColor.GOLD + "Class Select", "", "Switches to editing", "class selection GUIs");
        CLASS_DETAIL = make(Material.ENCHANTMENT_TABLE, ChatColor.GOLD + "Class Details", "", "Swtiches to editing", "class detail GUIs");
        SKILL_TREE = make(Material.BOOKSHELF, ChatColor.GOLD + "Skill Tree", "", "Switches to editing", "skill tree GUIs");

        config = SkillAPI.getConfig("gui");
        DataSection data = config.getConfig();
        for (GUIType type : GUIType.values())
        {
            if (type == GUIType.CLASS_SELECTION)
            {
                if (data.has(type.name()))
                    setups.put(type.name(), new GUIData(data.getSection(type.name())));
                else
                    setups.put(type.name(), new GUIData());
            }
            else if (type == GUIType.CLASS_DETAILS)
            {
                for (RPGClass c : SkillAPI.getClasses().values())
                {
                    if (data.has(c.getName()))
                        setups.put(c.getName(), new GUIData(data.getSection(c.getName())));
                    else
                        setups.put(c.getName(), new GUIData());
                }
            }
            else if (type == GUIType.SKILL_TREE)
            {
                for (Skill skill : SkillAPI.getSkills().values())
                {
                    if (data.has(skill.getName()))
                        setups.put(skill.getName(), new GUIData(data.getSection(skill.getName())));
                    else
                        setups.put(skill.getName(), new GUIData());
                }
            }
        }
    }

    private static ItemStack make(Material mat, String name, String... lore)
    {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    private final Player player;

    private InventoryData data;

    private Inventory inventory;

    private GUIType type = GUIType.CLASS_SELECTION;
    private RPGClass rpgClass;
    private Skill    skill;

    private ItemStack[] playerContents;
    private ItemStack[] inventoryContents;

    public GUITool(Player player)
    {
        this.player = player;
        playerContents = new ItemStack[36];
    }

    public void open()
    {
        if (data != null && !inUse)
        {
            this.data = new InventoryData(player);
            GUIData data = setups.get(GUIType.CLASS_SELECTION.name());
            inventory = player.getServer().createInventory(this, data.getSize(), "SkillAPI - GUI Manager");
            player.openInventory(inventory);
            populate();

            inUse = true;
        }
    }

    private void populate()
    {
        player.getInventory().clear();
        playerContents = player.getInventory().getContents();
        inventory.clear();
        inventoryContents = inventory.getContents();

        playerContents[0] = PREV;
        playerContents[8] = NEXT;


        player.getInventory().setContents(playerContents);
    }

    private void populateClassSelection()
    {

    }

    private void populateClassDetails()
    {

    }

    private void populateSkillTree()
    {

    }

    @Override
    public void restore()
    {
        if (data != null)
        {
            data.restore(player);
            data = null;
            inUse = false;
        }
    }

    @Override
    public Inventory getInventory()
    {
        return inventory;
    }
}
