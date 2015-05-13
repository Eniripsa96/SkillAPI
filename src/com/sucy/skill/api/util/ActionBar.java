package com.sucy.skill.api.util;

import com.rit.sucy.reflect.Reflection;
import org.bukkit.Bukkit;
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

    private static boolean initialized = false;
    private static boolean supported = false;

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
            constructText = chatText.getConstructor(String.class);
            constructPacket = chatPacket.getConstructor(chatBase, byte.class);
            getHandle = craftPlayer.getDeclaredMethod("getHandle");

            supported = true;
        }
        catch (Exception ex)
        {
            Bukkit.getLogger().warning("Failed to setup Action Bar utility");
            ex.printStackTrace();
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
            Object text = constructText.newInstance(message);
            Object data = constructPacket.newInstance(text, (byte)2);
            Object handle = getHandle.invoke(player);
            Object connection = Reflection.getValue(handle, "playerConnection");
            Method send = Reflection.getMethod(connection, "sendPacket", packet);
            send.invoke(connection, data);
        }
        catch (Exception ex)
        {
            // Failed to send
            supported = false;
        }
    }
}
