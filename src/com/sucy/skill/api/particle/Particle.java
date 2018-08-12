/**
 * SkillAPI
 * com.sucy.skill.api.particle.Particle
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
package com.sucy.skill.api.particle;

import com.rit.sucy.version.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * Simplified particle utility compared to MCCore's
 */
public class Particle {
    private static Constructor<?> packet;

    private static Method toNms;
    private static Method getHandle;
    private static Method sendPacket;

    private static Field connection;

    private static HashMap<String, Object> particleTypes = new HashMap<>();

    /**
     * Initializes the SkillAPI particle utility
     */
    public static void init() {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().substring(23);
            String nms = "net.minecraft.server." + version + '.';
            String craft = "org.bukkit.craftbukkit." + version + '.';
            getHandle = Class.forName(craft + "entity.CraftPlayer").getMethod("getHandle");
            connection = Class.forName(nms + "EntityPlayer").getDeclaredField("playerConnection");
            sendPacket = Class.forName(nms + "PlayerConnection")
                    .getDeclaredMethod("sendPacket", Class.forName(nms + "Packet"));

            Class<?> packetClass;
            // 1.13+ Servers
            Class<?> particleEnum;
            if (VersionManager.isVersionAtLeast(11300)) {
                Class<?> craftParticle = Class.forName(craft + "CraftParticle");
                toNms = craftParticle.getDeclaredMethod("toNMS", org.bukkit.Particle.class, Object.class);
                particleEnum = Class.forName(nms + "ParticleParam");
                packetClass = Class.forName(nms + "PacketPlayOutWorldParticles");
                packet = packetClass.getConstructor(
                        particleEnum,
                        boolean.class,
                        float.class,
                        float.class,
                        float.class,
                        float.class,
                        float.class,
                        float.class,
                        float.class,
                        int.class);
            }

            // 1.8+ servers
            else if (VersionManager.isVersionAtLeast(VersionManager.V1_8_0)) {
                particleEnum = Class.forName(nms + "EnumParticle");
                for (Object value : particleEnum.getEnumConstants()) { particleTypes.put(value.toString(), value); }
                packetClass = Class.forName(nms + "PacketPlayOutWorldParticles");
                packet = packetClass.getConstructor(
                        particleEnum,
                        Boolean.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Integer.TYPE,
                        int[].class);
            }

            // 1.7.x servers
            else {
                packetClass = Class.forName(nms + "PacketPlayOutWorldParticles");
                packet = packetClass.getConstructor(
                        String.class,
                        Boolean.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Integer.TYPE);
            }
        } catch (Exception ex) {
            System.out.println("Failed to set up particles, are you using Cauldron?");
        }
    }

    /**
     * Sends a list of packets to a player
     *
     * @param player  player to send to
     * @param packets packets to send
     *
     * @throws Exception
     */
    public static void send(Player player, List<Object> packets)
            throws Exception {
        Object network = connection.get(getHandle.invoke(player));
        for (Object packet : packets) { sendPacket.invoke(network, packet); }
    }

    /**
     * Sends a list of packets to a player
     *
     * @param player  player to send to
     * @param packets packets to send
     *
     * @throws Exception when reflection fails
     */
    public static void send(Player player, Object[] packets)
            throws Exception {
        Object network = connection.get(getHandle.invoke(player));
        for (Object packet : packets) { sendPacket.invoke(network, packet); }
    }

    /**
     * Sends packets to all players within a range
     *
     * @param loc     location of the effect
     * @param packets packets from the effect
     * @param range   range to play for
     */
    public static void send(Location loc, List<Object> packets, double range)
            throws Exception {
        range *= range;
        for (Player player : loc.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(loc) < range) { send(player, packets); }
        }
    }

    /**
     * Sends packets to all players within a range
     *
     * @param loc     location of the effect
     * @param packets packets from the effect
     * @param range   range to play for
     */
    public static void send(Location loc, Object[] packets, double range)
            throws Exception {
        range *= range;
        for (Player player : loc.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(loc) < range) { send(player, packets); }
        }
    }

    /**
     * Make a particle packet using the given data
     *
     * @param settings particle details
     * @param loc      location to play at
     *
     * @return particle object or null if invalid
     *
     * @throws Exception
     */
    public static Object make(ParticleSettings settings, Location loc)
            throws Exception {
        return make(settings, loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * Make a particle packet using the given data
     *
     * @param settings particle details
     * @param x        X coordinate
     * @param y        Y coordinate
     * @param z        Z coordinate
     *
     * @return particle object or null if invalid
     *
     * @throws Exception
     */
    public static Object make(ParticleSettings settings, double x, double y, double z) throws Exception {
        // Invalid particle settings
        if (settings == null || settings.type == null) { return null; }

        return make(
                settings.type.name(),
                x,
                y,
                z,
                settings.dx,
                settings.dy,
                settings.dz,
                settings.speed,
                settings.amount,
                settings.material,
                settings.data);
    }

    public static Object make(
            final String name,
            double x,
            double y,
            double z,
            float dx,
            float dy,
            float dz,
            float speed,
            int amount,
            Material material,
            int data) throws Exception {

        // 1.13+ servers
        if (VersionManager.isVersionAtLeast(11300)) {
            Object obj = data;
            final org.bukkit.Particle bukkit = org.bukkit.Particle.valueOf(name);
            switch (bukkit) {
                case REDSTONE:
                    final Color color = Color.fromRGB((int) (255 * (dx + 1)), (int) (255 * dy), (int) (255 * dz));
                    dx = 0;
                    dy = 0;
                    dz = 0;
                    obj = new org.bukkit.Particle.DustOptions(color, amount);
                    break;
                case ITEM_CRACK:
                    obj = new ItemStack(material, 1, (short) 0, (byte) data);
                    break;
                case BLOCK_CRACK:
                case BLOCK_DUST:
                case FALLING_DUST:
                    obj = material.createBlockData();
            }
            final Object particle = toNms.invoke(null, bukkit, obj);
            return packet.newInstance(particle, true, (float) x, (float) y, (float) z, dx, dy, dz, speed, amount);
        }

        // 1.8+ servers use an enum value to validate the particle type
        else if (VersionManager.isVersionAtLeast(VersionManager.V1_8_0)) {
            Object enumType = particleTypes.get(name);
            return packet.newInstance(
                    enumType,
                    true,
                    (float) x,
                    (float) y,
                    (float) z,
                    dx,
                    dy,
                    dz,
                    speed,
                    amount,
                    material == null ? new int[0] : new int[] { material.ordinal(), data });
        }

        // 1.7.x servers just use a string for the type,
        // so make sure it is a usable type before blindly
        // sending it through
        else {
            return packet.newInstance(name, (float) x, (float) y, (float) z, dx, dy, dz, amount, 1);
        }
    }
}
