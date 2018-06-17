/**
 * SkillAPI
 * com.sucy.skill.api.player.PlayerSkillBar
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
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
package com.sucy.skill.api.player;

import com.sucy.skill.SkillAPI;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * A skill bar for a player
 */
public class PlayerSkillBar
{
    private static final String
        UNASSIGNED = "e";

    private final HashMap<Integer, String> slots = new HashMap<Integer, String>();
    private final PlayerData player;
    private boolean enabled = true;
    private boolean setup   = false;

    /**
     * Initial constructor
     *
     * @param player owning player data
     */
    public PlayerSkillBar(PlayerData player)
    {
        this.player = player;
        for (int i = 1; i <= 9; i++)
        {
            if (SkillAPI.getSettings().getDefaultBarLayout()[i - 1])
            {
                slots.put(i, UNASSIGNED);
            }
        }
    }

    /**
     * @return whether or not the skill bar is enabled
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Whether or not the skill bar has been set up recently
     *
     * @return true if setup recently, false otherwise
     */
    public boolean isSetup()
    {
        return setup;
    }

    /**
     * <p>Retrieves the owner of the skill bar represented as a VersionPlayer</p>
     *
     * @return VersionPlayer of the owner
     */
    public PlayerData getPlayerData()
    {
        return player;
    }

    /**
     * @return name of the player owning the skill bar
     */
    public Player getPlayer()
    {
        return player.getPlayer();
    }

