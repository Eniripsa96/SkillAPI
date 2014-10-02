package com.sucy.skill.api.enums;

import com.sucy.skill.language.StatusNodes;

/**
 * Statuses able to be applied to a player
 */
public enum Status
{

    /**
     * Unable to move, cast skills, or deal auto-attack damage
     */
    STUN(StatusNodes.STUNNED),

    /**
     * Unable to move
     */
    ROOT(StatusNodes.ROOTED),

    /**
     * Unable to cast skills
     */
    SILENCE(StatusNodes.SILENCED),

    /**
     * Unable to deal auto-attack damage
     */
    DISARM(StatusNodes.DISARMED),

    /**
     * Healing received damages instead
     */
    CURSE(null),

    /**
     * Damage taken heals instead
     */
    ABSORB(null),

    /**
     * Nullify damage taken
     */
    INVINCIBLE(null),;

    private final String messageNode;

    private Status(String messageNode)
    {
        this.messageNode = messageNode;
    }

    /**
     * <p>Retrieves the language node that is displayed when a player is
     * restricted due to the status.</p>
     *
     * @return the language node for the status
     */
    public String getMessageNode()
    {
        return messageNode;
    }

    /**
     * <p>Checks whether or not this status has a message attached to
     * it for when a player is restricted by the status.</p>
     *
     * @return true if it has a message, false otherwise
     */
    public boolean hasMessageNode()
    {
        return messageNode != null;
    }
}
