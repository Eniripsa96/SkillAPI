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
    public static final String
            QUESTS_FOLDER = "plugins" + File.separator + "Quests" + File.separator + "modules",
            SCHEME_FOLDER = "plugins" + File.separator + "SkillAPI" + File.separator + "img" + File.separator + "default";

    /**
     * Copies a resource embedded in the jar into the given folder
     *
     * @param name   name of the file
     * @param folder folder to put the file in
     */
    public static void copyResource(String name, String folder)
    {
        try
        {
            // Prepare to copy the file
            InputStream stream = ResourceManager.class.getResourceAsStream("/" + name);
            OutputStream resStreamOut;
            int readBytes;
            byte[] buffer = new byte[4096];
            File dir = new File(folder);
            dir.mkdirs();
            resStreamOut = new FileOutputStream(new File(dir + File.separator + name));

            // Copy to the file
            while ((readBytes = stream.read(buffer)) > 0)
            {
                resStreamOut.write(buffer, 0, readBytes);
            }

            // Close the streams
            stream.close();
            resStreamOut.close();
        }
        catch (Exception ex)
        {
            Bukkit.getLogger().info("Failed to copy resource: " + name);
        }
    }

    /**
     * <p>Places the SkillAPI module for Quests into the proper directory</p>
     */
    public static void copyQuestsModule()
    {
        copyResource("SkillAPIModule.jar", QUESTS_FOLDER);
    }
}
