package com.sucy.skill.api.player;

import com.sucy.skill.SkillAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerAccounts
{
    private final HashMap<Integer, PlayerData> classData = new HashMap<Integer, PlayerData>();

    private int           active;
    private OfflinePlayer player;

    public PlayerAccounts(OfflinePlayer player)
    {
        this.player = player;

        PlayerData data = new PlayerData(player);
        classData.put(1, data);
        active = 1;
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
        return SkillAPI.getSettings().getMaxAccounts(getPlayer());
    }

    public boolean hasData(int id)
    {
        return classData.containsKey(id);
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
        Player player = getPlayer();
        if (player == null || id == active)
        {
            return;
        }
        if (id <= getAccountLimit() && id > 0 && !classData.containsKey(id))
        {
            classData.put(id, new PlayerData(player));
        }
        if (classData.containsKey(id))
        {
            getActiveData().stopPassives(player);
            if (getActiveData().hasClass())
            {
                getActiveData().getSkillBar().clear(player);
            }
            active = id;
            getActiveData().startPassives(player);
            if (getActiveData().hasClass())
            {
                getActiveData().getSkillBar().setup(player);
            }
        }
    }
}
