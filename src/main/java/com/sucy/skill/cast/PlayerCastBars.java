/**
 * SkillAPI
 * com.sucy.skill.cast.PlayerCastBars
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
package com.sucy.skill.cast;

import com.rit.sucy.config.parse.DataSection;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.task.PreviewTask;
import com.sucy.skill.thread.MainThread;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Representation of cast bar data for a single player
 */
public class PlayerCastBars implements InventoryHolder
{
    private HashMap<Integer, String> hoverBar   = new HashMap<Integer, String>();
    private HashMap<Integer, String> instantBar = new HashMap<Integer, String>();

    private HashSet<String> used   = new HashSet<String>();
    private HashSet<String> unused = new HashSet<String>();

    private PlayerView view = PlayerView.INVENTORY;

    private ItemStack[] backup;

    private PlayerData player;

    private PlayerSkill hovered;

    private long cooldown;

    private int oldSlot;

    /**
     * @param data player data reference
     */
    public PlayerCastBars(PlayerData data)
    {
        this.player = data;
    }

    /**
     * Validates added skills, making sure they're still unlocked
     */
    public void validate()
    {
        validate(hoverBar);
        validate(instantBar);
    }

    /**
     * @return true if in the hover view, false otherwise
     */
    public boolean isHovering()
    {
        return view == PlayerView.HOVER_BAR;
    }

    public PlayerView getView() {
        return view;
    }

    /**
     * Marks a skill as hovered
     *
     * @param slot skill slot
     */
    private void hoverSkill(Player player, int slot)
    {
        if (hoverBar.containsKey(slot))
        {
            hovered = this.player.getSkill(hoverBar.get(slot));
            hovered.initIndicators(player);
        }
        else hovered = null;
    }

    /**
     * Makes the packets for cast previews
     *
     * @param step animation step
     *
     * @return packet list or null if nothing is hovered
     *
     * @throws Exception
     */
    public List<Object> getHoverPackets(Player player, int step)
        throws Exception
    {
        if (hovered == null)
            return null;

        hovered.updateIndicators(player);
        return hovered.makePackets(step);
    }

