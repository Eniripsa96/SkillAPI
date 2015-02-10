package com.sucy.skill.data.io;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import org.bukkit.OfflinePlayer;

/**
 * Base class for managers that handle saving and loading player data
 */
public abstract class IOManager
{
    /**
     * API reference
     */
    protected final SkillAPI api;

    /**
     * Initializes a new IO manager
     *
     * @param api SkillAPI reference
     */
    protected IOManager(SkillAPI api)
    {
        this.api = api;
    }

    /**
     * Loads data for the player
     *
     * @param player player to load for
     * @return loaded player data
     */
    public abstract PlayerAccounts loadData(OfflinePlayer player);

    /**
     * Saves the player's data
     *
     * @param data data to save
     */
    public abstract void saveData(PlayerAccounts data);

    /**
     * Saves all player data
     */
    public void saveAll()
    {
        for (PlayerAccounts data : SkillAPI.getPlayerAccountData().values())
        {
            saveData(data);
        }
    }
}
