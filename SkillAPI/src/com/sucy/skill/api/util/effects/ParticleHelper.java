package com.sucy.skill.api.util.effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Random;

/**
 * Utility class for doing particle effects
 */
public class ParticleHelper {

    private static final String VERSION = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
    private static final Random random = new Random();

    /**
     * Randomly plays particle effects within the circle
     *
     * @param loc       location to center the effect around
     * @param type      type of particle to use
     * @param radius    radius of the circle
     * @param amount    amount of particles to use
     * @param direction direction to orientate the circle
     */
    public static void fillCircle(Location loc, ParticleType type, int radius, int amount, Direction direction) {
        fillCircle(loc, type, radius, 0, amount, direction);
    }

    /**
     * Randomly plays particle effects within the circle
     *
     * @param loc       location to center the effect around
     * @param type      type of particle to use
     * @param data      data value to use
     * @param radius    radius of the circle
     * @param amount    amount of particles to use
     * @param direction direction to orientate the circle
     */
    public static void fillCircle(Location loc, ParticleType type, int data, int radius, int amount, Direction direction) {
        Location temp = loc.clone();
        int rSquared = radius * radius;
        int twoRadius = radius * 2;
        int index = 0;

        // Play the particles
        while (index < amount) {
            if (direction == Direction.XY || direction == Direction.XZ)
                temp.setX(loc.getX() + random.nextFloat() * twoRadius - radius);
            if (direction == Direction.XY || direction == Direction.YZ)
                temp.setY(loc.getY() + random.nextFloat() * twoRadius - radius);
            if (direction == Direction.XZ || direction == Direction.YZ)
                temp.setZ(loc.getZ() + random.nextFloat() * twoRadius - radius);

            if (temp.distanceSquared(loc) > rSquared) continue;

            if (!OTHER_VALUES.containsKey(data) && type == ParticleType.OTHER) data = 0;
            if (type == ParticleType.OTHER) send(OTHER_VALUES.get(data), loc, 25);
            else loc.getWorld().playEffect(temp, type.getEffect(), data);
            index++;
        }
    }

    /**
     * Randomly plays particle effects within the sphere
     *
     * @param loc       location to center the effect around
     * @param type      type of particle to use
     * @param radius    radius of the sphere
     * @param amount    amount of particles to use
     */
    public static void fillSphere(Location loc, ParticleType type, int radius, int amount) {
        fillSphere(loc, type, 0, radius, amount);
    }

    /**
     * Randomly plays particle effects within the sphere
     *
     * @param loc       location to center the effect around
     * @param type      type of particle to use
     * @param data      data value to use
     * @param radius    radius of the sphere
     * @param amount    amount of particles to use
     */
    public static void fillSphere(Location loc, ParticleType type, int data, int radius, int amount) {
        Location temp = loc.clone();
        int rSquared = radius * radius;
        int twoRadius = radius * 2;
        int index = 0;

        // Play the particles
        while (index < amount) {
            temp.setX(loc.getX() + random.nextFloat() * twoRadius - radius);
            temp.setY(loc.getY() + random.nextFloat() * twoRadius - radius);
            temp.setZ(loc.getZ() + random.nextFloat() * twoRadius - radius);

            if (temp.distanceSquared(loc) > rSquared) continue;

            if (!OTHER_VALUES.containsKey(data) && type == ParticleType.OTHER) data = 0;
            if (type == ParticleType.OTHER) send(OTHER_VALUES.get(data), loc, 25);
            else loc.getWorld().playEffect(temp, type.getEffect(), data);
            index++;
        }
    }

    /**
     * Randomly plays particle effects within a hemisphere
     *
     * @param loc    location to center the effect around
     * @param type   type of particle to use
     * @param radius radius of the hemisphere
     * @param amount amount of particles to use
     */
    public static void fillHemisphere(Location loc, ParticleType type, int radius, int amount) {
        fillHemisphere(loc, type, 0, radius, amount);
    }

