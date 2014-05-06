package com.sucy.skill.api.skill;

/**
 * <p>Types of skills for display in skill trees</p>
 */
public enum SkillType {

    /**
     * A single target skill shot
     */
    SKILL_SHOT ("skill-shot"),

    /**
     * A skill that imbues normal attacks with extra effects
     */
    ATTACK_IMBUEMENT ("attack-imbuement"),

    /**
     * A single target targeted skill
     */
    TARGET ("target"),

    /**
     * An area of effect skill shot
     */
    SKILL_SHOT_AOE ("skill-shot-aoe"),

    /**
     * An area of effect targeted skill
     */
    TARGET_AOE ("target-aoe"),

    /**
     * A passive skill
     */
    PASSIVE ("passive"),

    /**
     * An area target effect
     */
    AREA ("area"),

    /**
     * A self targeted skill
     */
    SELF ("self"),

    /**
     * A linear target skill
     */
    LINEAR ("linear"),

    /**
     * A cone target skill
     */
    CONE ("cone"),

    /**
     * A summoning skill
     */
    SUMMON ("summon")
    ;

    private final String name;

    /**
     * Enum constructor
     *
     * @param node language configuration node
     */
    private SkillType(String node) {
        this.name = node;
    }

    /**
     * @return language node for the skill type
     */
    public String getNode() {
        return "Skill Types." + name;
    }
}
