package com.sucy.skill.api.player;

import com.sucy.skill.SkillAPI;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerBar
{

    private static final ItemStack EMPTY   = new ItemStack(Material.PUMPKIN_SEEDS);
    private static final String
                                   ENABLED = "e",
            SLOTS                          = "skill-slots",
            UNASSIGNED                     = "e";

    public static void setup()
    {
        ItemMeta meta = EMPTY.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Empty Skill Slot");
        EMPTY.setItemMeta(meta);
    }

    private final HashMap<Integer, String> slots = new HashMap<Integer, String>();
    private final SkillAPI   plugin;
    private final PlayerData player;
    private boolean enabled = true;
    private boolean setup   = false;

    public PlayerBar(SkillAPI plugin, PlayerData player)
    {
        this.plugin = plugin;
        this.player = player;
        boolean[] layout = plugin.getSettings().getDefaultBarLayout();
        for (int i = 1; i <= 9; i++)
        {
            if (layout[i - 1])
            {
                slots.put(i, UNASSIGNED);
            }
        }
    }

    public PlayerBar(SkillAPI plugin, PlayerData player, ConfigurationSection config)
    {
        this.plugin = plugin;
        this.player = player;
        for (String key : config.getKeys(false))
        {
            if (key.equals("e"))
            {
                enabled = config.getBoolean(key);
            }
            else if (key.equals(SLOTS))
            {
                List<Integer> slots = config.getIntegerList(SLOTS);
                for (int i : slots)
                {
                    this.slots.put(i, UNASSIGNED);
                }
            }
            else if (plugin.getSkill(key) != null)
            {
                slots.put(config.getInt(key), key);
            }
        }

        boolean[] layout = plugin.getSettings().getDefaultBarLayout();
        boolean[] locked = plugin.getSettings().getLockedSlots();
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

    public boolean isEnabled()
    {
        Player p = player.getPlayer();
        return enabled && p != null && p.getGameMode() != GameMode.CREATIVE;
    }

    public boolean isSetup()
    {
        return setup;
    }

    public PlayerData getOwner()
    {
        return player;
    }

    public String getPlayerName()
    {
        return player.getPlayerName();
    }

    public Player getPlayer()
    {
        return player.getPlayer();
    }

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

    public int countOccupiedSkillSlots()
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

    public void toggleSlot(int slot)
    {
        if (!isEnabled())
        {
            return;
        }
        if (plugin.getSettings().getLockedSlots()[slot])
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

    public void apply(int slot)
    {
        if (!isEnabled())
        {
            return;
        }
        if (isWeaponSlot(slot))
        {
            return;
        }
        PlayerSkill skill = player.getSkill(slots.get(slot + 1));
        if (skill == null || !skill.isUnlocked())
        {
            return;
        }
        player.cast(skill.getData().getName());
    }

    public void clear(HumanEntity player)
    {
        if (!isEnabled())
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
    }

    public void clear(PlayerDeathEvent event)
    {
        if (!enabled || event.getEntity().getGameMode() == GameMode.CREATIVE)
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
    }

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
    }

    public void setup(HumanEntity player)
    {
        if (!enabled || player.getGameMode() == GameMode.CREATIVE)
        {
            return;
        }

        // Disable the skill bar if there isn't enough space
        if (countOpenSlots() < countOccupiedSkillSlots())
        {
            enabled = false;
            return;
        }

        // Set it to a weapon slot
        if (!isWeaponSlot(player.getInventory().getHeldItemSlot()))
        {
            player.getInventory().setHeldItemSlot(getFirstWeaponSlot());
        }

        // Add in the skill indicators
        for (int i = 0; i < 9; i++)
        {
            if (isWeaponSlot(i))
            {
                continue;
            }
            ItemStack item = player.getInventory().getItem(i);
            player.getInventory().setItem(i, EMPTY);
            if (item != null)
            {
                player.getInventory().addItem(item);
            }
        }

        // Update the slots
        update(player);
        setup = true;
    }

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

    public void assign(PlayerSkill skill, int slot)
    {
        if (skill.getPlayerData() != player)
        {
            throw new IllegalArgumentException("That skill does not belong to the player!");
        }
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

    public void update(HumanEntity player)
    {
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
                    player.getInventory().setItem(index, EMPTY);
                }
            }
            else if (enabled && player != null && player.getGameMode() != GameMode.CREATIVE)
            {
                player.getInventory().setItem(index, skill.getData().getIndicator(skill));
            }
        }
    }

    public boolean isWeaponSlot(int slot)
    {
        return !slots.containsKey(slot + 1);
    }

    public HashMap<Integer, String> getData()
    {
        return slots;
    }

    public void save(ConfigurationSection config)
    {
        config.set(ENABLED, enabled);
        config.set(SLOTS, new ArrayList<Integer>(slots.keySet()));
        for (Map.Entry<Integer, String> entry : slots.entrySet())
        {
            if (entry.getValue().equals(UNASSIGNED))
            {
                continue;
            }
            config.set(entry.getValue(), entry.getKey());
        }
    }
}
