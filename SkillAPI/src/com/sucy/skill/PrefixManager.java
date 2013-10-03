package com.sucy.skill;

import com.rit.sucy.chat.Chat;
import com.rit.sucy.chat.Prefix;
import com.rit.sucy.scoreboard.BoardManager;
import com.rit.sucy.scoreboard.StatBoard;
import com.sucy.skill.skills.PlayerSkills;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Manages prefixes for classes
 * - Only works if ChatAPI is installed -
 */
public class PrefixManager {

    /**
     * Clears a class prefix
     *
     * @param player player name
     */
    public static void clearPrefix(String player) {
        if (!isCoreEnabled()) return;

        Chat.getPlayerData(player).clearPluginPrefix("SkillAPI");
        BoardManager.getPlayerBoards(player).removeBoards("SkillAPI");
    }

    public static void clearAll() {
        if (!isCoreEnabled()) return;

        BoardManager.clearPluginBoards("SkillAPI");
    }

    /**
     * Sets a class prefix
     *
     * @param player     player name
     * @param prefix     prefix text
     * @param braceColor color of braces
     */
    public static void setPrefix(PlayerSkills player, String prefix, ChatColor braceColor) {
        if (!isCoreEnabled()) return;

        Chat.getPlayerData(player.getName()).setPluginPrefix(
                new Prefix("SkillAPI", prefix, braceColor)
        );
        BoardManager.getPlayerBoards(player.getName()).removeBoards("SkillAPI");
        StatBoard board = new StatBoard(player.getPrefix(), "SkillAPI");
        board.addStats(player);
        BoardManager.getPlayerBoards(player.getName()).addBoard(board);
    }

    /**
     * Checks if ChatAPI is installed
     *
     * @return true if installed, false otherwise
     */
    public static boolean isCoreEnabled() {
        return Bukkit.getPluginManager().getPlugin("MCCore") != null;
    }
}
