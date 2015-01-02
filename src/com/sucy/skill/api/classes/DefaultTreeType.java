package com.sucy.skill.api.classes;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.tree.*;

/**
 * Types of SkillTrees that are available for classes/skills to use
 */
public enum DefaultTreeType implements TreeType
{
    BASIC_HORIZONTAL,
    BASIC_VERTICAL,
    LEVEL_HORIZONTAL,
    LEVEL_VERTICAL,
    REQUIREMENT;

    /**
     * Retrieves the skill tree depending on the enum value
     *
     * @return skill tree instance
     */
    public SkillTree getTree(SkillAPI api, RPGClass parent)
    {
        switch (this)
        {
            case BASIC_HORIZONTAL:
                return new BasicHorizontalTree(api, parent);
            case BASIC_VERTICAL:
                return new BasicVerticalTree(api, parent);
            case LEVEL_HORIZONTAL:
                return new LevelHorizontalTree(api, parent);
            case LEVEL_VERTICAL:
                return new LevelVerticalTree(api, parent);
            case REQUIREMENT:
                return new RequirementTree(api, parent);
            default:
                return null;
        }
    }

    /**
     * Retrieves a tree type by enum value name
     *
     * @param name enum value name
     *
     * @return corresponding tree type or the default REQUIREMENT if invalid
     */
    public static DefaultTreeType getByName(String name)
    {
        try
        {
            return Enum.valueOf(DefaultTreeType.class, name.toUpperCase().replace(' ', '_'));
        }
        catch (Exception ex)
        {
            return REQUIREMENT;
        }
    }
}
