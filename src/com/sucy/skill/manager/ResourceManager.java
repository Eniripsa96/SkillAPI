package com.sucy.skill.manager;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Manages embedded resources within the .jar
 */
public class ResourceManager
{
    private static final String
            QUESTS_FOLDER = "plugins" + File.separator + "Quests" + File.separator + "modules",
            QUESTS_TARGET = QUESTS_FOLDER + File.separator + "SkillAPIModule.jar";

    /**
     * <p>Places the SkillAPI module for Quests into the proper directory</p>
     */
    public static void copyQuestsModule()
    {

        // Don't copy if it's already there
        File target = new File(QUESTS_TARGET);

        try
        {

            // Prepare to copy the file
            InputStream stream = ResourceManager.class.getResourceAsStream("/SkillAPIModule.jar");
            OutputStream resStreamOut;
            int readBytes;
            byte[] buffer = new byte[4096];
            File folder = new File(QUESTS_FOLDER);
            folder.mkdirs();
            resStreamOut = new FileOutputStream(target);

            // Copy to the file
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }

            // Close the streams
            stream.close();
            resStreamOut.close();
        }

        // An error occurred
        catch (Exception ex) {
            Bukkit.getLogger().severe("Failed to copy the module for Quests");
        }
    }
}
