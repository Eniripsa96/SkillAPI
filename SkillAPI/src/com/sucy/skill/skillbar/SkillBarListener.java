package com.sucy.skill.skillbar;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.event.PlayerClassChangeEvent;
import com.sucy.skill.api.event.PlayerSkillDowngradeEvent;
import com.sucy.skill.api.event.PlayerSkillUnlockEvent;
import com.sucy.skill.api.event.PlayerSkillUpgradeEvent;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.TargetSkill;
import com.sucy.skill.config.Config;
import com.sucy.skill.tree.SkillTree;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;

import java.util.HashMap;

/**
 * Listener for skill bars
 */
public class SkillBarListener implements Listener {

    private final HashMap<String, PlayerSkillBar> skillBars = new HashMap<String, PlayerSkillBar>();
    private final SkillAPI plugin;
    private final Config config;

    /**
     * Constructor
     *
     * @param plugin plugin
     */
    public SkillBarListener(SkillAPI plugin) {
        this.plugin = plugin;
        PlayerSkillBar.setup();
        config = new Config(plugin, "skillBars");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        for (String key : config.getConfig().getKeys(false)) {
            skillBars.put(key, new PlayerSkillBar(plugin, key, config.getConfig().getConfigurationSection(key)));
            Player player = plugin.getServer().getPlayer(key);
            if (player != null) {
                skillBars.get(key).setup(player);
            }
        }
    }

    /**
     * Saves the data config
     */
    public void disable() {
        for (PlayerSkillBar bar : skillBars.values()) {
            bar.save(config.getConfig().createSection(bar.getPlayerName()));
            Player player = plugin.getServer().getPlayer(bar.getPlayerName());
            if (player != null) {
                bar.clear(player);
            }
        }
        config.saveConfig();
    }

    /**
     * Retrieves the skill bar for a player
     *
     * @param player player to retrieve for
     * @return       skill bar of the player
     */
    public PlayerSkillBar getSkillBar(HumanEntity player) {
        if (!skillBars.containsKey(player.getName())) {
            player.getInventory().setHeldItemSlot(8);
            PlayerSkillBar bar = new PlayerSkillBar(plugin, player.getName());
            skillBars.put(player.getName(),bar);
            bar.setup(player);
        }
        return skillBars.get(player.getName());
    }

    /**
     * Sets up skill bars on joining
     *
     * @param event event details
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.getPlayer(event.getPlayer().getName()).hasClass()) {
            getSkillBar(event.getPlayer()).setup(event.getPlayer());
        }
    }

    /**
     * Clears skill bars upon quitting the game
     *
     * @param event event details
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (plugin.getPlayer(event.getPlayer().getName()).hasClass()) {
            getSkillBar(event.getPlayer()).clear(event.getPlayer());
        }
    }

    /**
     * Manages setting up and clearing the skill bar when a player changes professions
     *
     * @param event event details
     */
    @EventHandler
    public void onProfess(PlayerClassChangeEvent event) {

        Player p = event.getPlayerData().getPlayer();

        // Professing as a first class sets up the skill bar
        if (event.getPreviousClass() == null && event.getNewClass() != null) {
            PlayerSkillBar bar = getSkillBar(p);
            if (!bar.isSetup()) bar.setup(p);
        }

        // Resetting your class clears the skill bar
        else if (event.getPreviousClass() != null && event.getNewClass() == null) {
            PlayerSkillBar bar = getSkillBar(p);
            bar.clear(p);
            bar.update();
        }
    }

    /**
     * Adds unlocked skills to the skill bar if applicable
     *
     * @param event event details
     */
    @EventHandler
    public void onUnlock(PlayerSkillUnlockEvent event) {
        if (event.getUnlockedSkill() instanceof DynamicSkill && !((DynamicSkill) event.getUnlockedSkill()).hasActiveEffects()) return;
        if (!(event.getUnlockedSkill() instanceof TargetSkill) && !(event.getUnlockedSkill() instanceof SkillShot)) return;
        getSkillBar(event.getPlayerData().getPlayer()).unlock(event.getUnlockedSkill());
    }

