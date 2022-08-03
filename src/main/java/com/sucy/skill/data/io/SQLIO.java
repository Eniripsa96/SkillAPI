/**
 * SkillAPI
 * com.sucy.skill.data.io.SQLIO
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

import com.rit.sucy.config.parse.DataSection;
import com.rit.sucy.config.parse.YAMLParser;
import com.rit.sucy.sql.ColumnType;
import com.rit.sucy.sql.direct.SQLDatabase;
import com.rit.sucy.sql.direct.SQLTable;
import com.rit.sucy.version.VersionManager;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.data.Settings;
import com.sucy.skill.log.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;

/**
 * Loads player data from the SQL Database
 */
public class SQLIO extends IOManager
{
    public static final String ID     = "id";
    public static final String DATA   = "data";
    public static final char QUOTE = '\'';
    
    private static HashMap<PlayerAccounts, Long> lastSaved;

    /**
     * Initializes the SQL IO Manager
     *
     * @param api API reference
     */
    public SQLIO(SkillAPI api)
    {
        super(api);
    	lastSaved = new HashMap<PlayerAccounts, Long>();
    }

    private SQLConnection openConnection() {
        SQLConnection connection = new SQLConnection();

        Settings settings = SkillAPI.getSettings();
        connection.database = new SQLDatabase(api, settings.getSQLHost(), settings.getSQLPort(), settings.getSQLDatabase(), settings.getSQLUser(), settings.getSQLPass());
        connection.database.openConnection();
        connection.table = connection.database.createTable(api, "players");

        connection.table.createColumn(ID, ColumnType.INCREMENT);
        connection.table.createColumn(DATA, ColumnType.TEXT);
        return connection;
    }

    @Override
    public HashMap<String, PlayerAccounts> loadAll() {
        SQLConnection connection = openConnection();

        HashMap<String, PlayerAccounts> result = new HashMap<String, PlayerAccounts>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            result.put(new VersionPlayer(player).getIdString(), load(connection, player));
        }

        connection.database.closeConnection();

        return result;
    }

    @Override
    public PlayerAccounts loadData(OfflinePlayer player)
    {
        if (player == null) return null;

        SQLConnection connection = openConnection();

        PlayerAccounts result = load(connection, player);

        connection.database.closeConnection();

        return result;
    }

    private PlayerAccounts load(SQLConnection connection, OfflinePlayer player) {
        String data = connection.table.createEntry(player.getUniqueId().toString()).getString(DATA);
        try
        {
        	DataSection file = YAMLParser.parseText(data, QUOTE);
            return load(player, file);
        }
        catch (Exception ex)
        {
            Logger.bug("Failed to load data for " + player.getName() + " from the SQL Database - " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveData(PlayerAccounts data)
    {
		long now = System.currentTimeMillis();
    	if (lastSaved.containsKey(data)) {
    		if (lastSaved.get(data) + 10000 >= now) {
    			return;
    		}
    	}
    	lastSaved.put(data, now);
		BukkitRunnable save = new BukkitRunnable() {
			public void run() {
		        SQLConnection connection = openConnection();
		        saveSingle(connection, data);
		        connection.database.closeConnection();
			}
		};
		save.runTaskAsynchronously(api);
    }

    @Override
    public void saveAll()
    {
        SQLConnection connection = openConnection();
        HashMap<String, PlayerAccounts> data = SkillAPI.getPlayerAccountData();
        ArrayList<String> keys = new ArrayList<String>(data.keySet());
        for (String key : keys)
        {
            saveSingle(connection, data.get(key));
        }
        connection.database.closeConnection();
    }

    private void saveSingle(SQLConnection connection, PlayerAccounts data)
    {
        if (data.getPlayer() != null && data.getPlayer().hasMetadata("NPC")) return;
        
        DataSection file = save(data);

        try
        {
			Logger.log("Successfully saved " + data.getPlayerName());
            connection.table.createEntry(data.getPlayer().getUniqueId().toString()).set(DATA, file.toString(QUOTE));
        }
        catch (Exception ex)
        {
            Logger.bug("Failed to save data for invalid player");
            ex.printStackTrace();
        }
    }

    private class SQLConnection {
        private SQLDatabase database;
        private SQLTable table;
    }
}
