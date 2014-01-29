package com.sucy.skill.quests;

import org.bukkit.Bukkit;

import java.io.*;

/**
 * Helper class for checking for Vault
 */
public class QuestsModuleManager {

    private static final String
        FOLDER = "plugins" + File.separator + "Quests" + File.separator + "modules",
        TARGET = FOLDER + File.separator + "SkillAPIModule.jar";

    /**
     * <p>Places the SkillAPI module for Quests into the proper directory</p>
     */
    public static void copyQuestsModule() {

        // Don't copy if it's already there
        File target = new File(TARGET);
        if (target.exists()) return;

        try {

            // Prepare to copy the file
            InputStream stream = QuestsModuleManager.class.getResourceAsStream("/SkillAPIModule.jar");
            OutputStream resStreamOut;
            int readBytes;
            byte[] buffer = new byte[4096];
            File folder = new File(FOLDER);
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
