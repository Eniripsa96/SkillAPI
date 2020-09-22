package com.sucy.skill.hook;

import org.bukkit.entity.Entity;

/**
 * Handles checking whether or not an entity is an NPC
 */
public class CitizensHook {
    public static boolean isNPC(Entity entity) {
        return entity.getClass().getName().equals("PlayerNPC");
    }
}