    /**
     * Retrieves the slot for the first weapon slot
     *
     * @return first weapon slot
     */
    public int getFirstWeaponSlot()
    {
        for (int i = 0; i < 9; i++)
        {
            if (isWeaponSlot(i))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * <p>Counts the item in the owning player's inventory in the skill slots</p>
     * <p>If the player is offline, this returns -1</p>
     *
     * @return number of items in the skill slots
     */
    public int getItemsInSkillSlots()
    {
        int count = 0;
        Player p = player.getPlayer();
        if (p == null)
        {
            return -1;
        }
        for (int slot : slots.keySet())
        {
            if (slot > 0 && slot < 10 && p.getInventory().getItem(slot - 1) != null)
            {
                count++;
            }
        }
        return count;
    }

    /**
     * <p>Counts the number of open slots in the player's
     * inventory besides skill slots</p>
     * <p>This returns -1 if the player is offline</p>
     *
     * @return open slots in the players inventory
     */
    public int countOpenSlots()
    {
        int count = 0;
        Player p = player.getPlayer();
        if (p == null)
        {
            return -1;
        }
        ItemStack[] items = p.getInventory().getContents();
        for (int i = 0; i < items.length; i++)
        {
            if (items[i] == null && !slots.containsKey(i + 1))
            {
                count++;
            }
        }
        return count;
    }

    /**
     * Toggles the enabled state of the skill bar
     */
    public void toggleEnabled()
    {
        if (enabled)
        {
            clear(player.getPlayer());
            enabled = false;
        }
        else
        {
            enabled = true;
            setup(player.getPlayer());
        }
    }

    /**
     * Toggles a slot between weapon and skill
     *
     * @param slot slot to toggle
     */
    public void toggleSlot(int slot)
    {
        if (!isEnabled() || SkillAPI.getSettings().getLockedSlots()[slot])
        {
            return;
        }
        slot++;

        // Make sure there is always at least one weapon slot
        if (!slots.containsKey(slot) && (slots.size() == 8 || countOpenSlots() == 0))
        {
            return;
        }

        // Cannot have item in cursor
        Player p = player.getPlayer();
        if (p == null || (p.getItemOnCursor() != null && p.getItemOnCursor().getType() != Material.AIR))
        {
            return;
        }

        // Toggle the slot
        clear(p);
        if (slots.containsKey(slot))
        {
            slots.remove(slot);
        }
        else
        {
            slots.put(slot, UNASSIGNED);
        }
        setup(p);
    }

    /**
     * Applies an action for the item slot
     *
     * @param slot slot to apply to
     */
    public void apply(int slot)
    {
        if (getPlayer() == null || getPlayer().getGameMode() == GameMode.CREATIVE)
        {
            return;
        }
        if (!isEnabled())
        {
            return;
        }
        if (isWeaponSlot(slot))
        {
            return;
        }
        PlayerSkill skill = player.getSkill(slots.get(slot + 1));
        if (skill == null)
        {
            return;
        }
        player.cast(skill);
    }

    /**
     * Clears the skill bar icons for the player
     *
     * @param player player to clear for
     */
    public void clear(HumanEntity player)
    {
        if (player == null || !setup)
        {
            return;
        }
        for (int i = 0; i < 9; i++)
        {
            if (isWeaponSlot(i))
            {
                continue;
            }
            player.getInventory().setItem(i, null);
        }
        setup = false;
    }

    /**
     * Clears the skill bar icons for the player and prevents them from dropping on death
     *
     * @param event death event of the player to clear for
     */
    public void clear(PlayerDeathEvent event)
    {
        if (event == null || !setup)
        {
            return;
        }
        for (int i = 0; i < 9; i++)
        {
            if (isWeaponSlot(i))
            {
                continue;
            }
            event.getDrops().remove(event.getEntity().getInventory().getItem(i));
            event.getEntity().getInventory().setItem(i, null);
        }
        setup = false;
    }

    /**
     * Resets the skill bar
     */
    public void reset()
    {
        for (int i = 0; i < 9; i++)
        {
            if (isWeaponSlot(i))
            {
                continue;
            }
            slots.put(i + 1, UNASSIGNED);
        }
        update(getPlayer());
    }

    /**
     * Sets up the player for the skill bar
     *
     * @param player player to set up for
     */
    public void setup(HumanEntity player)
    {
        if (player == null || !enabled || player.getGameMode() == GameMode.CREATIVE || setup)
        {
            return;
        }

        // Disable the skill bar if there isn't enough space
        if (countOpenSlots() < getItemsInSkillSlots())
        {
            enabled = false;
            return;
        }

        // Set it to a weapon slot
        if (!isWeaponSlot(player.getInventory().getHeldItemSlot()))
        {
            int slot = getFirstWeaponSlot();
            if (slot == -1)
            {
                toggleSlot(0);
                slot = 0;
            }
            player.getInventory().setHeldItemSlot(slot);
        }

        // Add in the skill indicators
        for (int i = 0; i < 9; i++)
        {
            if (isWeaponSlot(i))
            {
                continue;
            }
            ItemStack item = player.getInventory().getItem(i);
            player.getInventory().setItem(i, SkillAPI.getSettings().getUnassigned());
            if (item != null)
            {
                player.getInventory().addItem(item);
            }
        }

        // Update the slots
        setup = true;
        update(player);
    }

    /**
     * Adds an unlocked skill to the skill bar
     *
     * @param skill unlocked skill
     */
    public void unlock(PlayerSkill skill)
    {
        for (int i = 1; i <= 9; i++)
        {
            if (slots.containsKey(i) && slots.get(i).equals(UNASSIGNED))
            {
                slots.put(i, skill.getData().getName());
                update(player.getPlayer());
                return;
            }
        }
    }

    /**
     * Assigns the skill to the slot
     *
     * @param skill skill to assign
     * @param slot  slot to assign to
     */
    public void assign(PlayerSkill skill, int slot)
    {
        if (isWeaponSlot(slot))
        {
            return;
        }
        for (Map.Entry<Integer, String> entry : slots.entrySet())
        {
            if (entry.getValue().equals(skill.getData().getName()))
            {
                slots.put(entry.getKey(), UNASSIGNED);
                break;
            }
        }
        slots.put(slot + 1, skill.getData().getName());
        update(player.getPlayer());
    }

    /**
     * Updates the player's skill bar icons
     */
    public void update(HumanEntity player)
    {
        if (!setup)
        {
            setup(player);
            return;
        }
        for (int i = 1; i <= 9; i++)
        {
            int index = i - 1;
            if (isWeaponSlot(index))
            {
                continue;
            }

            PlayerSkill skill = this.player.getSkill(slots.get(i));
            if (skill == null || !skill.isUnlocked())
            {
                slots.put(i, UNASSIGNED);
                if (enabled && player != null && player.getGameMode() != GameMode.CREATIVE)
                {
                    player.getInventory().clear(index);
                    player.getInventory().setItem(index, SkillAPI.getSettings().getUnassigned());
                }
            }
            else if (isEnabled() && player != null)
            {
                player.getInventory().setItem(index, skill.getData().getIndicator(skill));
            }
        }
    }

    /**
     * Updates the displayed cooldown for the skill bar
     */
    public void updateCooldowns()
    {
        Player player = getPlayer();
        if (!setup || !enabled || player == null) return;

        for (int i = 0; i < 9; i++)
        {
            if (!isWeaponSlot(i))
            {
                ItemStack item = player.getInventory().getItem(i);
                if (item == null)
                {
                    update(player);
                    item = player.getInventory().getItem(i);
                }
                PlayerSkill skill = this.player.getSkill(slots.get(i + 1));
                if (skill != null && skill.isUnlocked())
                {
                    int amount = Math.max(1, skill.getCooldown());
                    if (item.getAmount() != amount)
                    {
                        item.setAmount(amount);
                        player.getInventory().clear(i);
                        player.getInventory().setItem(i, item);
                    }
                }
            }
        }
    }

    /**
     * Checks if the slot is the weapon slot for the player
     *
     * @param slot slot to check
     *
     * @return true if weapon slot, false otherwise
     */
    public boolean isWeaponSlot(int slot)
    {
        return !slots.containsKey(slot + 1);
    }

    /**
     * <p>Retrieves the data for the skill bar.</p>
     * <p>The key is the slot of the hotbar.</p>
     * <p>The value is the skill assigned to the slot.</p>
     * <p>Modifying this map will change the player's skill bar data.</p>
     *
     * @return skill bar data
     */
    public HashMap<Integer, String> getData()
    {
        return slots;
    }

    /**
     * Applies setting data to the skill bar, applying locked slots
     * if they aren't matching.
     */
    public void applySettings()
    {
        boolean[] layout = SkillAPI.getSettings().getDefaultBarLayout();
        boolean[] locked = SkillAPI.getSettings().getLockedSlots();
        for (int i = 1; i <= 9; i++)
        {
            if (locked[i - 1])
            {
                if (layout[i - 1])
                {
                    if (!slots.containsKey(i))
                    {
                        slots.put(i, UNASSIGNED);
                    }
                }
                else if (slots.containsKey(i))
                {
                    slots.remove(i);
                }
            }
        }
    }
}
