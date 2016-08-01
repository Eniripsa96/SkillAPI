/**
 * SkillAPI
 * com.sucy.skill.tools.GUIHolder
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

import com.sucy.skill.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public abstract class GUIHolder<T extends IconHolder> implements InventoryHolder
{
    protected HashMap<String, ? extends IconHolder> data = new HashMap<String, IconHolder>();

    protected GUIData    gui;
    protected PlayerData player;
    protected Inventory  inventory;
    protected int        page;

    public void set(GUIData gui, PlayerData player, Inventory inv, HashMap<String, T> data)
    {
        this.gui = gui;
        this.player = player;
        this.inventory = inv;
        this.data = data;

        onSetup();
    }

    public void next()
    {
        setPage((page + 1) % gui.getPages());
    }

    public void prev()
    {
        setPage((page + gui.getPages() - 1) % gui.getPages());
    }

    public void setPage(int page)
    {
        this.page = page;
        ItemStack[] contents = gui.getPage(page).instance(player, data);
        inventory.setContents(contents);
    }

    public void handleDrag(InventoryDragEvent event)
    {
        event.setCancelled(true);
    }

    @SuppressWarnings("unchecked")
    public void handleClick(InventoryClickEvent event)
    {
        event.setCancelled(true);
        boolean top = event.getRawSlot() < event.getView().getTopInventory().getSize();
        String result = gui.getPage(page).get(event.getSlot());
        if (top && result != null && data.containsKey(result))
        {
            onClick((T)data.get(result), event.getSlot(), event.isLeftClick(), event.isShiftClick());
        }
    }

    public void handleClose(InventoryCloseEvent event)
    {
        onClose((Player)event.getPlayer());
    }

    protected abstract void onClick(T type, int slot, boolean left, boolean shift);

    protected void onSetup() { }

    protected void onClose(Player player) { }

    @Override
    public Inventory getInventory()
    {
        return inventory;
    }
}
