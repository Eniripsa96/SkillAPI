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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Loads player data from the SQL Database
 */
public class SQLIO extends IOManager
{
    private static final String ID     = "id";
    private static final String DATA   = "data";
    private static final char   STRING = '√';

    private boolean     startup;
    private SQLDatabase database;
    private SQLTable    table;

    /**
     * Initializes the SQL IO Manager
     *
     * @param api API reference
     */
    public SQLIO(SkillAPI api)
    {
        super(api);
        startup = true;
    }

    /**
     * Connects to the database
     */
    private void init()
    {
        if (database == null)
        {
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
    public void cleanup()
    {
        startup = false;
        if (database != null)
        {
            database.closeConnection();
            database = null;
        }
    }

    @Override
    public PlayerAccounts loadData(OfflinePlayer player)
    {
        if (player == null) return null;

        init();

        PlayerAccounts result = null;

        try
        {
            String playerKey = new VersionPlayer(player).getIdString();
            DataSection file = YAMLParser.parseText(table.createEntry(playerKey).getString(DATA), STRING);
            result = load(player, file);
        }
        catch (Exception ex)
        {
            Bukkit.getLogger().info("Failed to load data from the SQL Database - " + ex.getMessage());
        }

        if (!startup) cleanup();

        return result;
    }

    @Override
    public void saveData(PlayerAccounts data)
    {
        init();
        saveSingle(data);
        cleanup();
    }

    @Override
    public void saveAll()
    {
        init();
        HashMap<String, PlayerAccounts> data = SkillAPI.getPlayerAccountData();
        ArrayList<String> keys = new ArrayList<String>(data.keySet());
        for (String key : keys)
        {
            saveSingle(data.get(key));
        }
        cleanup();
    }

    private void saveSingle(PlayerAccounts data)
    {
        DataSection file = save(data);

        try
        {
            String playerKey = new VersionPlayer(data.getOfflinePlayer()).getIdString();
            table.createEntry(playerKey).set(DATA, file.toString(STRING));
        }
        catch (Exception ex)
        {
            Bukkit.getLogger().warning("Failed to save data for invalid player");
        }
    }
}
