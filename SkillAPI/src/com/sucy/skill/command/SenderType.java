package com.sucy.skill.command;

/**
 * <p>Type of sender that a command requires</p>
 * <p>For use with CommandHandler and ICommand</p>
 */
public enum SenderType {

    /**
     * Only players can use the command
     */
    PLAYER_ONLY,

    /**
     * Only the console can use the command
     */
    CONSOLE_ONLY,

    /**
     * Anyone can send the command, player or console
     */
    ANYONE
}
