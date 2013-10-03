package com.sucy.skill.api.util;

import com.sucy.skill.api.Attributed;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.TargetSkill;
import com.sucy.skill.config.SkillValues;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Helper class for accessing attribute data of skills</p>
 */
public class AttributeHelper {

    /**
     * <p>Calculates a value for an attribute at a given level for an attributed object</p>
     * <p>You will most often use this for your skills when applying skill effects. Using
     * this on your custom attributes will retrieve your attributes at the proper values
     * according to the current skill level.</p>
     *
     * @param attributed attributed object to calculate for
     * @param attribute  attribute name
     * @param level      level of the skill
     * @return           attribute value
     */
    public static int calculate(Attributed attributed, String attribute, int level) {
        return attributed.getBase(attribute) + attributed.getScale(attribute) * (level - 1);
    }

    /**
     * <p>Retrieves the list of attributes for a skill</p>
     * <p>If default attributes are missing, then they are added
     * with a default value of 0.</p>
     *
     * @param skill skill to get the attributes of
     * @return      attribute values
     */
    public static List<String> getAllAttributes(ClassSkill skill) {
        List<String> attributes = skill.getAttributeNames();
        if (attributes == null) attributes = new ArrayList<String>();
        if (!attributes.contains(SkillValues.MANA.getKey())
                && (skill instanceof SkillShot || skill instanceof TargetSkill))
            attributes.add(0, SkillValues.MANA.getKey());
        if (!attributes.contains(SkillValues.COOLDOWN.getKey())
                && (skill instanceof SkillShot || skill instanceof TargetSkill))
            attributes.add(0, SkillValues.COOLDOWN.getKey());
        if (!attributes.contains(SkillValues.COST.getKey())) attributes.add(0, SkillValues.COST.getKey());
        if (!attributes.contains(SkillValues.LEVEL.getKey())) attributes.add(0, SkillValues.LEVEL.getKey());
        return attributes;
    }
}
