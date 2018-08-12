/**
 * Minenotch ©
 * com.minenotch.nms.PacketHandler
 */
package com.sucy.skill.packet;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.KeyPressEvent;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

/**
 * Handles the interception of packets, applying effects where necessary
 */
class PacketHandlerV1_13 extends ChannelDuplexHandler {
    private Player player;
    private Field  dropField;

    PacketHandlerV1_13(final Player player, final Field dropField) {
        this.player = player;
        this.dropField = dropField;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext c, Object m) throws Exception {
        switch (m.getClass().getSimpleName()) {
            case "PacketPlayInBlockDig":
                if (dropField != null){
                    if (((Enum<?>) dropField.get(m)).name().equals("DROP_ITEM")) {
                        callEvent(KeyPressEvent.Key.Q);
                    }
                }
                break;
            case "PacketPlayInArmAnimation":
                callEvent(KeyPressEvent.Key.LEFT);
                break;
            case "PacketPlayInUseItem":
            case "PacketPlayInBlockPlace":
                callEvent(KeyPressEvent.Key.RIGHT);
        }
        super.channelRead(c, m);
    }

    private void callEvent(final KeyPressEvent.Key key) {
        SkillAPI.schedule(() -> Bukkit.getPluginManager().callEvent(new KeyPressEvent(player, key)), 0);
    }
}
