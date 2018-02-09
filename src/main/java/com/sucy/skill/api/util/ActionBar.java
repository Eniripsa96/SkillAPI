/**
 * SkillAPI
 * com.sucy.skill.api.util.ActionBar
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
package com.sucy.skill.api.util;

import com.rit.sucy.reflect.Reflection;
import com.rit.sucy.text.TextFormatter;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.log.Logger;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Handles sending text to players using the action bar.
 */
public class ActionBar
{
    private static Class<?> craftPlayer;
    private static Class<?> chatPacket;
    private static Class<?> packet;
    private static Class<?> chatText;
    private static Class<?> chatBase;

    private static Method getHandle;

    private static Constructor<?> constructPacket;
    private static Constructor<?> constructText;

    private static Object messageType = (byte)2;

    private static boolean initialized = false;
    private static boolean supported   = false;

    private static void initialize()
    {
        initialized = true;
        try
        {
            craftPlayer = Reflection.getCraftClass("entity.CraftPlayer");
            chatPacket = Reflection.getNMSClass("PacketPlayOutChat");
            packet = Reflection.getNMSClass("Packet");
            chatBase = Reflection.getNMSClass("IChatBaseComponent");
            chatText = Reflection.getNMSClass("ChatComponentText");
            if (VersionManager.isVersionAtLeast(11200)) {
                Class<?> chatMessageType = Reflection.getNMSClass("ChatMessageType");
                messageType = chatMessageType.getMethod("a", byte.class).invoke(null, messageType);
                constructPacket = chatPacket.getConstructor(chatBase, chatMessageType);
            }
            else {
                constructPacket = chatPacket.getConstructor(chatBase, byte.class);
            }
            constructText = chatText.getConstructor(String.class);
            getHandle = craftPlayer.getDeclaredMethod("getHandle");

            supported = true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.invalid("Failed to setup Action Bar utility - not supported on pre-1.8 servers or brand new servers");
        }
    }

    /**
     * Checks whether or not the action bar is supported
     *
     * @return true if supported, false otherwise
     */
    public static boolean isSupported()
    {
        if (!initialized) initialize();
        return supported;
    }

    /**
     * Shows an action bar message to the given player
     *
     * @param player  player to show the message to
     * @param message message to show
     */
    public static void show(Player player, String message)
    {
        if (!initialized) initialize();
        if (!isSupported()) return;

        try
        {
            Object text = constructText.newInstance(TextFormatter.colorString(message));
            Object data = constructPacket.newInstance(text, messageType);
            Object handle = getHandle.invoke(player);
            Object connection = Reflection.getValue(handle, "playerConnection");
            Method send = Reflection.getMethod(connection, "sendPacket", packet);
            send.invoke(connection, data);
        }
        catch (Exception ex)
        {
            Logger.bug("Failed to apply Action Bar");
            ex.printStackTrace();
            // Failed to send
            supported = false;
        }
    }
}
