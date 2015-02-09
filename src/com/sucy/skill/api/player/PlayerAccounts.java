package com.sucy.skill.api.player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerAccountChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Represents the collection of accounts owned by a single player.
 * Most of the time, this class won't be used by other plugins as
 * you can skip directly to a player's active data using the
 * SkillAPI.getPlayerData methods. This would be if you want
 * to extend functionality for handling the inactive accounts.
 */
public class PlayerAccounts
{
    private final HashMap<Integer, PlayerData> classData = new HashMap<Integer, PlayerData>();

    private int           active;
    private OfflinePlayer player;

    /**
     * Initializes a new container for player account data.
     * This shouldn't be used by other plugins as the API
     * provides one for each player already.
     *
     * @param player player to store data for
     */
    public PlayerAccounts(OfflinePlayer player)
    {
        this.player = player;

        PlayerData data = new PlayerData(player, true);
        classData.put(1, data);
        active = 1;
    }

    /**
     * Retrieves the active account ID for the player
     *
     * @return active account ID
     */
    public int getActiveId()
    {
        return active;
    }

    /**
     * Retrieves the active account data for the player
     *
     * @return active account data
     */
    public PlayerData getActiveData()
    {
        return classData.get(active);
    }

    /**
     * Gets the Bukkit player object for the owner of the data
     *
     * @return Bukkit player object or null if offline/dead
     */
    public Player getPlayer()
    {
        return player.getPlayer();
    }

    /**
     * Gets the name of the owner of the data
     *
     * @return owner's name
     */
    public String getPlayerName()
    {
        return player.getName();
    }

    /**
     * Retrieves the max amount of accounts the owner can use
     *
     * @return available account number
     */
    public int getAccountLimit()
    {
        return SkillAPI.getSettings().getMaxAccounts(getPlayer());
    }

    /**
     * Checks whether or not there is any data for the given account ID. If
     * the player has not switched to the account, there will be no data
     * unless the setting to initialize one account for each class is enabled.
     *
     * @param id account ID
     * @return true if data exists, false otherwise
     */
    public boolean hasData(int id)
    {
        return classData.containsKey(id);
    }

    /**
     * Gets the account data by ID for the owner
     *
     * @param id account ID
     * @return account data or null if not found
     */
    public PlayerData getData(int id)
    {
        return classData.get(id);
    }

    /**
     * Gets the account data by ID for the owner. If no data
     * exists under the given ID, new data is created as long
     * as the ID is a positive integer (not necessarily in
     * bounds for the player's allowed accounts).
     *
     * @param id     account ID
     * @param player offline player reference
     * @param init   whether or not the data is being initialized
     * @return account data or null if invalid id or player
     */
    public PlayerData getData(int id, OfflinePlayer player, boolean init)
    {
        if (!hasData(id) && id > 0 && player != null)
        {
            classData.put(id, new PlayerData(player, init));
        }
        return classData.get(id);
    }

    /**
     * Retrieves all of the data for the owner. Modifying this map will
     * alter the player's actual data.
     *
     * @return all account data for the player
     */
    public HashMap<Integer, PlayerData> getAllData()
    {
        return classData;
    }

    /**
     * Switches the active account for the player by ID. This will not accept
     * IDs outside the player's account limits. If the player is offline or
     * dead, this will not do anything.
     *
     * @param id ID of the account to switch to
     */
    public void setAccount(int id)
    {
        Player player = getPlayer();
        if (player == null || id == active)
        {
            return;
        }
        if (id <= getAccountLimit() && id > 0 && !classData.containsKey(id))
        {
            classData.put(id, new PlayerData(player, false));
        }
        if (classData.containsKey(id))
        {
            PlayerAccountChangeEvent event = new PlayerAccountChangeEvent(this, active, id);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled())
            {
                return;
            }

            getActiveData().stopPassives(player);
            if (getActiveData().hasClass())
            {
                getActiveData().getSkillBar().clear(player);
            }
            active = event.getNewID();
            getActiveData().startPassives(player);
            getActiveData().updateScoreboard();
            if (getActiveData().hasClass())
            {
                getActiveData().getSkillBar().setup(player);
            }
        }
    }
}
