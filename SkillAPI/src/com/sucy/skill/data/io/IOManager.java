package com.sucy.skill.data.io;

import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;

public abstract class IOManager
{

    protected final SkillAPI api;

    protected IOManager(SkillAPI api)
    {
        this.api = api;
    }

    public abstract PlayerAccounts loadData(VersionPlayer player);

    public abstract void saveData(PlayerAccounts data);

    public void saveAll()
    {

    }
}
