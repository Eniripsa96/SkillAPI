package com.sucy.skill.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * SkillAPI © 2018
 * com.sucy.skill.api.event.KeyPressEvent
 */
public class KeyPressEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Key    key;

    public KeyPressEvent(final Player player, final Key key) {
        this.player = player;
        this.key = key;
    }

    public Player getPlayer() {
        return player;
    }

    public Key getKey() {
        return key;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum Key {
        LEFT, RIGHT, Q
    }
}
