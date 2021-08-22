package com.sucy.skill.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * SkillAPI © 2018
 * com.sucy.skill.util.FileReader
 */
public class FileReader {
    public static String readText(Class<?> clazz, String path) throws Exception {
        return readText(clazz.getResourceAsStream("/" + path));
    }

    public static String readText(final String path) throws Exception {
        return readText(new FileInputStream(path));
    }

    public static String readText(InputStream stream) throws Exception {
        try {
            StringBuilder builder = new StringBuilder();
            byte[] data = new byte[1024];

            int bytes;
            do {
                bytes = stream.read(data);
                builder.append(new String(data, 0, bytes, "UTF-8"));
            } while(bytes == 1024);

            stream.close();
            return builder.toString();
        } finally {
            try {
                stream.close();
            } catch (final IOException ex) {
                System.out.println("Failed to close stream");
            }
        }
    }
}
