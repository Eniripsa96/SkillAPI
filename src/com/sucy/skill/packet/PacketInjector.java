package com.sucy.skill.packet;

import com.rit.sucy.reflect.Reflection;
import com.sucy.skill.SkillAPI;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * SkillAPI © 2018
 * com.sucy.skill.PacketInjector
 */
public class PacketInjector {
    private Field    playerCon;
    private Field    network;
    private Method   handle;
    private Field    k;
    private Field    dropField;

    private SkillAPI skillAPI;

    /**
     * Sets up the injector, grabbing necessary reflection data
     */
    public PacketInjector(final SkillAPI skillAPI) {
        this.skillAPI = skillAPI;

        try {
            String nms = Reflection.getNMSPackage();
            playerCon = Class.forName(nms + "EntityPlayer")
                    .getField("playerConnection");

            Class<?> playerConnection = Class.forName(nms + "PlayerConnection");
            network = playerConnection.getField("networkManager");

            Class<?> networkManager = Class.forName(nms + "NetworkManager");
            try {
                k = networkManager.getField("channel");
            } catch (Exception ex) {
                k = networkManager.getDeclaredField("i");
                k.setAccessible(true);
            }

            handle = Class.forName(Reflection.getCraftPackage() + "entity.CraftPlayer").getMethod("getHandle");
        } catch (Throwable t) {
            this.error();
            t.printStackTrace();
        }

        try {
            dropField = Reflection.getNMSClass("PacketPlayInBlockDig").getDeclaredField("c");
            dropField.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isWorking() {
        return handle != null;
    }

    private void error() {
        skillAPI.getLogger().warning("Failed to set up packet listener - some click combos may not behave properly");
    }

    /**
     * Injects an interceptor to the player's network manager
     *
     * @param p player to add to
     */
    public void addPlayer(Player p) {
        if (handle == null) return;

        try {
            Channel ch = getChannel(p);
            if (ch.pipeline().get("PacketInjector") == null) {
                PacketHandlerV1_13 h = new PacketHandlerV1_13(p, dropField);
                ch.pipeline().addBefore("packet_handler", "PacketInjector", h);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Removes an interruptor from a player's network manager
     *
     * @param p player to remove from
     */
    public void removePlayer(Player p) {
        if (handle == null) return;

        try {
            Channel ch = getChannel(p);
            if (ch.pipeline().get("PacketInjector") != null) {
                ch.pipeline().remove("PacketInjector");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Gets the channel used by a player's network manager
     *
     * @return retrieved channel
     */
    private Channel getChannel(final Player player) throws IllegalAccessException, InvocationTargetException {
        return (Channel) k.get(network.get(playerCon.get(handle.invoke(player))));
    }
}
