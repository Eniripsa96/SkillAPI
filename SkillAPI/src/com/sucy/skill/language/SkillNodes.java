package com.sucy.skill.language;

/**
 * <p>Nodes for the language file pertaining to the skill tree</p>
 * <p>This is primarily for the API retrieving config messages.
 * You shouldn't need to use these values at all.</p>
 */
public class SkillNodes {

    public static final String

        /**
         * Base node of the skill tree nodes
         */
        BASE = "Skill Tree.",

        /**
         * Title for a skill display
         */
        TITLE = BASE + "title",

        /**
         * Type of the skill
         */
        TYPE = BASE + "type",

        /**
         * Requirement of a skill
         */
        REQUIREMENT_BASE = BASE + "requirement.",

        /**
         * When a requirement is met
         */
        REQUIREMENT_MET = REQUIREMENT_BASE + "met",

        /**
         * When a requirement is not met
         */
        REQUIREMENT_NOT_MET = REQUIREMENT_BASE + "not-met",

        /**
         * Attribute of a skill
         */
        ATTRIBUTE_BASE = BASE + "attribute.",

        /**
         * When an attribute is increasing
         */
        ATTRIBUTE_CHANGING = ATTRIBUTE_BASE + "changing",

        /**
         * When an attribute is decreasing
         */
        ATTRIBUTE_NOT_CHANGING = ATTRIBUTE_BASE + "not-changing",

        /**
         * No attributes present
         */
        ATTRIBUTE_NONE = ATTRIBUTE_BASE + "no-attributes",

        /**
         * Description of a skill
         */
        DESCRIPTION_BASE = BASE + "description.",

        /**
         * First line of a description
         */
        DESCRIPTION_FIRST = DESCRIPTION_BASE + "first",

        /**
         * Other lines of a description
         */
        DESCRIPTION_OTHER = DESCRIPTION_BASE + "other",

        /**
         * No description present
         */
        DESCRIPTION_NONE = DESCRIPTION_BASE + "no-description",

        /**
         * Layout for the skill display
         */
        LAYOUT = BASE + "layout";
}
