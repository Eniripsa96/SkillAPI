package com.sucy.skill.api.event;

import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player initially unlocks a skill
 */
public class PlayerSkillUnlockEvent extends Event
{

    private static final HandlerList handlers = new HandlerList();
    private PlayerData  player;
    private PlayerSkill unlockedSkill;

    /**
     * Constructor
     *
     * @param playerData    data of the player unlocking the skill
     * @param unlockedSkill the skill that was unlocked
     */
    public PlayerSkillUnlockEvent(PlayerData playerData, PlayerSkill unlockedSkill)
    {
        this.player = playerData;
        this.unlockedSkill = unlockedSkill;
    }

    /**
     * @return data of the player unlocking the skill
     */
    public PlayerData getPlayerData()
    {
        return player;
    }

    /**
     * @return skill that was unlocked
     */
    public PlayerSkill getUnlockedSkill()
    {
        return unlockedSkill;
    }

    /**
     * @return gets the handlers for the event
     */
    public HandlerList getHandlers()
    {
        return handlers;
    }

    /**
     * @return gets the handlers for the event
     */
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
