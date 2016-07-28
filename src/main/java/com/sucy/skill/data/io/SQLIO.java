/**
 * SkillAPI
 * com.sucy.skill.data.io.SQLIO
 * <p/>
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2014 Steven Sucy
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
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
import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.data.Settings;
import com.sucy.skill.log.Logger;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Loads player data from the SQL Database
 */
public class SQLIO extends IOManager {
    private static final String ID = "id";
    private static final String DATA = "data";
    private static final char STRING = '√';

    private boolean startup;
    private SQLDatabase database;
    private SQLTable table;

    /**
     * Initializes the SQL IO Manager
     *
     * @param api API reference
     */
    public SQLIO(SkillAPI api) {
        super(api);
        startup = true;
    }

    /**
     * Connects to the database
     */
    private void init() {
        if (database == null) {
            Settings settings = SkillAPI.getSettings();
            database = new SQLDatabase(api, settings.getSQLHost(), settings.getSQLPort(), settings.getSQLDatabase(), settings.getSQLUser(), settings.getSQLPass());
            database.openConnection();
            table = database.createTable(api, "players");

            table.createColumn(ID, ColumnType.INCREMENT);
            table.createColumn(DATA, ColumnType.TEXT);
        }
    }

    /**
     * Closes the database connection
     */
    public void cleanup() {
        startup = false;
        if (database != null) {
            database.closeConnection();
            database = null;
        }
    }

    @Override
    public PlayerAccounts loadData(OfflinePlayer player) {
        if (player == null) return null;

        init();

        PlayerAccounts result = null;

        try {
            String playerKey = new VersionPlayer(player).getIdString();
            DataSection file = YAMLParser.parseText(table.createEntry(playerKey).getString(DATA), STRING);
            result = load(player, file);
        } catch (Exception ex) {
            Logger.bug("Failed to load data from the SQL Database - " + ex.getMessage());
        }

        if (!startup) cleanup();

        return result;
    }

    @Override
    public void saveData(PlayerAccounts data) {
        init();
        saveSingle(data);
        cleanup();
    }

    @Override
    public void saveAll() {
        init();
        HashMap<String, PlayerAccounts> data = SkillAPI.getPlayerAccountData();
        ArrayList<String> keys = new ArrayList<String>(data.keySet());
        for (String key : keys) {
            saveSingle(data.get(key));
        }
        cleanup();
    }

    private void saveSingle(PlayerAccounts data) {
        DataSection file = save(data);

        try {
            String playerKey = new VersionPlayer(data.getOfflinePlayer()).getIdString();
            table.createEntry(playerKey).set(DATA, file.toString(STRING));
        } catch (Exception ex) {
            Logger.bug("Failed to save data for invalid player");
        }
    }
}
