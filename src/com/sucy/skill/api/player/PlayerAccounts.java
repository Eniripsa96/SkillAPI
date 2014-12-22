package com.sucy.skill.api.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerAccounts
{

    private final HashMap<String, PlayerData> classData = new HashMap<String, PlayerData>();

    private String        active;
    private OfflinePlayer player;
    private int           accounts;

    public PlayerAccounts(OfflinePlayer player)
    {
        this.player = player;

        PlayerData data = new PlayerData(player);
        classData.put("default", data);
        active = "default";
        accounts = 1;
    }

    public String getActiveId()
    {
        return active;
    }

    public PlayerData getActiveData()
    {
        return classData.get(active);
    }

    public Player getPlayer()
    {
        return player.getPlayer();
    }

    public String getPlayerName()
    {
        return player.getName();
    }

    public UUID getUUID()
    {
        return player.getUniqueId();
    }

    public int getAccountLimit()
    {
        return accounts;
    }

    public PlayerData getData(String key)
    {
        return classData.get(key);
    }

    public HashMap<String, PlayerData> getAllData()
    {
        return classData;
    }

    public void setAccount(String key)
    {
        if (classData.containsKey(key))
        {
            active = key;
        }
        else if (classData.size() >= accounts && accounts > 0)
        {

        }
    }
}
