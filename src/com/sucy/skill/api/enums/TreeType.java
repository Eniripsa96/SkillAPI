package com.sucy.skill.api.enums;

/**
 * <p>Types of skill tree arrangements that can be used in the configuration.</p>
 */
public enum TreeType
{
    /**
     * A basic arrangement that puts base skills on the left, and the skills
     * that require those directly to the right of them.
     */
    BASIC_HORIZONTAL("BasicHorizontal"),

    /**
     * A basic arrangement that puts base skills at the top, and the
     * skills that require those directly below them.
     */
    BASIC_VERTICAL("BasicVertical"),

    /**
     * Arranges skills by their initial level requirement, putting
     * lowest level skills to the left and higher level skills to the right.
     */
    LEVEL_HORIZONTAL("LevelHorizontal"),

    /**
     * Arranges skills by their initial level requirement, putting
     * lowest level skills at the top and higher level skills at the bottom.
     */
    LEVEL_VERTICAL("LevelVertical"),

    /**
     * Arranges basic skills not needed by other skills on the left hand side,
     * other basic skills at the top right, and the skills that require those
     * below them.
     */
    REQUIREMENT("Requirement"),;

    /**
     * Key for the skill tree arrangement used in the configuration
     */
    private String key;

    /**
     * Enum constructor
     *
     * @param key skill tree arrangement configuration key
     */
    private TreeType(String key)
    {
        this.key = key;
    }

    /**
     * Retrieves the configuration key for the skill tree arrangement
     *
     * @return configuration key for the skill tree arrangement
     */
    public String getKey()
    {
        return key;
    }
}
