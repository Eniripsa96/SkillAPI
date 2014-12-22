package com.sucy.skill.api.enums;

/**
 * <p>A collection of reasons why a player would gain experience</p>
 * <p>This is used when gaining experience to determine where it came
 * from so some effects can act differently accordingly.</p>
 */
public enum ExpSource
{
    /**
     * Experience resulted from defeating a monster
     */
    MOB(0x1),

    /**
     * Experience resulted from breaking a block
     */
    BLOCK_BREAK(0x10),

    /**
     * Experience resulted from placing a block
     */
    BLOCK_PLACE(0x100),

    /**
     * Experience resulted from crafting an item
     */
    CRAFT(0x1000),

    /**
     * Experience resulted from an issued command
     */
    COMMAND(0x10000),

    /**
     * Experience resulted from an unspecified reason
     */
    SPECIAL(0x100000),

    /**
     * Experience resulted from initializing the player data on startup
     */
    INITIALIZATION(0x1000000);

    /**
     * The ID of the experience source which should be a unique power of 2 (or bit value)
     */
    private int id;

    /**
     * Enum constructor
     *
     * @param id ID of the experience source (should use a unique bit)
     */
    private ExpSource(int id)
    {
        this.id = id;
    }

    /**
     * <p>Retrieves the ID of the experience source.</p>
     *
     * @return ID of the experience source
     */
    public int getId()
    {
        return id;
    }
}
