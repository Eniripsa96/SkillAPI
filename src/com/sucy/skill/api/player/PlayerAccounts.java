package com.sucy.skill.api.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerAccounts
{
    private final HashMap<Integer, PlayerData> classData = new HashMap<Integer, PlayerData>();

    private int           active;
    private OfflinePlayer player;
    private int           accounts;

    public PlayerAccounts(OfflinePlayer player)
    {
        this.player = player;

        PlayerData data = new PlayerData(player);
        classData.put(1, data);
        active = 1;
        accounts = 1;
    }

    public int getActiveId()
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

    public PlayerData getData(int id)
    {
        return classData.get(id);
    }

    public HashMap<Integer, PlayerData> getAllData()
    {
        return classData;
    }

    public void setAccount(int id)
    {
        if (classData.containsKey(id))
        {
            active = id;
        }
        else if (id > 0)
        {

        }
    }
}
