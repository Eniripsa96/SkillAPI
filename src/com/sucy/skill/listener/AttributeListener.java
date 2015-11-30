package com.sucy.skill.listener;

import com.rit.sucy.items.InventoryManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ManaSource;
import com.sucy.skill.api.event.*;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.manager.AttributeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Listener for managing applying attribute bonuses for players
 */
public class AttributeListener implements Listener
{
    public static final String MENU_KEY = "skillAPIAttrMenu";

    private static HashMap<String, Double> bonuses = new HashMap<String, Double>();

    /**
     * Clears stored bonuses for the given player
     *
     * @param player player to clear bonuses for
     */
    public static void clearBonuses(Player player)
    {
        ArrayList<String> keys = new ArrayList<String>(bonuses.keySet());
        for (String key : keys)
        {
            if (key.startsWith(player.getName() + ":"))
            {
                bonuses.remove(key);
            }
        }
    }

    /**
     * Initializes the listener. This is automatically
     * handled by SkillAPI and shouldn't be
     * instantiated by other plugins.
     *
     * @param api SkillAPI reference
     */
    public AttributeListener(SkillAPI api)
    {
        api.getServer().getPluginManager().registerEvents(this, api);
    }

    /**
     * Gives players bonus stats on login
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event)
    {
        updatePlayer(SkillAPI.getPlayerData(event.getPlayer()));
    }

    /**
     * Clears stored bonuses for a player when they quit
     *
     * @param event event details
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        clearBonuses(event.getPlayer());
    }

    /**
     * Applies health and mana bonus attributes
     *
     * @param event event details
     */
    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent event)
    {
        updatePlayer(event.getPlayerData());
    }

    /**
     * Applies health and mana attribute bonuses on upgrading the attribute
     *
     * @param event event details
     */
    @EventHandler
    public void onInvest(PlayerUpAttributeEvent event)
    {
        updatePlayer(event.getPlayerData());
    }

    /**
     * Apply attributes to mana regen
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onManaRegen(PlayerManaGainEvent event)
    {
        // Bonus to regen from attributes
        if (event.getSource() == ManaSource.REGEN)
        {
            double newAmount = event.getPlayerData().scaleStat(AttributeManager.MANA_REGEN, event.getAmount());
            event.setAmount(newAmount);
        }
    }

    /**
     * Apply physical damage/defense attribute buffs
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPhysicalDamage(PhysicalDamageEvent event)
    {
        // Physical Damage
        if (event.getDamager() instanceof Player)
        {
            Player player = (Player) event.getDamager();
            PlayerData data = SkillAPI.getPlayerData(player);

            double newAmount = data.scaleStat(AttributeManager.PHYSICAL_DAMAGE, event.getDamage());
            event.setDamage(newAmount);
        }

        // Physical Defense
        if (event.getTarget() instanceof Player)
        {
            Player player = (Player) event.getTarget();
            PlayerData data = SkillAPI.getPlayerData(player);

            double newAmount = data.scaleStat(AttributeManager.PHYSICAL_DEFENSE, event.getDamage());
            event.setDamage(newAmount);
        }
    }

    /**
     * Apply skill damage/defense attribute buffs
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSkillDamage(SkillDamageEvent event)
    {
        // Skill Damage
        if (event.getDamager() instanceof Player)
        {
            Player player = (Player) event.getDamager();
            PlayerData data = SkillAPI.getPlayerData(player);

            double newAmount = data.scaleStat(AttributeManager.PHYSICAL_DAMAGE, event.getDamage());
            event.setDamage(newAmount);
        }

        // Skill Defense
        if (event.getTarget() instanceof Player)
        {
            Player player = (Player) event.getTarget();
            PlayerData data = SkillAPI.getPlayerData(player);

            double newAmount = data.scaleStat(AttributeManager.PHYSICAL_DEFENSE, event.getDamage());
            event.setDamage(newAmount);
        }
    }

    /**
     * Updates the stats of a player based on their current attributes
     *
     * @param data player to update
     */
    public static void updatePlayer(PlayerData data)
    {
        Player player = data.getPlayer();
        if (player != null)
        {
            double change = updateStat(data, AttributeManager.HEALTH, player.getMaxHealth());
            data.addMaxHealth(change);

            change = updateStat(data, AttributeManager.MANA, data.getMaxMana());
            data.addMaxMana(change);
        }
    }

    private static double updateStat(PlayerData data, String key, double value)
    {
        Player player = data.getPlayer();
        if (player != null)
        {
            String mapKey = player.getName() + ":" + key;
            double current = bonuses.containsKey(mapKey) ? bonuses.remove(mapKey) : 0;
            double updated = data.scaleStat(key, value - current) - value + current;
            bonuses.put(mapKey, updated);
            return updated - current;
        }
        return 0;
    }

    /**
     * Handles attribute menu interaction
     *
     * @param event event details
     */
    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        // Class selection
        if (InventoryManager.isMatching(event.getInventory(), MENU_KEY))
        {
            // Do nothing when clicking outside the inventory
            if (event.getSlot() == -999)
            {
                return;
            }

            boolean top = event.getRawSlot() < event.getView().getTopInventory().getSize();
            AttributeManager manager = SkillAPI.getAttributeManager();

            // Interact with the skill tree when clicking in the top region
            if (top)
            {
                if (event.getSlot() < manager.getKeys().size() || event.getCursor() != null)
                {
                    event.setCancelled(true);

                    PlayerData data = SkillAPI.getPlayerData((Player) event.getWhoClicked());
                    if (event.isRightClick() && SkillAPI.getSettings().isAttributesDowngrade())
                    {
                        data.refundAttribute(manager.getKeys().toArray()[event.getSlot()].toString());
                    }
                    else if (event.isLeftClick())
                    {

                        Object[] keys = manager.getKeys().toArray();
                        data.upAttribute(keys[event.getSlot()].toString());
                    }
                    data.openAttributeMenu();
                }
            }

            // Do not allow shift clicking items into the inventory
            else if (event.isShiftClick())
            {
                event.setCancelled(true);
            }
        }
    }
}
