package com.sucy.skill.data.io;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import org.bukkit.OfflinePlayer;

public abstract class IOManager
{

    protected final SkillAPI api;

    protected IOManager(SkillAPI api)
    {
        this.api = api;
    }

    public abstract PlayerAccounts loadData(OfflinePlayer player);

    public abstract void saveData(PlayerAccounts data);

    public void saveAll()
    {
        for (PlayerAccounts data : api.getPlayerAccountData().values())
        {
            saveData(data);
        }
    }
}
