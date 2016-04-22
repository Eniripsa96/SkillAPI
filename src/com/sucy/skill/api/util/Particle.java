/**
 * SkillAPI
 * com.sucy.skill.api.util.Particle
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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

import com.rit.sucy.version.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * Simplified particle utility compared to MCCore's
 */
public class Particle
{
    private static Constructor<?> packet;

    private static Class<?> particleEnum;

    private static Method getHandle;
    private static Method sendPacket;

    private static Field connection;

    private static HashMap<String, Object> particleTypes = new HashMap<String, Object>();

    /**
     * Initializes the SkillAPI particle utility
     */
    public static void init()
    {
        try
        {
            String version = Bukkit.getServer().getClass().getPackage().getName().substring(23);
            String nms = "net.minecraft.server." + version + '.';
            String craft = "org.bukkit.craftbukkit." + version + '.';
            getHandle = Class.forName(craft + "entity.CraftPlayer").getMethod("getHandle");
            connection = Class.forName(nms + "EntityPlayer").getDeclaredField("playerConnection");
            sendPacket = Class.forName(nms + "PlayerConnection").getDeclaredMethod("sendPacket", Class.forName(nms + "Packet"));

            // 1.8+ servers
            Class<?> packetClass;
            if (VersionManager.isVersionAtLeast(VersionManager.V1_8_0))
            {
                particleEnum = Class.forName(nms + "EnumParticle");
                System.out.println("Particles:");
                for (Object value : particleEnum.getEnumConstants())
                {
                    System.out.println("  - " + value.toString());
                    particleTypes.put(value.toString(), value);
                }
                packetClass = Class.forName(nms + "PacketPlayOutWorldParticles");
                packet = packetClass.getConstructor(particleEnum, Boolean.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Integer.TYPE, int[].class);
            }

            // 1.7.x servers
            else
            {
                packetClass = Class.forName(nms + "PacketPlayOutWorldParticles");
                packet = packetClass.getConstructor(String.class, Boolean.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Integer.TYPE);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Sends a list of packets to a player
     *
     * @param player  player to send to
     * @param packets packets to send
     * @throws Exception
     */
    public static void send(Player player, List<Object> packets)
        throws Exception
    {
        Object network = connection.get(getHandle.invoke(player));
        for (Object packet : packets)
            sendPacket.invoke(network, packet);
    }

    /**
     * Makes a particle packet using the given data
     *
     * @param type  type of particle
     * @param loc   location to show at
     * @param dx    DX value
     * @param dy    DY value
     * @param dz    DZ value
     * @param speed particle speed
     * @return particle object or null if invalid
     * @throws Exception
     */
    public static Object make(String type, Location loc, float dx, float dy, float dz, float speed)
        throws Exception
    {
        return make(type, loc.getX(), loc.getY(), loc.getZ(), dx, dy, dz, speed);
    }

    /**
     * Make a particle packet using the given data
     *
     * @param type  type of particle
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @param dx    DX value
     * @param dy    DY value
     * @param dz    DZ value
     * @param speed particle speed
     * @return particle object or null if invalid
     * @throws Exception
     */
    public static Object make(String type, double x, double y, double z, float dx, float dy, float dz, float speed)
        throws Exception
    {
        // 1.8+ servers use an enum value to validate the particle type
        if (VersionManager.isVersionAtLeast(VersionManager.V1_8_0))
        {
            if (CONVERSION.containsKey(type))
                type = CONVERSION.get(type);
            else
                type = type.toUpperCase().replace(" ", "_");

            Object enumType = particleTypes.get(type);
            if (enumType == null)
            {
                System.out.println("Null particle: " + type);
                return null;
            }

            return packet.newInstance(
                enumType,
                true,
                (float)x,
                (float)y,
                (float)z,
                dx,
                dy,
                dz,
                speed,
                1,
                new int[0]
            );
        }

        // 1.7.x servers just use a string for the type,
        // so make sure it is a usable type before blindly
        // sending it through
        else if (CONVERSION.containsKey(type))
        {
            return packet.newInstance(
                type,
                (float)x,
                (float)y,
                (float)z,
                dx,
                dy,
                dz,
                speed,
                1
            );
        }
        else return null;
    }

    /**
     * Particle conversion map
     */
    public static final HashMap<String, String> CONVERSION = new HashMap<String, String>()
    {{
            put("angryVillager", "VILLAGER_ANGRY");
            put("bubble", "WATER_BUBBLE");
            put("blockcrack_", "BLOCK_CRACK");
            put("blockdust_", "BLOCK_DUST");
            put("cloud", "CLOUD");
            put("crit", "CRIT");
            put("depthSuspend", "SUSPENDED_DEPTH");
            put("dripLava", "DRIP_LAVA");
            put("dripWater", "DRIP_WATER");
            put("enchantmenttable", "ENCHANTMENT_TABLE");
            put("explode", "EXPLOSION_NORMAL");
            put("fireworksSpark", "FIREWORKS_SPARK");
            put("flame", "FLAME");
            put("footstep", "FOOTSTEP");
            put("happyVillager", "VILLAGER_HAPPY");
            put("heart", "HEART");
            put("hugeexplosion", "EXPLOSION_HUGE");
            put("iconcrack_", "ITEM_CRACK");
            put("instantSpell", "SPELL_INSTANT");
            put("largeexplode", "EXPLOSION_LARGE");
            put("largesmoke", "SMOKE_LARGE");
            put("lava", "LAVA");
            put("magicCrit", "CRIT_MAGIC");
            put("mobSpell", "SPELL_MOB");
            put("mobSpellAmbient", "SPELL_MOB_AMBIENT");
            put("note", "NOTE");
            put("portal", "PORTAL");
            put("reddust", "REDSTONE");
            put("slime", "SLIME");
            put("snowballpoof", "SNOWBALL");
            put("snowshovel", "SNOW_SHOVEL");
            put("spell", "SPELL");
            put("splash", "WATER_SPLASH");
            put("suspend", "SUSPENDED");
            put("townaura", "TOWN_AURA");
            put("witchMagic", "SPELL_WITCH");
        }};
}
