package com.sucy.skill.manager;

import com.rit.sucy.chat.Chat;
import com.rit.sucy.chat.Prefix;
import com.rit.sucy.scoreboard.BoardManager;
import com.rit.sucy.scoreboard.StatBoard;
import com.rit.sucy.scoreboard.Team;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.data.PlayerStats;
import org.bukkit.ChatColor;

/**
 * Manages prefixes for classes
 * - Only works if ChatAPI is installed -
 */
public class ClassBoardManager
{
    /**
     * Clears a class prefix
     *
     * @param player player name
     *
     * @deprecated use clearPrefix(VersionPlayer) instead
     */
    public static void clear(String player)
    {
        clear(new VersionPlayer(player));
    }

    /**
     * Clears the prefix for a player
     *
     * @param player player reference
     */
    public static void clear(VersionPlayer player)
    {
        Chat.getPlayerData(player.getName()).clearPluginPrefix("SkillAPI");
        BoardManager.getPlayerBoards(player.getName()).removeBoards("SkillAPI");
        BoardManager.clearTeam(player.getName());
        BoardManager.clearScore(player.getName());
    }

    /**
     * Clears all scoreboards for the plugin
     */
    public static void clearAll()
    {
        BoardManager.clearPluginBoards("SkillAPI");
    }

    /**
     * Updates scoreboard information for the player data
     *
     * @param player     player name
     * @param prefix     prefix text
     * @param braceColor color of braces
     */
    public static void update(PlayerData player, String prefix, ChatColor braceColor)
    {

        // Give a chat prefix
        Chat.getPlayerData(player.getPlayerName()).setPluginPrefix(
                new Prefix("SkillAPI", prefix, braceColor)
        );

        // Clear previous data
        BoardManager.getPlayerBoards(player.getPlayerName()).removeBoards("SkillAPI");
        BoardManager.clearTeam(player.getPlayerName());

        // Apply new data
        if (SkillAPI.getSettings().isShowScoreboard())
        {
            StatBoard board = new StatBoard(player.getMainClass().getData().getPrefix(), "SkillAPI");
            board.addStats(new PlayerStats(player));
            BoardManager.getPlayerBoards(player.getPlayerName()).addBoard(board);
        }
        if (SkillAPI.getSettings().isShowClassName())
        {
            BoardManager.setTeam(player.getPlayerName(), player.getMainClass().getData().getName());
        }
        if (SkillAPI.getSettings().isShowClassLevel())
        {
            BoardManager.setBelowNameScore(player.getPlayerName(), player.getMainClass().getLevel());
        }
    }

    /**
     * Registers a class with the MCCore scoreboards
     *
     * @param c class to register
     */
    public static void registerClass(RPGClass c)
    {
        if (SkillAPI.getSettings().isShowClassName())
        {
            BoardManager.registerTeam(new Team(c.getName(), c.getPrefix() + ChatColor.RESET + " ", null));
        }
    }

    /**
     * Updates the player's level in the scoreboards
     *
     * @param data player's data to use for the update
     */
    public static void updateLevel(PlayerData data)
    {
        if (SkillAPI.getSettings().isShowClassLevel() && data.hasClass())
        {
            BoardManager.setBelowNameScore(data.getPlayerName(), data.getMainClass().getLevel());
        }
    }

    /**
     * Registers the text below player names
     */
    public static void registerText()
    {
        if (SkillAPI.getSettings().isShowClassLevel())
        {
            BoardManager.setTextBelowNames("Level");
        }
    }
}
