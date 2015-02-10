package com.sucy.skill.api.event;

import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player upgrades a skill
 */
public class PlayerSkillUpgradeEvent extends Event implements Cancellable
{

    private static final HandlerList handlers = new HandlerList();
    private PlayerData  player;
    private PlayerSkill upgradedSkill;
    private boolean cancelled = false;
    private int cost;

    /**
     * Constructor
     *
     * @param playerData    data of the player upgrading the skill
     * @param upgradedSkill the skill that was upgraded
     * @param cost          the cost for the upgrade
     */
    public PlayerSkillUpgradeEvent(PlayerData playerData, PlayerSkill upgradedSkill, int cost)
    {
        this.player = playerData;
        this.upgradedSkill = upgradedSkill;
        this.cost = cost;
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
    public PlayerSkill getUpgradedSkill()
    {
        return upgradedSkill;
    }

    /**
     * @return cost of the upgrade
     */
    public int getCost()
    {
        return cost;
    }

    /**
     * @return true if cancelled, false otherwise
     */
    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    /**
     * Sets whether or not the event is cancelled
     *
     * @param value true if cancelled, false otherwise
     */
    @Override
    public void setCancelled(boolean value)
    {
        cancelled = value;
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
