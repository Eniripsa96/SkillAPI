/**
 * SkillAPI
 * com.sucy.skill.data.io.ConfigIO
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.data.io;

import com.rit.sucy.config.CommentedConfig;
import com.rit.sucy.config.parse.DataSection;
import com.rit.sucy.version.VersionManager;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.log.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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

    public HashMap<String, PlayerAccounts> loadAll() {
        HashMap<String, PlayerAccounts> result = new HashMap<String, PlayerAccounts>();
        for (Player player : VersionManager.getOnlinePlayers()) {
            result.put(new VersionPlayer(player).getIdString(), loadData(player));
        }
        return result;
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
                config.getConfig().set(key, old.get(key));
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
        try
        {
            CommentedConfig config = new CommentedConfig(api, "players/" + new VersionPlayer(data.getOfflinePlayer()).getIdString());
            config.clear();

            DataSection file = save(data);
            config.getConfig().applyDefaults(file);

            config.save();
        }
        catch (Exception ex)
        {
            Logger.bug("Failed to save data for invalid player");
        }
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
            saveData(data.get(key));
    }
}
