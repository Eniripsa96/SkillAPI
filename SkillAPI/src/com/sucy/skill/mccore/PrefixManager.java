package com.sucy.skill.mccore;

import com.rit.sucy.chat.Chat;
import com.rit.sucy.chat.Prefix;
import com.rit.sucy.scoreboard.BoardManager;
import com.rit.sucy.scoreboard.StatBoard;
import com.rit.sucy.scoreboard.Team;
import com.sucy.skill.api.CustomClass;
import com.sucy.skill.api.PlayerSkills;
import org.bukkit.ChatColor;

/**
 * Manages prefixes for classes
 * - Only works if ChatAPI is installed -
 */
public class PrefixManager {

    public static boolean showSidebar;
    public static  boolean showClasses;
    public static boolean showLevels;

    /**
     * Clears a class prefix
     *
     * @param player player name
     */
    public static void clearPrefix(String player) {
        Chat.getPlayerData(player).clearPluginPrefix("SkillAPI");
        BoardManager.getPlayerBoards(player).removeBoards("SkillAPI");
        BoardManager.clearTeam(player);
        BoardManager.clearScore(player);
    }

    /**
     * Clears all scoreboards for the plugin
     */
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

        // Give a chat prefix
        Chat.getPlayerData(player.getName()).setPluginPrefix(
                new Prefix("SkillAPI", prefix, braceColor)
        );

        // Clear previous data
        BoardManager.getPlayerBoards(player.getName()).removeBoards("SkillAPI");
        BoardManager.clearTeam(player.getName());

        // Apply new data
        if (showSidebar) {
            StatBoard board = new StatBoard(player.getPrefix(), "SkillAPI");
            board.addStats(new PlayerStats(player));
            BoardManager.getPlayerBoards(player.getName()).addBoard(board);
        }
        if (showClasses) BoardManager.setTeam(player.getName(), player.getClassName());
        if (showLevels) BoardManager.setBelowNameScore(player.getName(), player.getLevel());
    }

    /**
     * Registers a class with the MCCore scoreboards
     *
     * @param c class to register
     */
    public static void registerClass(CustomClass c) {
        if (showClasses) BoardManager.registerTeam(new Team(c.getName(), c.getPrefix() + ChatColor.RESET + " ", null));
    }

    /**
     * Updates the player's level in the scoreboards
     *
     * @param data player's data to use for the update
     */
    public static void updateLevel(PlayerSkills data) {
        if (showLevels) BoardManager.setBelowNameScore(data.getName(), data.getLevel());
    }

    /**
     * Registers the text below player names
     */
    public static void registerText(String text) {
        if (showLevels) BoardManager.setTextBelowNames("Level");
    }
}
