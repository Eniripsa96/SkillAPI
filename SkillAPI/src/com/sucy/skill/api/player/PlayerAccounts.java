package com.sucy.skill.api.player;

import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerAccounts
{

    private final HashMap<Integer, PlayerData> classData = new HashMap<Integer, PlayerData>();

    private SkillAPI      api;
    private int           active;
    private VersionPlayer player;
    private int           accounts;

    public PlayerAccounts(VersionPlayer player)
    {
        api = (SkillAPI) Bukkit.getPluginManager().getPlugin("SkillAPI");
        active = api.getSettings().getDefaultAccounts();
        this.player = player;
    }

    public int getActiveId()
    {
        return active;
    }

    public PlayerData getActiveData()
    {
        return classData.get(active);
    }

    public VersionPlayer getVersionPlayer()
    {
        return player;
    }

    public Player getPlayer()
    {
        return player.getPlayer();
    }

    public boolean isLimitedAccounts()
    {
        return accounts <= 0;
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

    public void setAccountLimit(int limit)
    {
        this.accounts = limit;
    }

    public void changeAccount(int id)
    {
        if (classData.containsKey(id))
        {
            active = id;
        }
        else if (classData.size() >= accounts && accounts > 0)
        {

        }
    }
}
