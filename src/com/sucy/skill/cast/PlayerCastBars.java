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

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Representation of cast bar data for a single player
 */
public class PlayerCastBars
{
    private HashMap<Integer, String> hoverBar   = new HashMap<Integer, String>();
    private HashMap<Integer, String> instantBar = new HashMap<Integer, String>();

    private PlayerView view = PlayerView.INVENTORY;

    private ItemStack[] backup;

    private long cooldown;

    /**
     * Shows the hover cast bar to the player
     *
     * @param player player to show to
     */
    public void showHoverBar(Player player)
    {
        if (view != PlayerView.INVENTORY || hoverBar.size() == 0)
            return;

        view = PlayerView.HOVER_BAR;
        backup = player.getInventory().getContents();

        ItemStack[] contents = new ItemStack[36];
        makeContents(player, hoverBar, contents, 0);
        player.getInventory().setContents(contents);
    }

    /**
     * Shows the instant bar to the player
     *
     * @param player player to show to
     */
    public void showInstantBar(Player player)
    {
        if (view != PlayerView.INVENTORY || instantBar.size() == 0)
            return;

        view = PlayerView.INSTANT_BAR;
        backup = player.getInventory().getContents();

        ItemStack[] contents = new ItemStack[36];
        makeContents(player, instantBar, contents, 0);
        player.getInventory().setContents(contents);
    }

    /**
     * Adds an unlocked skill to the skill bars
     *
     * @param skill skill to add
     * @return null
     */
    public String unlock(PlayerSkill skill)
    {
        for (int i = 0; i < 9; i++)
            if (!hoverBar.containsKey(i))
                return hoverBar.put(i, skill.getData().getName());
        for (int i = 0; i < 9; i++)
            if (!instantBar.containsKey(i))
                return instantBar.put(i, skill.getData().getName());
        return null;
    }

    /**
     * Makes the contents for one of the views
     *
     * @param player   player to make them for
     * @param slots    slots to use
     * @param contents where to store the results
     * @param offset   starting index to add to
     */
    private void makeContents(Player player, HashMap<Integer, String> slots, ItemStack[] contents, int offset)
    {
        for (Map.Entry<Integer, String> slot : slots.entrySet())
        {
            contents[offset + slot.getKey()] = SkillAPI.getSkill(slot.getValue())
                .getIndicator(SkillAPI.getPlayerData(player).getSkill(slot.getValue()));
        }
    }
}
