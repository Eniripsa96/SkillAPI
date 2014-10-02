package com.sucy.skill.api.event;

import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCastSkillEvent extends Event implements Cancellable
{

    private static final HandlerList handlers = new HandlerList();
    private PlayerData  playerData;
    private PlayerSkill skill;
    private Player      player;
    private boolean     cancelled;

    public PlayerCastSkillEvent(PlayerData playerData, PlayerSkill skill, Player player)
    {
        this.playerData = playerData;
        this.skill = skill;
        this.player = player;
        this.cancelled = false;
    }

    public Player getPlayer()
    {
        return player;
    }

    public PlayerData getPlayerData()
    {
        return playerData;
    }

    public PlayerSkill getSkill()
    {
        return skill;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
