package com.sucy.skill.api.skill;

import com.sucy.skill.api.Attributed;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * <p>Base class for skills</p>
 * <p>If the skill uses events to function, simply implement Listener
 * and write the methods with EventHandler. The API will automatically
 * register the events for you.</p>
 * <p>For a detailed tutorial on how to use this class, visit
 * <a href="http://dev.bukkit.org/bukkit-plugins/skillapi/pages/skill-tutorial/"/></p>
 */
public abstract class ClassSkill extends Attributed {

    private final SkillType type;
    private final String name;
    private Material indicator;
    private String skillReq;
    private int skillReqLevel;
    private int maxLevel;

    protected final ArrayList<String> description = new ArrayList<String>();

    /**
     * Constructor
     *
     * @param name      skill name
     * @param type      skill type
     * @param indicator skill tree indicator
     * @param maxLevel  maximum skill level
     */
    public ClassSkill(String name, SkillType type, Material indicator, int maxLevel) {
        this(name, type, indicator, maxLevel, null, 0);
    }

    /**
     * Constructor
     *
     * @param name      skill name
     * @param type      skill type
     * @param indicator skill tree indicator
     * @param maxLevel  maximum skill level
     * @param skillReq  skill required before this one
     */
    public ClassSkill(String name, SkillType type, Material indicator, int maxLevel, String skillReq, int skillReqLevel) {
        this.type = type;
        this.name = name;
        this.indicator = indicator;
        this.maxLevel = maxLevel;
        this.skillReq = skillReq;
        this.skillReqLevel = skillReqLevel;
    }

    /**
     * @return skill name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the maximum level of the skill
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * @return the material used to represent the skill in skill trees
     */
    public Material getIndicator() {
        return indicator;
    }

    /**
     * @return the type of skill
     */
    public SkillType getType() {
        return type;
    }

    /**
     * @return skill required to have to get this one
     */
    public String getSkillReq() {
        return skillReq;
    }

    /**
     * @return Level of the required skill that is needed
     */
    public int getSkillReqLevel() {
        return skillReqLevel;
    }

    /**
     * @return the description lore lines for the indicator
     */
    public List<String> getDescription() {
        return description;
    }
}