    /**
     * Randomly plays particle effects within a hemisphere
     *
     * @param loc    location to center the effect around
     * @param type   type of particle to use
     * @param data   data value to use
     * @param radius radius of the hemisphere
     * @param amount amount of particles to use
     */
    public static void fillHemisphere(Location loc, ParticleType type, int data, int radius, int amount) {
        Location temp = loc.clone();
        int rSquared = radius * radius;
        int twoRadius = radius * 2;
        int index = 0;

        // Play the particles
        while (index < amount) {
            temp.setX(loc.getX() + random.nextFloat() * twoRadius - radius);
            temp.setY(loc.getY() + random.nextFloat() * radius);
            temp.setZ(loc.getZ() + random.nextFloat() * twoRadius - radius);

            if (temp.distanceSquared(loc) > rSquared) continue;

            if (!OTHER_VALUES.containsKey(data) && type == ParticleType.OTHER) data = 0;
            if (type == ParticleType.OTHER) send(OTHER_VALUES.get(data), loc, 25);
            else loc.getWorld().playEffect(temp, type.getEffect(), data);
            index++;
        }
    }

    private static Class<?> packetClass;
    private static Object packet;

    public static void initialize() {
        packetClass = getClass("Packet");
        packet = getInstance(getClass("Packet63WorldParticles"));
        if (packet == null) {
            packet = getInstance(getClass("PacketPlayOutWorldParticles"));
        }
        setValue(packet, "e", 0.0f);
        setValue(packet, "f", 0.0f);
        setValue(packet, "g", 0.0f);
        setValue(packet, "h", 1.0f);
        setValue(packet, "i", 1);
    }

    /**
     * Sends the particle to all players within a radius of the location
     *
     * @param loc    location to play at
     * @param radius radius of the effect
     */
    private static void send(String particle, Location loc, int radius) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getWorld() == loc.getWorld() && player.getLocation().distanceSquared(loc) < radius * radius) {
                sendToPlayer(particle, player, loc);
            }
        }
    }

    /**
     * Sends the particle to the player at the given location
     *
     * @param player   player to send to
     * @param location location of the particle
     */
    private static void sendToPlayer(String particle, Player player, Location location) {
        if (packet == null) return;
        setValue(packet, "a", particle);
        setValue(packet, "b", (float) location.getX());
        setValue(packet, "c", (float) location.getY());
        setValue(packet, "d", (float) location.getZ());
        sendPacket(player, packet);
    }

    /**
     * Retrieves a class by name
     *
     * @param name name of the class
     * @return     class object or null if invalid
     */
    private static Class<?> getClass(String name) {
        try {
            return Class.forName(VERSION + name);
        }
        catch (Exception ex) {
            return null;
        }
    }

    /**
     * Gets an instance of the class
     *
     * @param c    class to get an instance of
     * @param args constructor arguments
     * @return     instance of the class or null if unable to create the object
     */
    private static Object getInstance(Class<?> c, Object ... args) {
        try {
            for (Constructor<?> constructor : c.getDeclaredConstructors()) {
                if (constructor.getGenericParameterTypes().length == args.length) {
                    return constructor.newInstance(args);
                }
            }
        }
        catch (Exception ex) { /* */ }
        return null;
    }

    /**
     * Tries to send a packet to the player
     *
     * @param player player to send to
     * @param packet packet to send
     */
    private static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object connection = handle.getClass().getField("playerConnection").get(handle);
            connection.getClass().getMethod("sendPacket", packetClass).invoke(connection, packet);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Tries to set a value for the object
     *
     * @param o         object reference
     * @param fieldName name of the field to set
     * @param value     value to set
     */
    private static void setValue(Object o, String fieldName, Object value) {
        try {
            Field field = o.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(o, value);
        }
        catch (Exception ex) { /* Do Nothing */ }
    }

    private static final HashMap<Integer, String> OTHER_VALUES = new HashMap<Integer, String>() {{
        put(0, "angryVillager");
        put(1, "bubble");
        put(2, "cloud");
        put(3, "crit");
        put(4, "deathSuspend");
        put(5, "dripLava");
        put(6, "dripWater");
        put(7, "enchantmenttable");
        put(8, "explode");
        put(9, "fireworksSpark");
        put(10, "flame");
        put(11, "footstep");
        put(12, "happyVillager");
        put(13, "heart");
        put(14, "hugeexplosion");
        put(15, "iconcrack_");
        put(16, "instantSpell");
        put(17, "largeexplode");
        put(18, "largesmoke");
        put(19, "lava");
        put(20, "magicCrit");
        put(21, "mobSpell");
        put(22, "mobSpellAmbient");
        put(23, "note");
        put(24, "portal");
        put(25, "reddust");
        put(26, "slime");
        put(27, "snowballpoof");
        put(28, "snowshovel");
        put(29, "spell");
        put(30, "splash");
        put(31, "suspend");
        put(32, "tilecrack_");
        put(33, "townaura");
        put(34, "witchMagic");
    }};
}
