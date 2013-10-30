package com.sucy.skill.tree;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.CustomClass;
import com.sucy.skill.api.SkillTreeException;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;

import java.util.*;

/**
 * <p>Root class for tree implementations based on levels</p>
 * <p>This is still in development to make it work as intended</p>
 *
 */
public abstract class LevelTree extends SkillTree {

    /**
     * Constructor
     *
     * @param api  api reference
     * @param tree class reference
     */
    public LevelTree(SkillAPI api, CustomClass tree) {
        super(api, tree);
    }

    /**
     * Arranges the skill tree
     *
     * @param skills skills to arrange
     * @throws com.sucy.skill.api.SkillTreeException
     */
    @Override
    protected void arrange(List<ClassSkill> skills) throws SkillTreeException {

        // Get the max level
        int maxLevel = 1;
        for (ClassSkill skill : skills) {
            if (skill.getMaxLevel() > maxLevel) maxLevel = skill.getMaxLevel();
        }

        // Break it up into tiers
        int scale = (maxLevel + getPerTierLimit() - 1) / getPerTierLimit();
        Collections.sort(skills, comparator);
        HashMap<Integer, List<ClassSkill>> tiers = new HashMap<Integer, List<ClassSkill>>();
        int tier = 0;
        while (skills.size() > 0) {
            List<ClassSkill> list = new ArrayList<ClassSkill>();
            tiers.put(tier++, list);
            int max = tier * scale;
            int count = 0;

            while (skills.size() > 0 && count++ < getPerTierLimit() && skills.get(0).getMaxLevel() <= max) {
                list.add(skills.remove(0));
            }
        }

        // Arrange the tree
        for (int i = 0; i < tier; i++) {
            List<ClassSkill> list = tiers.get(i);
            for (int j = 0; j < list.size(); j++) {
            }
        }
    }

    /**
     * @return maximum number of skills per tier is allowed
     */
    protected abstract int getPerTierLimit();

    /**
     * @return maximum number of tiers allowed
     */
    protected int getTierLimit() {
        return 15 - getPerTierLimit();
    }

    /**
     * Comparator for skills for level trees
     */
    private static final Comparator<ClassSkill> levelComparator = new Comparator<ClassSkill>() {

        /**
         * Compares skills based on their stats for skill tree arrangement
         *  -> Skills with lower level requirements come first
         *  -> Then its skills with lower costs
         *  -> Then its skills alphabetically
         *
         * @param skill1 skill being compared
         * @param skill2 skill to compare to
         * @return      -1, 0, or 1
         */
        @Override
        public int compare(ClassSkill skill1, ClassSkill skill2) {
            return skill1.getBase(SkillAttribute.LEVEL) > skill2.getBase(SkillAttribute.LEVEL) ? 1
                    : skill1.getBase(SkillAttribute.LEVEL) < skill2.getBase(SkillAttribute.LEVEL) ? -1
                    : skill1.getBase(SkillAttribute.COST) > skill2.getBase(SkillAttribute.COST) ? 1
                    : skill1.getBase(SkillAttribute.COST) < skill2.getBase(SkillAttribute.COST) ? -1
                    : skill1.getName().compareTo(skill2.getName());
        }
    };
}
