package com.sucy.skill.api.player;

import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.data.AccountSettingsData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerAccounts
{

    private final HashMap<String, PlayerData> classData = new HashMap<String, PlayerData>();

    private SkillAPI      api;
    private String        active;
    private VersionPlayer player;
    private int           accounts;

    public PlayerAccounts(VersionPlayer player)
    {
        api = (SkillAPI) Bukkit.getPluginManager().getPlugin("SkillAPI");
        this.player = player;

        for (AccountSettingsData data : api.getSettings().getAccountSettings())
        {
            RPGClass rpgClass = api.getClass(data.getDefaultClass());
            PlayerData playerData = new PlayerData(api, player);
            classData.put(data.getKey().toLowerCase(), playerData);

            if (rpgClass != null)
            {
                playerData.setClass(rpgClass);
            }

            if (data.getPermission() == null && active == null)
            {
                active = data.getKey().toLowerCase();
            }
        }
    }

    public String getActiveId()
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
