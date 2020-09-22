/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdBackup
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 Steven Sucy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.config.Filter;
import com.rit.sucy.config.parse.YAMLParser;
import com.rit.sucy.sql.direct.SQLDatabase;
import com.rit.sucy.sql.direct.SQLTable;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.data.Settings;
import com.sucy.skill.data.io.SQLIO;
import com.sun.xml.internal.fastinfoset.Encoder;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;

/**
 * Backs up SQL data into local config files
 */
public class CmdBackup implements IFunction
{
    private static final String BACKUP = "backup";
    private static final String FAILED = "failed";
    private static final String DONE   = "done";

    /**
     * Executes the command
     *
     * @param command owning command
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    arguments
     */
    @Override
    public void execute(ConfigurableCommand command, Plugin plugin, CommandSender sender, String[] args)
    {
        final SkillAPI api = (SkillAPI) plugin;
        command.sendMessage(sender, BACKUP, "&2Starting backup asynchronously...");
        new BackupTask(api, command, sender).runTaskAsynchronously(api);
    }

    /**
     * The task for backing up SQL data
     */
    private class BackupTask extends BukkitRunnable
    {
        private final ConfigurableCommand cmd;
        private final SkillAPI            api;
        private final CommandSender       sender;

        /**
         * @param api SkillAPI reference
         */
        BackupTask(SkillAPI api, ConfigurableCommand cmd, CommandSender sender)
        {
            this.api = api;
            this.cmd = cmd;
            this.sender = sender;
        }

        /**
         * Runs the backup task, backing up the entire SQL database locally
         */
        @Override
        public void run()
        {
            Settings settings = SkillAPI.getSettings();
            int count = 0;
            SQLDatabase database = new SQLDatabase(api, settings.getSQLHost(), settings.getSQLPort(), settings.getSQLDatabase(), settings.getSQLUser(), settings.getSQLPass());
            try
            {
                database.openConnection();
                SQLTable table = database.createTable(api, "players");
                ResultSet query = table.queryAll();

                final File file = new File(api.getDataFolder(), "players");
                file.mkdir();

                // Go through every entry, saving it to disk
                while (query.next())
                {
                    String sqlYaml = query.getString(SQLIO.DATA);
                    String yaml = YAMLParser.parseText(sqlYaml, SQLIO.STRING).toString();
                    String name = query.getString("Name");

                    FileOutputStream out = new FileOutputStream(new File(file, name + ".yml"));
                    BufferedWriter write = new BufferedWriter(new OutputStreamWriter(out, Encoder.UTF_8));

                    write.write(yaml);

                    write.close();

                    count++;
                }
                cmd.sendMessage(sender, DONE, "&2SQL database backup has finished successfully");
            }
            catch (Exception ex)
            {
                cmd.sendMessage(
                    sender,
                    FAILED,
                    "&4SQL database backup failed - backed up {amount} entries",
                    Filter.AMOUNT.setReplacement(count + "")
                );
                ex.printStackTrace();
            }
            database.closeConnection();
        }
    }
}
