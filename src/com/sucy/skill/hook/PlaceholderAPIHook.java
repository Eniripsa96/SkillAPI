package com.sucy.skill.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

/**
 * SkillAPI © 2018
 * com.sucy.skill.hook.PlaceholderAPIHook
 */
public class PlaceholderAPIHook {

    public static String format(final String message, final Player player) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }
}
