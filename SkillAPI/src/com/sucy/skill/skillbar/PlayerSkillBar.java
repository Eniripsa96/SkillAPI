package com.sucy.skill.skillbar;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.skill.ClassSkill;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * A skill bar for a player
 */
public class PlayerSkillBar {

    private static final ItemStack EMPTY = new ItemStack(Material.PUMPKIN_SEEDS);
    private static final String
        ENABLED = "e";

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
    private final String name;
    private boolean enabled = true;
    private boolean setup = false;

    /**
     * Initial constructor
     *
     * @param plugin plugin reference
     * @param name   player name
     */
    public PlayerSkillBar(SkillAPI plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        for (int i = 1; i <= 9; i++) {
            if (plugin.getDefaultBar()[i - 1]) {
                slots.put(i, "");
            }
        }
    }

    /**
     * Constructor from config data
     *
     * @param plugin plugin reference
     * @param name   player name
     * @param config config to load from
     */
    public PlayerSkillBar(SkillAPI plugin, String name, ConfigurationSection config) {
        this(plugin, name);
        for (String key : config.getKeys(false)) {
            if (key.equals("e")) enabled = config.getBoolean(key);
            else if (plugin.getSkill(key) != null) {
                slots.put(config.getInt(key), key);
            }
            else slots.put(config.getInt(key), "");
        }
    }

    /**
     * @return whether or not the skill bar is enabled
     */
    public boolean isEnabled() {
        Player p = plugin.getServer().getPlayer(name);
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
     * @return name of the player owning the skill bar
     */
    public String getPlayerName() {
        return name;
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
        Player p = plugin.getServer().getPlayer(name);
        if (p == null) return -1;
        for (int slot : slots.keySet()) {
            if (p.getInventory().getItem(slot - 1) != null) {
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
        Player p = plugin.getServer().getPlayer(name);
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
            clear(plugin.getServer().getPlayer(name));
            enabled = false;
        }
        else {
            enabled = true;
            setup(plugin.getServer().getPlayer(name));
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

        // Toggle the slot
        clear(plugin.getServer().getPlayer(name));
        if (slots.containsKey(slot)) slots.remove(slot);
        else slots.put(slot, "");
        setup(plugin.getServer().getPlayer(name));
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
        plugin.getPlayer(name).castSkill(skill.getName());
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
     * Sets up the player for the skill bar
     *
     * @param player player to set up for
     */
    public void setup(HumanEntity player) {
        if (!isEnabled()) return;

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
        update();
        setup = true;
    }

    /**
     * Adds an unlocked skill to the skill bar
     *
     * @param skill unlocked skill
     */
    public void unlock(ClassSkill skill) {
        for (int i = 1; i <= 9; i++) {
            if (slots.containsKey(i) && slots.get(i).equals("")) {
                slots.put(i, skill.getName());
                update();
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
                slots.put(entry.getKey(), "");
                break;
            }
        }
        slots.put(slot + 1, skill.getName());
        update();
    }

    /**
     * Updates the player's skill bar icons
     */
    public void update() {
        PlayerSkills data = plugin.getPlayer(name);
        Player player = data.getPlayer();
        for (int i = 1; i <= 9; i++) {
            int index = i - 1;
            if (isWeaponSlot(index)) continue;

            ClassSkill skill = plugin.getSkill(slots.get(i));
            if (skill == null || !data.hasSkillUnlocked(skill.getName())) {
                if (isEnabled()) {
                    player.getInventory().setItem(index, EMPTY);
                }
            }
            else if (isEnabled()) {
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
        for (Map.Entry<Integer, String> entry : slots.entrySet()) {
            config.set(entry.getValue(), entry.getKey());
        }
    }
}