    /**
     * Checks the skills assigned to a bar to make sure they are still unlocked
     *
     * @param map data of the bar to validate
     */
    private void validate(HashMap<Integer, String> map)
    {
        Iterator<Map.Entry<Integer, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<Integer, String> entry = iterator.next();
            if (!player.hasSkill(entry.getValue()) || !player.getSkill(entry.getValue()).isUnlocked())
            {
                iterator.remove();
                used.remove(entry.getValue());
            }
        }
    }

    /**
     * Restores the players inventory after
     * viewing one of the related views
     *
     * @param player player to restore
     */
    public void restore(Player player)
    {
        if (view == PlayerView.INVENTORY)
            return;

        // Update organizer data
        if (view == PlayerView.ORGANIZER)
        {
            reset();

            ItemStack[] contents = player.getInventory().getContents();
            update(contents, hoverBar, 0);
            update(contents, instantBar, 24);
        }

        // Restore player's items
        player.getInventory().setContents(backup);
        view = PlayerView.INVENTORY;
        player.getInventory().setHeldItemSlot(oldSlot);
    }

    /**
     * Opens the cast bar organizer GUI
     *
     * @param player player to open for
     *
     * @return true if opened
     */
    public boolean showOrganizer(Player player)
    {
        if (used.size() + unused.size() == 0 || view != PlayerView.INVENTORY)
            return false;

        view = PlayerView.ORGANIZER;
        backup = player.getInventory().getContents();

        // Set up player inventory for the different bars
        ItemStack[] playerContents = new ItemStack[36];
        playerContents[8] = SkillAPI.getSettings().getHoverItem();
        playerContents[35] = SkillAPI.getSettings().getInstantItem();
        fill(playerContents, hoverBar, 0);
        fill(playerContents, instantBar, 24);

        // Make the inventory for unused skills
        int size = Math.min(54, 9 * ((used.size() + unused.size() + 8) / 9));
        Inventory inv = player.getServer().createInventory(this, size);
        ItemStack[] contents = new ItemStack[size];
        int i = 0;
        int j = 9;
        for (String skill : unused)
        {
            if (i < contents.length)
                contents[i++] = makeIndicator(skill);
            else if (j < 24)
                playerContents[j++] = makeIndicator(skill);
        }

        // Apply layouts and open the view
        player.getInventory().setContents(playerContents);
        inv.setContents(contents);
        player.openInventory(inv);

        return true;
    }

    /**
     * Fills the contents with the skills in a cast bar
     *
     * @param contents contents to add to
     * @param bar      cast bar data
     * @param index    index to start at
     */
    private void fill(ItemStack[] contents, HashMap<Integer, String> bar, int index)
    {
        for (Map.Entry<Integer, String> entry : bar.entrySet())
            contents[index + entry.getKey()] = makeIndicator(entry.getValue());
    }

    /**
     * Updates the layout for a cast bar
     *
     * @param contents customizer GUI contents
     * @param bar      bar data to update
     * @param index    starting index
     */
    private void update(ItemStack[] contents, HashMap<Integer, String> bar, int index)
    {
        for (int i = 0; i < 8; i++)
        {
            if (contents[i + index] != null)
            {
                List<String> lore = contents[i + index].getItemMeta().getLore();
                String skill = lore.get(lore.size() - 1);
                if (unused.contains(skill))
                {
                    bar.put(i, skill);
                    used.add(skill);
                    unused.remove(skill);
                }
            }
        }
    }

    /**
     * Creates an indicator for use in the skill organize display
     *
     * @param skill skill to display
     *
     * @return makes a skill indicator, appending the skill name to the end for identification
     */
    private ItemStack makeIndicator(String skill)
    {
        if (skill == null) {
            return null;
        }
        ItemStack item = SkillAPI.getSkill(skill).getIndicator(this.player.getSkill(skill), true);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add(skill);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Shows the hover cast bar to the player
     *
     * @param player player to show to
     */
    public boolean showHoverBar(Player player)
    {
        boolean result = show(player, PlayerView.HOVER_BAR, hoverBar);
        player.getInventory().setHeldItemSlot(0);
        hoverSkill(player, 0);
        MainThread.register(new PreviewTask(player));
        return result;
    }

    /**
     * Shows the instant bar to the player
     *
     * @param player player to show to
     */
    public boolean showInstantBar(Player player)
    {
        return show(player, PlayerView.INSTANT_BAR, instantBar);
    }

    /**
     * Shows a cast bar to the player if requirements are met
     *
     * @param player player to show
     * @param view   view related to the bar
     * @param bar    bar data
     */
    private boolean show(Player player, PlayerView view, HashMap<Integer, String> bar)
    {
        long left = System.currentTimeMillis() - cooldown - SkillAPI.getSettings().getCastCooldown();
        if (this.view != PlayerView.INVENTORY || bar.size() == 0 || left < 0)
            return false;

        this.view = view;
        backup = player.getInventory().getContents();

        ItemStack[] contents = new ItemStack[36];
        makeContents(bar, contents, 0);
        player.getInventory().setContents(contents);

        return true;
    }

    /**
     * Handles changing to a different weapon slot
     *
     * @param event event details
     */
    public boolean handle(PlayerItemHeldEvent event)
    {
        switch (view)
        {
            case INSTANT_BAR:
                if (instantBar.containsKey(event.getNewSlot()))
                {
                    player.cast(instantBar.get(event.getNewSlot()));
                    cooldown = System.currentTimeMillis();
                }
                restore(event.getPlayer());
                event.setCancelled(true);
                return true;

            case HOVER_BAR:
                hoverSkill(event.getPlayer(), event.getNewSlot());
                return true;

            case INVENTORY:
                oldSlot = event.getPreviousSlot();
                return false;
        }

        return false;
    }

    /**
     * Handles a click event when in certain views
     *
     * @param player the player doing the interaction
     */
    public boolean handleInteract(Player player)
    {
        switch (view)
        {
            case INSTANT_BAR:
                restore(player);
                return true;

            case HOVER_BAR:
                if (hoverBar.containsKey(player.getInventory().getHeldItemSlot()))
                {
                    this.player.cast(hoverBar.get(player.getInventory().getHeldItemSlot()));
                    cooldown = System.currentTimeMillis();
                }
                restore(player);
                return true;
        }
        return false;
    }

    /**
     * Handles when the player opens an inventory
     *
     * @param player player to handle for
     */
    public void handleOpen(Player player)
    {
        if (view == PlayerView.HOVER_BAR || view == PlayerView.INSTANT_BAR)
            restore(player);
    }

    /**
     * Handles clicking in the GUI
     *
     * @param event event details
     */
    public void handle(InventoryClickEvent event)
    {
        if (event.getInventory() == event.getWhoClicked().getInventory())
        {
            if (event.getSlot() == 8 || event.getSlot() == 35)
                event.setCancelled(true);
        }
        else if (event.getSlot() < 0)
            event.setCancelled(true);
    }

    /**
     * Adds an unlocked skill to the skill bars
     *
     * @param skill skill to add
     */
    public void unlock(PlayerSkill skill)
    {
        if (!addTo(hoverBar, skill))
            addTo(instantBar, skill);
    }

    /**
     * Adds a skill to the first open slot in the bar
     *
     * @param bar   bar to add to
     * @param skill skill to add
     *
     * @return true if added, false if no room
     */
    private boolean addTo(HashMap<Integer, String> bar, PlayerSkill skill)
    {
        for (int i = 0; i < 9; i++)
        {
            if (!bar.containsKey(i))
            {
                add(bar, skill.getData().getName(), i);
                return true;
            }
        }
        return false;
    }

    /**
     * Sets a skill to the bar
     *
     * @param bar   bar to set to
     * @param skill skill to set
     * @param slot  slot to set to
     */
    private void add(HashMap<Integer, String> bar, String skill, int slot)
    {
        bar.put(slot, skill);
        used.add(skill);
        unused.remove(skill);
    }

    /**
     * Resets the layout and populates the unused list
     * with all available skills
     */
    public void reset()
    {
        unused.clear();
        used.clear();
        instantBar.clear();
        hoverBar.clear();
        for (PlayerSkill skill : player.getSkills())
            if (skill.isUnlocked() && skill.getData().canCast())
                unused.add(skill.getData().getName());
    }

    /**
     * Makes the contents for one of the views
     *
     * @param slots    slots to use
     * @param contents where to store the results
     * @param offset   starting index to add to
     */
    private void makeContents(HashMap<Integer, String> slots, ItemStack[] contents, int offset)
    {
        for (Map.Entry<Integer, String> slot : slots.entrySet())
        {
            PlayerSkill skill = this.player.getSkill(slot.getValue());
            if (skill != null) {
                contents[offset + slot.getKey()] = skill.getData().getIndicator(skill, true);
            }
        }
    }

    /**
     * Loads data from the config
     *
     * @param config config data
     * @param hover  whether or not it's for the hover bar
     */
    public void load(DataSection config, boolean hover)
    {
        if (config == null)
            return;

        HashMap<Integer, String> bar = hover ? hoverBar : instantBar;
        for (String key : config.keys())
            add(bar, key, config.getInt(key));
    }

    /**
     * Saves data to the config
     *
     * @param config config data
     * @param hover  whether or not it's for the hover bar
     */
    public void save(DataSection config, boolean hover)
    {
        HashMap<Integer, String> bar = hover ? hoverBar : instantBar;
        for (Map.Entry<Integer, String> entry : bar.entrySet())
            config.set(entry.getValue(), entry.getKey());
    }

    /**
     * Added to satisfy InventoryHolder, though doesn't do anything
     *
     * @return null
     */
    @Override
    public Inventory getInventory() { return null; }
}
