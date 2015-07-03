package com.sucy.skill.data.io;

import com.rit.sucy.config.CommentedConfig;
import com.rit.sucy.config.parse.DataSection;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * IO manager that saves/loads to a .yml configuration file
 */
public class ConfigIO extends IOManager
{
    /**
     * Initializes a new .yml config manager
     *
     * @param plugin SkillAPI reference
     */
    public ConfigIO(SkillAPI plugin)
    {
        super(plugin);
    }

    /**
     * Loads data for the given player
     *
     * @param player player to load data for
     *
     * @return loaded player data
     */
    @Override
    public PlayerAccounts loadData(OfflinePlayer player)
    {
        String playerKey = new VersionPlayer(player).getIdString();
        CommentedConfig config = new CommentedConfig(api, "players/" + playerKey);
        CommentedConfig nameConfig = new CommentedConfig(api, "players/" + player.getName());
        if (!playerKey.equals(player.getName()) && nameConfig.getConfigFile().exists())
        {
            DataSection old = nameConfig.getConfig();
            for (String key : old.keys())
            {
                config.getConfig().set(key, old.get(key));
            }
            nameConfig.getConfigFile().delete();
        }
        DataSection file = config.getConfig();

        return load(player, file);
    }

    /**
     * Saves player data to the config
     *
     * @param data data to save to the config
     */
    @Override
    public void saveData(PlayerAccounts data)
    {
        CommentedConfig config = new CommentedConfig(api, "players/" + new VersionPlayer(data.getPlayer()).getIdString());
        config.clear();

        DataSection file = save(data);
        config.getConfig().applyDefaults(file);

        config.save();
    }

    /**
     * Saves all player data to the config
     */
    @Override
    public void saveAll()
    {
        HashMap<String, PlayerAccounts> data = SkillAPI.getPlayerAccountData();
        ArrayList<String> keys = new ArrayList<String>(data.keySet());
        for (String key : keys)
        {
            saveData(data.get(key));
        }
    }
}
