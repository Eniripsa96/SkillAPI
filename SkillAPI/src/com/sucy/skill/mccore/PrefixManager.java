package com.sucy.skill.mccore;

import com.rit.sucy.chat.Chat;
import com.rit.sucy.chat.Prefix;
import com.rit.sucy.scoreboard.BoardManager;
import com.rit.sucy.scoreboard.StatBoard;
import com.sucy.skill.api.PlayerSkills;
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
        Chat.getPlayerData(player).clearPluginPrefix("SkillAPI");
        BoardManager.getPlayerBoards(player).removeBoards("SkillAPI");
    }

    public static void clearAll() {
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
        Chat.getPlayerData(player.getName()).setPluginPrefix(
                new Prefix("SkillAPI", prefix, braceColor)
        );
        BoardManager.getPlayerBoards(player.getName()).removeBoards("SkillAPI");
        StatBoard board = new StatBoard(player.getPrefix(), "SkillAPI");
        board.addStats(new PlayerStats(player));
        BoardManager.getPlayerBoards(player.getName()).addBoard(board);
    }
}
