package com.sucy.skill.skillbar;

import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.skill.ClassSkill;
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

/**
 * A skill bar for a player
 */
public class PlayerSkillBar {

    private static final ItemStack EMPTY = new ItemStack(Material.PUMPKIN_SEEDS);
    private static final String
        ENABLED = "e",
        SLOTS = "skill-slots",
        UNASSIGNED = "e";

    /**
     * Sets up the unassigned indicator
     */
    public static void setup() {
        ItemMeta meta = EMPTY.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Unassigned");
        EMPTY.setItemMeta(meta);
    }

    private final HashMap<Integer, String> slots = new HashMap<Integer, String>();
    private final SkillAPI plugin;
    private final VersionPlayer player;
    private boolean enabled = true;
    private boolean setup = false;

    /**
     * Initial constructor
     *
     * @param plugin plugin reference
     * @param player player reference
     */
    public PlayerSkillBar(SkillAPI plugin, VersionPlayer player) {
        this.plugin = plugin;
        this.player = player;
        for (int i = 1; i <= 9; i++) {
            if (plugin.getDefaultBar()[i - 1]) {
                slots.put(i, UNASSIGNED);
            }
        }
    }

    /**
     * Constructor from config data
     *
     * @param plugin plugin reference
     * @param player player name
     * @param config config to load from
     */
    public PlayerSkillBar(SkillAPI plugin, VersionPlayer player, ConfigurationSection config) {
        this.plugin = plugin;
        this.player = player;
        for (String key : config.getKeys(false)) {
            if (key.equals("e")) enabled = config.getBoolean(key);
            else if (key.equals(SLOTS)) {
                List<Integer> slots = config.getIntegerList(SLOTS);
                for (int i : slots) {
                    this.slots.put(i, UNASSIGNED);
                }
            }
            else if (plugin.getSkill(key) != null) {
                slots.put(config.getInt(key), key);
            }
        }
    }

    /**
     * @return whether or not the skill bar is enabled
     */
    public boolean isEnabled() {
        Player p = player.getPlayer();
        return enabled && p != null && p.getGameMode() != GameMode.CREATIVE;
    }

    /**
     * Whether or not the skill bar has been set up recently
     *
     * @return true if setup recently, false otherwise
     */
    public boolean isSetup() {
        return setup;
    }

    /**
     * <p>Retrieves the owner of the skill bar represented as a VersionPlayer</p>
     *
     * @return VersionPlayer of the owner
     */
    public VersionPlayer getOwner() {
        return player;
    }

    /**
     * @return name of the player owning the skill bar
     */
    public String getPlayerName() {
        return player.getName();
    }

    /**
     * Gets the player owning the skill bar
     *
     * @return owning player
     */
    public Player getPlayer() {
        return player.getPlayer();
    }

    /**
     * Retrieves the slot for the first weapon slot
     *
     * @return first weapon slot
     */
    public int getFirstWeaponSlot() {
        for (int i = 0; i < 9; i++) {
            if (isWeaponSlot(i)) return i;
        }
        return -1;
    }

