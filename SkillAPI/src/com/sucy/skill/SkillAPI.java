package com.sucy.skill;

import com.rit.sucy.config.LanguageConfig;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.data.ComboManager;
import com.sucy.skill.data.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class SkillAPI extends JavaPlugin
{

    private final HashMap<String, Skill>      skills  = new HashMap<String, Skill>();
    private final HashMap<String, RPGClass>   classes = new HashMap<String, RPGClass>();
    private final HashMap<String, PlayerData> players = new HashMap<String, PlayerData>();

    private LanguageConfig language;
    private ComboManager   comboManager;
    private Settings       settings;

    public Settings getSettings()
    {
        return settings;
    }

    public LanguageConfig getLanguage()
    {
        return language;
    }

    public ComboManager getComboManager()
    {
        return comboManager;
    }

    public Skill getSkill(String name)
    {
        return skills.get(name.toLowerCase());
    }

    public RPGClass getClass(String name)
    {
        return classes.get(name.toLowerCase());
    }

    public PlayerData getPlayerData(HumanEntity player)
    {
        return getPlayerData(new VersionPlayer(player));
    }

    public PlayerData getPlayerData(OfflinePlayer player)
    {
        return getPlayerData(new VersionPlayer(player));
    }

    public PlayerData getPlayerData(Player player)
    {
        return getPlayerData(new VersionPlayer(player));
    }

    public PlayerData getPlayerData(VersionPlayer player)
    {
        if (!players.containsKey(player.getIdString()))
        {
            PlayerData data = new PlayerData(this, player);
            players.put(player.getIdString(), data);
            return data;
        }
        else
        {
            return players.get(player.getIdString());
        }
    }
}
