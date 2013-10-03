package com.sucy.skill.config;

/**
 * <p>Paths used to store data for skills</p>
 * <p>This is primarily for the API saving data. You shouldn't need
 * to use these values at all.</p>
 */
public enum SkillValues {

    /**
     * Root path for skills
     */
    ROOT ("skills"),

    /**
     * Maximum skill level
     */
    MAX_LEVEL ("max-level"),

    /**
     * Skill prerequisite
     */
    SKILL_REQ ("skill-req"),

    /**
     * Item type for representation in skill trees
     */
    INDICATOR ("indicator"),

    /**
     * Description for use in skill trees
     */
    DESCRIPTION ("description"),

    /**
     * Cooldown for the skill
     */
    COOLDOWN ("Cooldown"),

    /**
     * Mana cost for the skill
     */
    MANA ("Mana"),

    /**
     * Level requirement
     */
    LEVEL ("Level"),

    /**
     * Point cost to unlock
     */
    COST ("Cost"),
    ;

    private final String key;

    /**
     * Private constructor
     *
     * @param key key for the value
     */
    private SkillValues(String key) {
        this.key = key;
    }

    /**
     * @return config key
     */
    public String getKey() {
        return key;
    }
}