    /**
     * <p>Counts the item in the owning player's inventory in the skill slots</p>
     * <p>If the player is offline, this returns -1</p>
     *
     * @return number of items in the skill slots
     */
    public int getItemsInSkillSlots() {
        int count = 0;
        Player p = player.getPlayer();
        if (p == null) return -1;
        for (int slot : slots.keySet()) {
            if (slot > 0 && slot < 10 && p.getInventory().getItem(slot - 1) != null) {
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
    public int countOpenSlots() {
        int count = 0;
        Player p = player.getPlayer();
        if (p == null) return -1;
        ItemStack[] items = p.getInventory().getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null && !slots.containsKey(i + 1)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Toggles the enabled state of the skill bar
     */
    public void toggleEnabled() {
        if (enabled) {
            clear(player.getPlayer());
            enabled = false;
        }
        else {
            enabled = true;
            setup(player.getPlayer());
        }
    }

    /**
     * Toggles a slot between weapon and skill
     *
     * @param slot slot to toggle
     */
    public void toggleSlot(int slot) {
        if (!isEnabled()) return;
        slot++;

        // Make sure there is always at least one weapon slot
        if (!slots.containsKey(slot) && (slots.size() == 8 || countOpenSlots() == 0)) {
            return;
        }

        // Cannot have item in cursor
        Player p = player.getPlayer();
        if (p == null || (p.getItemOnCursor() != null && p.getItemOnCursor().getType() != Material.AIR)) {
            return;
        }

        // Toggle the slot
        clear(p);
        if (slots.containsKey(slot)) slots.remove(slot);
        else slots.put(slot, UNASSIGNED);
        setup(p);
    }

    /**
     * Applies an action for the item slot
     *
     * @param slot slot to apply to
     */
    public void apply(int slot) {
        if (!isEnabled()) return;
        if (isWeaponSlot(slot)) return;
        ClassSkill skill = plugin.getSkill(slots.get(slot + 1));
        if (skill == null) return;
        plugin.getPlayer(player).castSkill(skill.getName());
    }

    /**
     * Clears the skill bar icons for the player
     *
     * @param player player to clear for
     */
    public void clear(HumanEntity player) {
        if (!isEnabled()) return;
        for (int i = 0; i < 9; i++) {
            if (isWeaponSlot(i)) continue;
            player.getInventory().setItem(i, null);
        }
    }

    /**
     * Clears the skill bar icons for the player and prevents them from dropping on death
     *
     * @param event death event of the player to clear for
     */
    public void clear(PlayerDeathEvent event) {
        if (!enabled || event.getEntity().getGameMode() == GameMode.CREATIVE) return;
        for (int i = 0; i < 9; i++) {
            if (isWeaponSlot(i)) continue;
            event.getDrops().remove(event.getEntity().getInventory().getItem(i));
            event.getEntity().getInventory().setItem(i, null);
        }
    }

    /**
     * Resets the skill bar
     */
    public void reset() {
        for (int i = 0; i < 9; i++) {
            if (isWeaponSlot(i)) continue;
            slots.put(i + 1, UNASSIGNED);
        }
    }

    /**
     * Sets up the player for the skill bar
     *
     * @param player player to set up for
     */
    public void setup(HumanEntity player) {
        if (!enabled || player.getGameMode() == GameMode.CREATIVE) return;

        // Disable the skill bar if there isn't enough space
        if (countOpenSlots() < getItemsInSkillSlots()) {
            enabled = false;
            return;
        }

        // Set it to a weapon slot
        if (!isWeaponSlot(player.getInventory().getHeldItemSlot())) {
            player.getInventory().setHeldItemSlot(getFirstWeaponSlot());
        }

        // Add in the skill indicators
        for (int i = 0; i < 9; i++) {
            if (isWeaponSlot(i)) continue;
            ItemStack item = player.getInventory().getItem(i);
            player.getInventory().setItem(i, EMPTY);
            if (item != null) player.getInventory().addItem(item);
        }

        // Update the slots
        update(player);
        setup = true;
    }

    /**
     * Adds an unlocked skill to the skill bar
     *
     * @param skill unlocked skill
     */
    public void unlock(ClassSkill skill) {
        for (int i = 1; i <= 9; i++) {
            if (slots.containsKey(i) && slots.get(i).equals(UNASSIGNED)) {
                slots.put(i, skill.getName());
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
    public void assign(ClassSkill skill, int slot) {
        if (isWeaponSlot(slot)) return;
        for (Map.Entry<Integer, String> entry : slots.entrySet()) {
            if (entry.getValue().equals(skill.getName())) {
                slots.put(entry.getKey(), UNASSIGNED);
                break;
            }
        }
        slots.put(slot + 1, skill.getName());
        update(player.getPlayer());
    }

    /**
     * Updates the player's skill bar icons
     */
    public void update(HumanEntity player) {
        PlayerSkills data = plugin.getPlayer(this.player);
        for (int i = 1; i <= 9; i++) {
            int index = i - 1;
            if (isWeaponSlot(index)) continue;

            ClassSkill skill = plugin.getSkill(slots.get(i));
            if (skill == null || !data.hasSkillUnlocked(skill.getName())) {
                slots.put(i, UNASSIGNED);
                if (enabled && player != null && player.getGameMode() != GameMode.CREATIVE) {
                    player.getInventory().setItem(index, EMPTY);
                }
            }
            else if (enabled && player != null && player.getGameMode() != GameMode.CREATIVE) {
                player.getInventory().setItem(index, skill.getIndicator(data));
            }
        }
    }

    /**
     * Checks if the slot is the weapon slot for the player
     *
     * @param slot slot to check
     * @return     true if weapon slot, false otherwise
     */
    public boolean isWeaponSlot(int slot) {
        return !slots.containsKey(slot + 1);
    }

    /**
     * Saves the bar data to a config
     *
     * @param config config to save to
     */
    public void save(ConfigurationSection config) {
        config.set(ENABLED, enabled);
        config.set(SLOTS, new ArrayList<Integer>(slots.keySet()));
        for (Map.Entry<Integer, String> entry : slots.entrySet()) {
            if (entry.getValue().equals(UNASSIGNED)) continue;
            config.set(entry.getValue(), entry.getKey());
        }
    }
}
