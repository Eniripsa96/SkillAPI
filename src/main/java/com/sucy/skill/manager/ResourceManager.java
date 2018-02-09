/**
 * SkillAPI
 * com.sucy.skill.manager.ResourceManager
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
package com.sucy.skill.manager;

import com.sucy.skill.log.Logger;

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
            Logger.bug("Failed to copy resource: " + name);
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
