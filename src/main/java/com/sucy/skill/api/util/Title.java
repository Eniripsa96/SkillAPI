package com.sucy.skill.api.util;

import com.rit.sucy.reflect.Reflection;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static com.rit.sucy.reflect.Reflection.getNMSClass;

public class Title
{
    private static Constructor<?> timesConstructor;
    private static Constructor<?> contentConstructor;

    private static Class<?> packetTitle;

    private static Method   serialize;

    private static Object timesType;
    private static Object titleType;
    private static Object subtitleType;

    private static void loadClasses() throws Exception
    {
        packetTitle = getNMSClass("PacketPlayOutTitle");
        Class<?> chatBaseComponent = getNMSClass("IChatBaseComponent");
        serialize = chatBaseComponent.getDeclaredClasses()[0].getDeclaredMethod("a", String.class);

        Class<?> titleSerializer = packetTitle.getDeclaredClasses()[0];
        timesType = titleSerializer.getField("TIMES").get(null);
        titleType = titleSerializer.getField("TITLE").get(null);
        subtitleType = titleSerializer.getField("SUBTITLE").get(null);

        timesConstructor = packetTitle.getConstructor(titleSerializer, chatBaseComponent, int.class, int.class, int.class);
        contentConstructor = packetTitle.getConstructor(titleSerializer, chatBaseComponent);
    }

    public static void send(Player player, String title, String subtitle, int fadeIn, int duration, int fadeOut)
    {
        try
        {
            if (packetTitle == null)
                loadClasses();
            if (title != null)
                send(player, title, titleType, fadeIn, duration, fadeOut);
            if (subtitle != null)
                send(player, subtitle, subtitleType, fadeIn, duration, fadeOut);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void send(Player player, String text, Object type, int fadeIn, int duration, int fadeOut) throws Exception
    {
        Object chatText = serialize.invoke(null, "{\"text\":\"" + text + "\"}");

        Object packet = timesConstructor.newInstance(timesType, chatText, fadeIn, duration, fadeOut);
        Reflection.sendPacket(player, packet);

        packet = contentConstructor.newInstance(type, chatText);
        Reflection.sendPacket(player, packet);
    }
}