    /**
     * Updates the skill bar when a skill is upgraded
     *
     * @param event event details
     */
    @EventHandler
    public void onUpgrade(PlayerSkillUpgradeEvent event) {
        final Player player = event.getPlayerData().getPlayer();
        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                getSkillBar(player.getPlayer()).update();
            }
        }, 0);
    }

    /**
     * Updates a player's skill bar when downgrading a skill to level 0
     *
     * @param event event details
     */
    @EventHandler
    public void onDowngrade(PlayerSkillDowngradeEvent event) {
        getSkillBar(event.getPlayerData().getPlayer()).update();
    }

    /**
     * Event for assigning skills to the skill bar
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onAssign(InventoryClickEvent event) {

        // Players without a class aren't effected
        if (!plugin.getPlayer(event.getWhoClicked().getName()).hasClass()) {
            return;
        }

        // Disabled skill bars aren't affected either
        PlayerSkillBar skillBar = getSkillBar(event.getWhoClicked());
        if (!skillBar.isEnabled()) return;

        // Prevent moving skill icons
        plugin.getLogger().info("Hotbar: " + event.getHotbarButton());
        plugin.getLogger().info("Action: " + event.getAction().name());
        if (event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
            if (!skillBar.isWeaponSlot(event.getHotbarButton())) {
                event.setCancelled(true);
                plugin.getLogger().info("Was cancelled");
            }
        }
        else if (event.getSlotType() == InventoryType.SlotType.QUICKBAR) {
            int slot = event.getSlot();
            if (slot < 9 && slot >= 0) {
                if (event.getClick() == ClickType.RIGHT) {
                    event.setCancelled(true);
                    skillBar.toggleSlot(slot);
                }
                else if (!skillBar.isWeaponSlot(slot)) {
                    event.setCancelled(true);
                }
            }
        }

        // Make sure it's the right type of click action
        if (event.getAction() != InventoryAction.HOTBAR_MOVE_AND_READD && event.getAction() != InventoryAction.HOTBAR_SWAP) return;

        // Must be a skill tree
        if (event.getInventory().getHolder() instanceof SkillTree) {
            SkillTree tree = (SkillTree)event.getInventory().getHolder();

            // Must be hovering over a skill
            if (tree.isSkill(event.getWhoClicked(), event.getRawSlot())) {
                ClassSkill skill = tree.getSkill(event.getRawSlot());

                // Must be an active skill
                if ((skill instanceof DynamicSkill && ((DynamicSkill)skill).hasActiveEffects())
                        || (!(skill instanceof DynamicSkill) && (skill instanceof TargetSkill || skill instanceof SkillShot))) {

                    // Assign the skill
                    skillBar.assign(skill, event.getHotbarButton());
                }
            }
        }
    }

    /**
     * Applies skill bar actions when pressing the number keys
     *
     * @param event event details
     */
    @EventHandler
    public void onCast(PlayerItemHeldEvent event) {
        if (!plugin.getPlayer(event.getPlayer().getName()).hasClass()) return;

        PlayerSkillBar bar = getSkillBar(event.getPlayer());
        if (!bar.isWeaponSlot(event.getNewSlot()) && bar.isEnabled()) {
            event.setCancelled(true);
            bar.apply(event.getNewSlot());
        }
    }

    /**
     * Clears or sets up the skill bar upon changing from or to creative mode
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChangeMode(PlayerGameModeChangeEvent event) {

        // Clear on entering creative mode
        if (event.getNewGameMode() == GameMode.CREATIVE && plugin.getPlayer(event.getPlayer().getName()).hasClass()) {
            getSkillBar(event.getPlayer()).clear(event.getPlayer());
        }

        // Setup on leaving creative mode
        else if (event.getPlayer().getGameMode() == GameMode.CREATIVE && plugin.getPlayer(event.getPlayer().getName()).hasClass()) {
            final Player player = event.getPlayer();
            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    getSkillBar(player).setup(player);
                }
            }, 0);
        }
    }
}
